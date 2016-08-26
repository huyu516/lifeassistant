package com.hy.lifeassistant.domain;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 开支记录
public class Expense extends RealmObject {

    @PrimaryKey
    private String id;      // 主键

    private float money;    // 金额

    private Date date;      // 时间

    private int type;       // 类型:0-吃法;1-零食;2-购物

    public Expense() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
