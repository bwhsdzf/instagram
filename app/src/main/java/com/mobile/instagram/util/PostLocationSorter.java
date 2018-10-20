package com.mobile.instagram.util;

import com.mobile.instagram.models.Post;

import java.util.Comparator;

public class PostLocationSorter implements Comparator<Post> {



    public int compare(Post p1, Post p2){
        float distance1 = LocationService.distanceFromCurrent(p1.getLat(),p1.getLng());
        float distance2 = LocationService.distanceFromCurrent(p2.getLat(),p2.getLng());
        if (distance1 < distance2 ){
            return 1;
        }else if (distance1 == distance2){
            return 0;
        }else{
            return -1;
        }
    }
}
