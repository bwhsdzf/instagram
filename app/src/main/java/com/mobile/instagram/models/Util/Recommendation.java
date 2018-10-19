package com.mobile.instagram.models.Util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.models.User;

import java.util.ArrayList;

public class Recommendation {
    private ArrayList<User> result;

    private ArrayList<User> tempList1;
    private ArrayList<User> tempList2;

    public Recommendation(){
        result = new ArrayList<>();
        tempList1 = new ArrayList<>();
        tempList2 = new ArrayList<>();
    }

    public ArrayList<User> findMayKnow(User user){
        result.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // Find followers are following who
        mDatabaseRef.child("user-follower").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                        }else{
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                User follower = ds.getValue(User.class);
                                result.addAll(findFollowing(follower));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        return result;
    }

    public ArrayList<User> findMostPopular(){
        result.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("users-follower").orderByChild("num").limitToFirst(5).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        return result;
    }

    private ArrayList<User> findFollowing(User user){
        tempList1.clear();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("user-following").child(user.getUid()).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User following = dataSnapshot.getValue(User.class);
                        tempList1.add(following);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        return tempList1;
    }
}
