package com.mobile.instagram.Util;

import com.mobile.instagram.models.Post;

import java.util.Comparator;

public class PostTimeSorter implements Comparator<Post> {

    public int compare(Post p1, Post p2){
        long difference =  p2.getTime() - p1.getTime();
        if( difference > 0 ){
            return 1;
        } else if(difference == 0){
            return 0;
        }else{
            return -1;
        }

    }
}
