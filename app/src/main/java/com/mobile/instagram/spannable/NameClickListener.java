package com.mobile.instagram.spannable;

import android.text.SpannableString;
import android.widget.Toast;

import com.mobile.instagram.activities.User_Feed.MyApplication;

public class NameClickListener implements ISpanClick {
    private SpannableString userName;
    private String userId;

    public NameClickListener(SpannableString name, String userid) {
        this.userName = name;
        this.userId = userid;
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(MyApplication.getContext(), userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
    }
}
