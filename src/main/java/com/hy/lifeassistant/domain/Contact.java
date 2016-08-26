package com.hy.lifeassistant.domain;

// 联系人
public class Contact implements Comparable<Contact> {

    private String name;        // 人名

    private String phone;       // 号码

    private String sortedKey;   // 人名拼音

    private int callTimes;      // 最近和本机通话次数

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(int callTimes) {
        this.callTimes = callTimes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSortedKey() {
        return sortedKey;
    }

    public void setSortedKey(String sortedKey) {
        this.sortedKey = sortedKey;
    }

    @Override
    public int compareTo(Contact another) {
        int diff = another.callTimes - this.callTimes;

        if (diff != 0) {
            return diff;
        }

        return another.getSortedKey().compareTo(this.getSortedKey());
    }

}
