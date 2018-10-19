package com.mobile.instagram.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post implements Parcelable {
    private long time;
    private String postId;
    private String uid;
    private String message;
    private String imgUrl;
    private ArrayList<String> likes;
    private String location;
    private ArrayList<Comment> comments;

    public Post() {}

    public Post(long time, String postId, String uid, String message, String location,String imgUrl,
                ArrayList<String> likes, ArrayList<Comment> comments){
        this.time = time;
        this.postId = postId;
        this.uid = uid;
        this.message = message;
        this.imgUrl = imgUrl;
        this.location = location;
        this.likes = likes;
        this.comments = comments;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setPostId(String postId){
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setUid(String uid){ this.uid = uid; }

    public String getUid() {
        return uid;
    }

    public void setMessage(String message){ this.message = message;}

    public String getMessage() {
        return message;
    }

    public void setLikes(ArrayList<String> like){ this.likes = like;}

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLocation(String location) {this.location = location;}

    public String getLocation() {
        return location;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(time);
        parcel.writeString(postId);
        parcel.writeString(uid);
        parcel.writeString(message);
        parcel.writeString(imgUrl);
        parcel.writeString(location);
    }

    public static final Parcelable.Creator<Post> CREATOR  = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            Post post = new Post();
            post.time = source.readLong();
            post.postId = source.readString();
            post.uid = source.readString();
            post.message = source.readString();
            post.imgUrl = source.readString();
            post.location = source.readString();
            return post;
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("time",this.time);
        map.put("postId",this.postId);
        map.put("uid",uid);
        map.put("location",location);
        map.put("message",message);
        map.put("likes",this.likes);
        map.put("comments",this.comments);
        map.put("imgUrl",this.imgUrl);
        return map;
    }

}
