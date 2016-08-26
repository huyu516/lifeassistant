package com.hy.lifeassistant.dao;

import com.hy.lifeassistant.base.BaseDao;
import com.hy.lifeassistant.domain.CallRecord;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;

public class CallRecordDao extends BaseDao {

    public void addAll(List<CallRecord> callRecordList) {
        beginTransaction();

        for (CallRecord callRecord : callRecordList) {
            getRealm().copyToRealm(callRecord);
        }

        commitTransaction();
    }

    public long findLastDate() {
        RealmResults<CallRecord> result = getRealm().allObjects(CallRecord.class);
        return result.isEmpty() ? 0 : result.max("date").longValue();
    }

    public List<CallRecord> findFirst50() {
        String[] sortFields = {"date"};
        Sort[] sortOrders = {Sort.DESCENDING};

        List<CallRecord> callRecordList = getRealm().allObjectsSorted(CallRecord.class, sortFields, sortOrders);

        int end = Math.min(50, callRecordList.size());
        return callRecordList.subList(0, end);
    }

}
