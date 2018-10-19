package com.mobile.instagram.models;

public class Comment {

    public String content;
    public String username;
    public String pid;
    public long time;

    public Comment(String username, String pid, String content, long time){
        this.pid = pid;
        this.username = username;
        this.content = content;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
