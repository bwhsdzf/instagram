package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.Post;

import java.util.ArrayList;

public class UserPosts {
    private ArrayList<Post> posts;

    public UserPosts() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserPosts(ArrayList<Post> posts){
        this.posts = posts;
    }

    public void setPosts(ArrayList<Post> posts){this.posts = posts; }

    public ArrayList<Post> getPosts() {
        return posts;
    }
}
