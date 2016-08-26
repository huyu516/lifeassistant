package com.hy.lifeassistant.dao;

import com.hy.lifeassistant.base.BaseDao;
import com.hy.lifeassistant.base.CommonUtils;
import com.hy.lifeassistant.domain.Expense;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Sort;

public class ExpenseDao extends BaseDao {

    public void add(Expense expense) {
        beginTransaction();
        getRealm().copyToRealm(expense);
        commitTransaction();
    }

    public void delete(Expense expense) {
        beginTransaction();
        expense.removeFromRealm();
        commitTransaction();
    }

    public List<Expense> findByPageNo(int pageNo) {
        String[] sortFields = {"date"};
        Sort[] sortOrders = {Sort.DESCENDING};

        List<Expense> result = getRealm().allObjectsSorted(Expense.class, sortFields, sortOrders);

        if (isPageNoUnValid(pageNo, result)) {
            return null;
        }

        int start = calcStart(pageNo);
        int end = calcEnd(pageNo, result);
        return result.subList(start, end);
    }

    public float[] findSummaryByLast7Days() {
        float[] result = new float[3];

        float[][] data = findForAnalysisByLast7Days();

        for (int i = 0; i < data.length; i++) {
            result[0] += data[i][0];
            result[1] += data[i][1];
            result[2] += data[i][2];
        }

        return result;
    }

    public float[][] findForAnalysisByLast7Days() {
        float[][] result = new float[7][3];

        Date now = new Date();

        List<Expense> expenseList = findByLast7Days();

        for (Expense expense : expenseList) {
            Date date = expense.getDate();
            int days = CommonUtils.daysBetween(date, now);

            result[7 - 1 - days][expense.getType()] += expense.getMoney();
        }

        return result;
    }

    public List<Expense> findByLast7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();

        List<Expense> result = getRealm().where(Expense.class).between("date", date, new Date()).findAll();

        return result;
    }

}
