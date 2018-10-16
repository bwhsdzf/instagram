package com.mobile.instagram.models;

import java.util.ArrayList;

public class Post {
    public long time;
    public String postId;
    public String uid;
    public String message;
    public ArrayList<String> likes;
    public String location;
    public ArrayList<Comment> comments;

    public Post(long time, String postId, String uid, String message, String location,
                ArrayList<String> likes, ArrayList<Comment> comments){
        this.time = time;
        this.postId = postId;
        this.uid = uid;
        this.message = message;
        this.location = location;
        this.likes = likes;
        this.comments = comments;
    }

}
