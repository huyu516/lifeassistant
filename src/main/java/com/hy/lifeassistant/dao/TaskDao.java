package com.hy.lifeassistant.dao;

import com.hy.lifeassistant.base.BaseDao;
import com.hy.lifeassistant.domain.Task;

import java.util.Date;
import java.util.List;

import io.realm.Sort;

public class TaskDao extends BaseDao {

    public void add(Task task) {
        beginTransaction();
        getRealm().copyToRealm(task);
        commitTransaction();
    }

    public void finish(Task task) {
        beginTransaction();
        task.setIsFinished(true);
        task.setFinishTime(new Date());
        getRealm().copyToRealmOrUpdate(task);
        commitTransaction();
    }

    public Task findById(String id) {
        return getRealm().where(Task.class).equalTo("id", id).findFirst();
    }

    public void delete(Task task) {
        beginTransaction();
        task.removeFromRealm();
        commitTransaction();
    }

    public void deleteAllFinished() {
        beginTransaction();
        getRealm().where(Task.class).equalTo("isFinished", true).findAll().clear();
        commitTransaction();
    }

    public List<Task> findByFinishedAndPageNo(int pageNo) {
        String[] sortFields = {"finishTime"};
        Sort[] sortOrders = {Sort.DESCENDING};
        return findByIsFinishedAndPageNoWithSorts(true, pageNo, sortFields, sortOrders);
    }

    public List<Task> findByUnFinishedAndPageNo(int pageNo) {
        String[] sortFields = {"level", "startTime"};
        Sort[] sortOrders = {Sort.DESCENDING, Sort.ASCENDING};
        return findByIsFinishedAndPageNoWithSorts(false, pageNo, sortFields, sortOrders);
    }

    private List<Task> findByIsFinishedAndPageNoWithSorts(boolean isFinished, int pageNo,
                                                          String[] sortFields, Sort[] sortOrders) {

        List<Task> result = getRealm().where(Task.class)
                .equalTo("isFinished", isFinished)
                .findAllSorted(sortFields, sortOrders);

        if (isPageNoUnValid(pageNo, result)) {
            return null;
        }

        int start = calcStart(pageNo);
        int end = calcEnd(pageNo, result);
        return result.subList(start, end);
    }

}
