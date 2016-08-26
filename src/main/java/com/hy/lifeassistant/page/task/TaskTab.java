package com.hy.lifeassistant.page.task;

import android.support.v4.app.Fragment;

import com.hy.lifeassistant.base.BaseTab;

// 任务标签页
public class TaskTab extends BaseTab {

    @Override
    protected String[] getTitles() {
        return new String[]{"未完成", "已完成"};
    }

    @Override
    protected Fragment[] getFragments() {
        return new Fragment[]{new TaskUnFinishedList(), new TaskFinishedList()};
    }

}
