package com.hy.lifeassistant.domain;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 通话记录
public class CallRecord extends RealmObject {

    @PrimaryKey
    private long date;       // 通话时间

    private String number;   // 对方号码

    private int type;        // !暂时无用

    public CallRecord() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

}
