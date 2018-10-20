package com.mobile.instagram.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mobile.instagram.R;
import com.mobile.instagram.models.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import	android.support.v4.graphics.drawable.*;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;
import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;
import com.google.firebase.storage.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private static String TAG = "Profile Activity";

    private User user;
    private String uidString;
    private User currentUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private boolean followed;

    private TextView username;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private ImageView profilePhoto;
    private GridView pictureView;

    private Button followButton;
    private Button unfollowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        Intent it = getIntent();
        uidString = it.getStringExtra("uid");
        System.out.print("uid is" + uidString);

        followButton = findViewById(R.id.profileFollowButton);
        followButton.setOnClickListener(this);
        unfollowButton = findViewById(R.id.profileUnfollowButton);
        unfollowButton.setOnClickListener(this);
        unfollowButton.setVisibility(View.GONE);

        this.username = findViewById(R.id.profileUserName);
        this.posts = findViewById(R.id.profilePostsNum);
        this.followers = findViewById(R.id.profileFollowerNum);
        this.following = findViewById(R.id.profileFollowingNum);
        this.profilePhoto = findViewById(R.id.userProfile);
        init();
    }
//    @Override
//    public void onStart(){
//        super.onResume();
//        init();
//    }

    private void init(){
        mDatabase.child("users").child(uidString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User getUser = dataSnapshot.getValue(User.class);
                username.setText(getUser.getUsername());
                user = getUser;
//                Log.d(TAG, user.posts.toString() );
//                Integer num = new Integer(user.posts.size()-1);
//                following.setText(new Integer(user.following.size()-1).toString());
//                followers.setText(new Integer(user.followers.size()-1).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User getUser = dataSnapshot.getValue(User.class);
                currentUser = getUser;
//                Log.d(TAG, user.posts.toString() );
//                Integer num = new Integer(user.posts.size()-1);
//                following.setText(new Integer(user.following.size()-1).toString());
//                followers.setText(new Integer(user.followers.size()-1).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-posts").child(uidString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    posts.setText("0");
                }
                else{
                    int postsCount = 0;
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        postsCount ++;
                    }
                    posts.setText(Integer.toString(postsCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-follower").child(uidString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    followers.setText("0");
                }
                else{
                    int followerCount = 0;
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        followerCount ++;
                        if (ds.getValue(User.class)!= null){
                            if (ds.getValue(User.class).getUid().equals(currentUser.getUid())){
                                followButton.setVisibility(View.GONE);
                                unfollowButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    followers.setText(Integer.toString(followerCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-following").child(uidString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    following.setText("0");
                }
                else{
                    int followingCount = 0;
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        followingCount ++;
                    }
                    following.setText(Integer.toString(followingCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        StorageReference riversRef = mStorageRef.child("profile_images/"+uidString+".jpg");
        final long ONE_MEGABYTE = 2048 * 2048;
        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                setCircleProfile(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, exception.getMessage());
            }
        });

//        if(followed){
//            followButton.setVisibility(View.GONE);
//            unfollowButton.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    // Set the profile pic to circular
    private void setCircleProfile(Bitmap bitmap){
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        profilePhoto.setImageDrawable(mDrawable);
    }

    private void followUser(){
        Map<String, Object> childUpdate = new HashMap<String, Object>();
        childUpdate.put("user-following/"+currentUser.getUid()+"/"+user.getUid(), user);
        childUpdate.put("user-follower/"+user.getUid()+"/"+currentUser.getUid(), currentUser);
        mDatabase.updateChildren(childUpdate);
        Toast.makeText(ProfileActivity.this, "Followed this user",
                Toast.LENGTH_LONG).show();
        followButton.setVisibility(View.GONE);
        unfollowButton.setVisibility(View.VISIBLE);
    }

    private void unfollowUser(){
        mDatabase.child("user-following").child(currentUser.getUid()).child(user.getUid()).removeValue();
        mDatabase.child("user-follower").child(user.getUid()).child(currentUser.getUid()).removeValue();
        Toast.makeText(ProfileActivity.this, "Unfollowed this user",
                Toast.LENGTH_LONG).show();
        unfollowButton.setVisibility(View.GONE);
        followButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.profileFollowButton){
            followUser();
        } else if ( i == R.id.profileUnfollowButton){
            unfollowUser();
        }
    }
}
