package com.mobile.instagram.models;

public class UserActivity {
    private String uid1;
    private String uid2;
    private boolean isLike;
    private String pid;
    private long time;

    public UserActivity(){}

    public UserActivity(String uid1, String uid2, boolean isLike, String pid, long time){
        this.isLike = isLike;
        this.pid = pid;
        this.time = time;
        this.uid1 = uid1;
        this.uid2 = uid2;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid1() {
        return uid1;
    }

    public String getPid() {
        return pid;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
