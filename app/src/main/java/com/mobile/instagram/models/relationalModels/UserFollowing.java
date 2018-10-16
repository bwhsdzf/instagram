package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.User;

import java.util.ArrayList;

public class UserFollowing {
    private String uid;
    private int num;
    private ArrayList<User> following;

    public UserFollowing(){

    }

    public UserFollowing(String uid, int num, ArrayList<User> following){
        this.uid = uid;
        this.num = num;
        this.following = following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
    }

    public ArrayList<User> getFollowing() {
        return following;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
