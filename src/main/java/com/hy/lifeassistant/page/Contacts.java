package com.hy.lifeassistant.page;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.base.PhoneUtil;
import com.hy.lifeassistant.dao.CallRecordDao;
import com.hy.lifeassistant.domain.CallRecord;
import com.hy.lifeassistant.domain.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// 通讯录
public class Contacts extends Fragment implements View.OnClickListener {

    private CallRecordDao callRecordDao;

    private ListView lvContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        callRecordDao = new CallRecordDao();

        View view = inflater.inflate(R.layout.f_contact, container, false);

        lvContact = (ListView) view.findViewById(R.id.lvContact);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter adapter = new ContactAdapter(getContext(), R.layout.i_contact, findContacts());
        lvContact.setAdapter(adapter);
    }

    private List<Contact> findContacts() {
        List<Contact> result = new ArrayList<>();

        String[] fields = {Phone.NUMBER, Phone.DISPLAY_NAME, Phone.SORT_KEY_ALTERNATIVE};

        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fields, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
            String sortedKey = cursor.getString(cursor.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE));

            Contact contact = new Contact();
            contact.setName(name);
            contact.setPhone(phone);
            contact.setSortedKey(sortedKey);

            result.add(contact);
        }

        cursor.close();

        sortedContactListByCallTimes(result);

        return result;
    }

    private void sortedContactListByCallTimes(List<Contact> contactList) {
        Map<String, Contact> phoneToContactMap = new HashMap<>();

        for (Contact contact : contactList) {
            phoneToContactMap.put(contact.getPhone(), contact);
        }

        copyCallLogToRealm();

        List<CallRecord> callRecordList = callRecordDao.findFirst50();

        for (CallRecord callRecord : callRecordList) {
            String phone = callRecord.getNumber();

            Contact contact = phoneToContactMap.get(phone);

            if (contact != null) {
                int callTimes = contact.getCallTimes();
                contact.setCallTimes(++callTimes);
            }
        }

        Collections.sort(contactList);
    }

    private void copyCallLogToRealm() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String[] fields = {Calls.NUMBER, Calls.TYPE, Calls.DATE};

        // 只从ContentProvider中查询数据库中没有的数据
        long lastDate = callRecordDao.findLastDate();

        Cursor cursor = getContext().getContentResolver().query(Calls.CONTENT_URI,
                fields, Calls.DATE + ">" + lastDate, null, Calls.DEFAULT_SORT_ORDER);

        List<CallRecord> callRecordList = new LinkedList<>();

        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));

            int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));
            long date = cursor.getLong(cursor.getColumnIndex(Calls.DATE));

            CallRecord callRecord = new CallRecord();
            callRecord.setNumber(number);
            callRecord.setType(type);
            callRecord.setDate(date);

            callRecordList.add(callRecord);
        }

        callRecordDao.addAll(callRecordList);

        cursor.close();
    }

    @Override
    public void onClick(View v) {
        String phone = v.getTag().toString();
        PhoneUtil.callPhone(getContext(), phone);
    }

    class ContactAdapter extends ArrayAdapter<com.hy.lifeassistant.domain.Contact> {

        private int resourceId;

        public ContactAdapter(Context context, int textViewResourceId, List<com.hy.lifeassistant.domain.Contact> contactList) {
            super(context, textViewResourceId, contactList);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contact contact = getItem(position);

            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setText(contact.getName());

            TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
            tvPhone.setText(contact.getPhone());

            ImageButton btnCall = (ImageButton) view.findViewById(R.id.btnCall);
            btnCall.setTag(contact.getPhone());
            btnCall.setOnClickListener(Contacts.this);

            return view;
        }

    }

}


