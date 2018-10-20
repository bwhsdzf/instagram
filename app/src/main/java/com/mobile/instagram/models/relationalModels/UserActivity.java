package com.mobile.instagram.models.relationalModels;

public class UserActivity {
    private String uid;
    private boolean isLike;
    private String uid2;
    private String pid;

    public UserActivity(){}

    public UserActivity(String uid, boolean isLike, String uid2, String pid){
        this.uid = uid;
        this.isLike =isLike;
        this.uid2 = uid2;
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getPid() {
        return pid;
    }

    public String getUid2() {
        return uid2;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }
}
