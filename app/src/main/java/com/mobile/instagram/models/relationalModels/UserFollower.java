package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.User;

import java.util.ArrayList;

public class UserFollower {
    private ArrayList<User> followers;

    public UserFollower(){}

    public UserFollower(ArrayList<User> followers){
        this.followers = followers;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }
}
