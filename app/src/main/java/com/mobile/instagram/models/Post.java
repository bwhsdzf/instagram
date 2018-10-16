package com.mobile.instagram.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Post {
    private long time;
    private String postId;
    private String uid;
    private String message;
    private ArrayList<String> likes;
    private String location;
    private ArrayList<Comment> comments;

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

    public void setTime(long time){
        this.time = time;
    }
    public void setPostId(String postId){
        this.postId = postId;
    }
    public void setUid(String uid){ this.uid = uid; }

    public void setMessage(String message){ this.message = message;}

    public void setLikes(ArrayList<String> like){ this.likes = like;}

    public void setLocation(String location) {this.location = location;}

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("time",this.time);
        map.put("postId",this.postId);
        map.put("uid",uid);
        map.put("location",location);
        map.put("message",message);
        map.put("likes",likes.toString());
        map.put("comments",this.comments.toString());
        return map;
    }

}
