package com.mobile.instagram.models.Util;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.instagram.models.Comment;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;
import com.mobile.instagram.R;
import com.mobile.instagram.models.UserActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirebasePushController {
    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static final StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public static void writeComment(final String content, final Post post){
        final String uid = mAuth.getUid();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        ArrayList<Comment> comments = post.getComments();
                        if (comments == null) comments = new ArrayList<>();
                        Comment comment = new Comment(user.getUsername(), post.getPostId(), content,
                                Calendar.getInstance().getTimeInMillis());
                        comments.add(comment);
                        Map<String, Object> childUpdate = new HashMap<>();
                        childUpdate.put("posts/"+post.getPostId()+"/comments", comments);
                        mDatabase.updateChildren(childUpdate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public static void likePost(final Post post){

        final String uid = mAuth.getUid();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        ArrayList<String> likes = post.getLikes();
                        if (likes == null) likes = new ArrayList<>();
                        likes.add(user.getUsername());
                        Map<String, Object> childUpdate = new HashMap<>();
                        childUpdate.put("posts/"+post.getPostId()+"/likes", likes);
                        childUpdate.put("user-posts/"+post.getUid()+"/"+post.getPostId()+"/likes",
                                likes);
                        UserActivity ua = new UserActivity(user.getUid(), post.getUid(), true,
                                post.getPostId(), Calendar.getInstance().getTimeInMillis());
                        String key = mDatabase.child("user-activities").child(user.getUid()).push().getKey();
                        childUpdate.put("user-activities/"+user.getUid()+ "/"+key, ua);
                        mDatabase.updateChildren(childUpdate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    public static void unlikePost(final Post post){
        final String uid = mAuth.getUid();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        ArrayList<String> likes = post.getLikes();
                        for (String username: likes){
                            if (username.equals(user.getUsername())){
                                likes.remove(username);
                                break;
                            }
                        }
                        Map<String, Object> childUpdate = new HashMap<>();
                        childUpdate.put("posts/"+post.getPostId()+"/likes", likes);
                        childUpdate.put("user-posts/"+post.getUid()+"/"+post.getPostId()+"/likes",
                                likes);
                        mDatabase.updateChildren(childUpdate);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
