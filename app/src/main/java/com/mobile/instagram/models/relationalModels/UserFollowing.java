package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.User;

import java.util.ArrayList;

public class UserFollowing {
    private ArrayList<User> following;

    public UserFollowing(){

    }

    public UserFollowing(ArrayList<User> following){
        this.following = following;
    }

    public void setFollowing(ArrayList<User> following) {
        this.following = following;
    }

    public ArrayList<User> getFollowing() {
        return following;
    }
}
