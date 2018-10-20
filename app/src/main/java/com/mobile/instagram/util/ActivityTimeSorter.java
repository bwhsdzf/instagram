package com.mobile.instagram.util;

import com.mobile.instagram.models.UserActivity;

import java.util.Comparator;

public class ActivityTimeSorter implements Comparator<UserActivity> {

    public int compare(UserActivity a1, UserActivity a2){
        long difference  = a2.getTime() - a1.getTime();
        if( difference > 0 ){
            return 1;
        } else if(difference == 0){
            return 0;
        }else{
            return -1;
        }
    }

}
