package com.mobile.instagram.models;

import java.util.ArrayList;

public class User {
    public String username;
    public String email;
    public String uid;
    public ArrayList<String> posts;
    public ArrayList<String> following;
    public ArrayList<String> followers;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email, ArrayList<String> posts,
                ArrayList<String> following, ArrayList<String> followers) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.posts = posts;
        this.following = following;
        this.followers =followers;
    }

}
