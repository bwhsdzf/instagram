package com.mobile.instagram.models;

public class Comment {

    public String commendId;
    public String content;
    public String uid;
    public long time;

    public Comment(String uid, String commentId, String content, long time){
        this.commendId = commentId;
        this.uid = uid;
        this.content = content;
        this.time = time;
    }
}
