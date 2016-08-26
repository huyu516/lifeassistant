package com.hy.lifeassistant.page.expense;

import android.support.v4.app.Fragment;

import com.hy.lifeassistant.base.BaseTab;

// 开支记录标签页
public class ExpenseTab extends BaseTab {

    @Override
    protected String[] getTitles() {
        return new String[]{"明细", "图表"};
    }

    @Override
    protected Fragment[] getFragments() {
        return new Fragment[]{new ExpenseList(), new ExpenseChart()};
    }

}
