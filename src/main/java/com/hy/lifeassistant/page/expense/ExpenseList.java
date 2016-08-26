package com.hy.lifeassistant.page.expense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.base.CommonUtils;
import com.hy.lifeassistant.base.SimpleAdapterFactory;
import com.hy.lifeassistant.dao.ExpenseDao;
import com.hy.lifeassistant.domain.Expense;
import com.hy.lifeassistant.view.PageableListView;
import com.hy.lifeassistant.view.PageableListView.ListViewSupporter;

import java.util.Date;
import java.util.List;

// 开支记录列表
public class ExpenseList extends Fragment implements OnClickListener, ListViewSupporter<Expense> {

    private ExpenseDao expenseDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        expenseDao = new ExpenseDao();

        View view = inflater.inflate(R.layout.f_expense_list, container, false);

        PageableListView plvTasks = (PageableListView) view.findViewById(R.id.plvExpenses);
        plvTasks.setSupporter(this);

        Integer[] levelImgs = {R.drawable.food, R.drawable.candy, R.drawable.shopping};
        String[] levelNames = {"吃饭", "零食", "购物"};
        int[] resIds = {R.id.ivTaskLevel, R.id.tvTaskLevel};
        SimpleAdapter adapter = SimpleAdapterFactory.create(getContext(), R.layout.i_task_level, resIds, levelImgs, levelNames);

        Spinner spType = (Spinner) view.findViewById(R.id.spType);
        spType.setAdapter(adapter);
        spType.setSelection(1, true);

        Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                addExpense();
                break;
            case R.id.btnDel:
                Expense expense = (Expense) v.getTag();
                delExpense(expense);
                break;
        }
    }

    private void addExpense() {
        EditText etMoney = (EditText) getView().findViewById(R.id.etMoney);
        String smoney = etMoney.getText().toString();
        etMoney.setText("");

        if (smoney.isEmpty()) {
            return;
        }

        Float money = Float.parseFloat(smoney);

        if (Float.isNaN(money) || money <= 0 || money > 10000) {
            Toast.makeText(getContext(), "请输入合法的金额", Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner spType = (Spinner) getView().findViewById(R.id.spType);
        int type = spType.getSelectedItemPosition();

        Expense expense = new Expense();
        expense.setId(CommonUtils.getUUid());
        expense.setDate(new Date());
        expense.setType(type);
        expense.setMoney(money);

        expenseDao.add(expense);

        refresh();
    }

    private void delExpense(Expense expense) {
        expenseDao.delete(expense);

        refresh();
    }

    @Override
    public View getView(int position, Expense data, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.i_expense, null);

        ImageView ivType = (ImageView) view.findViewById(R.id.ivType);
        switch (data.getType()) {
            case 0:
                ivType.setImageResource(R.drawable.food);
                break;
            case 1:
                ivType.setImageResource(R.drawable.candy);
                break;
            case 2:
                ivType.setImageResource(R.drawable.shopping);
                break;
        }

        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        String date = CommonUtils.formatDateTime(data.getDate());
        tvDate.setText(date);

        TextView tvMoney = (TextView) view.findViewById(R.id.tvMoney);
        tvMoney.setText(CommonUtils.formatFloat(data.getMoney()));

        Button btnDel = (Button) view.findViewById(R.id.btnDel);
        btnDel.setTag(data);
        btnDel.setOnClickListener(this);

        return view;
    }

    @Override
    public List<Expense> fetchData(int pageNo) {
        return expenseDao.findByPageNo(pageNo);
    }

    private void refresh() {
        ExpenseTab expenseTab = (ExpenseTab) getParentFragment();
        expenseTab.refresh();
    }

}
