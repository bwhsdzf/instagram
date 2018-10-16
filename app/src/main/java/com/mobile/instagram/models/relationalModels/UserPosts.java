package com.mobile.instagram.models.relationalModels;

import com.mobile.instagram.models.Post;

import java.util.ArrayList;

public class UserPosts {
    private String uid;
    private int num;
    private ArrayList<Post> posts;

    public UserPosts() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserPosts(String uid, int num, ArrayList<Post> posts){
        this.uid = uid;
        this.posts = posts;
        this.num = num;
    }

    public void setUid(String uid){ this.uid = uid; }

    public void setPosts(ArrayList<Post> posts){this.posts = posts; }

    public int getNum() { return num; }

    public void setNum(int num) { this.num = num; }

    public String getUid() {
        return uid;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }
}
