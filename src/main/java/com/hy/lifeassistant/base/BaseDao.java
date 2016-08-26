package com.hy.lifeassistant.base;

import java.util.Collection;
import java.util.UUID;

import io.realm.Realm;

// DAO基类
public abstract class BaseDao {

    private static final int PAGE_SIZE = 15;

    protected Realm getRealm() {
        return Realm.getDefaultInstance();
    }

//    public void closeRealm() {
//        getRealm().close();
//    }

    protected void beginTransaction() {
        getRealm().beginTransaction();
    }

    protected void commitTransaction() {
        getRealm().commitTransaction();
    }

    protected int calcStart(int pageNO) {
        return (pageNO - 1) * PAGE_SIZE;
    }

    protected int calcEnd(int pageNo, Collection<?> datas) {
        int end = calcStart(pageNo) + PAGE_SIZE;
        return Math.min(end, datas.size());
    }

    protected boolean isPageNoUnValid(int pageNo, Collection<?> datas) {
        return calcStart(pageNo) >= datas.size() || datas.isEmpty();
    }

}
