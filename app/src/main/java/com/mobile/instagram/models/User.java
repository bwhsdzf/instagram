package com.mobile.instagram.models;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {
    private String username;
    private String email;
    private String uid;
    private String profileUrl;
    private int followerCount;
    private int followingCount;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email, String profileUrl, int followerCount,
                int followingCount) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeString(profileUrl);
        parcel.writeInt(followerCount);
        parcel.writeInt(followingCount);
    }

    public static final Parcelable.Creator<User> CREATOR  = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            User user = new User();
            user.username = source.readString();
            user.email = source.readString();
            user.uid = source.readString();
            user.profileUrl = source.readString();
            user.followerCount = source.readInt();
            user.followingCount = source.readInt();
            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}