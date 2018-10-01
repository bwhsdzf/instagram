package com.mobile.instagram.models;

import com.google.firebase.database.*;
public class DatabaseCon {

    private DatabaseReference mDatabase;
    public DatabaseCon(DatabaseReference dr){
        this.mDatabase = dr;
    }

    public Query getUsername(String UID){
        Query query = mDatabase.child("users").child(UID);
        return query;
    }

}
