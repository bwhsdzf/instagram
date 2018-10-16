package com.mobile.instagram.models;

import java.util.ArrayList;
import com.mobile.instagram.models.Post;

public class User {
    public String username;
    public String email;
    public String uid;
    public ArrayList<String> following;
    public ArrayList<String> followers;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
//        this.posts = posts;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}