package com.mobile.instagram.models;

import java.util.ArrayList;
import com.mobile.instagram.models.Post;

public class User {
    private String username;
    private String email;
    private String uid;
    private String profileUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email, String profileUrl) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
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

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}