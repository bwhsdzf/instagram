package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.User;

import java.util.ArrayList;

public class UserFollower {
    private int num;
    private String uid;
    private ArrayList<User> followers;

    public UserFollower(){}

    public UserFollower(String uid, int num, ArrayList<User> followers){
        this.uid = uid;
        this.num = num;
        this.followers = followers;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }
}
