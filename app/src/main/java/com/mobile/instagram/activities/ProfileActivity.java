package com.mobile.instagram.activities;

import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;

import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;
import com.google.firebase.storage.*;
import com.mobile.instagram.Util.PostWallAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private static String TAG = "Profile Activity";

    private User user;
    private String uidString;
    private User currentUser;

    private ArrayList<Post> posts;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private TextView username;
    private TextView postsCount;
    private int postsCountInt;
    private int currentFollowingCount;
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

        posts = new ArrayList<>();

        this.username = findViewById(R.id.profileUserName);
        this.postsCount = findViewById(R.id.profilePostsNum);
        this.followers = findViewById(R.id.profileFollowerNum);
        this.following = findViewById(R.id.profileFollowingNum);
        this.profilePhoto = findViewById(R.id.userProfile);
        this.pictureView = findViewById(R.id.pictureView);
        postsCount.setText("0");
        init();
    }
//    @Override
//    public void onStart(){
//        super.onResume();
//        init();
//    }

    private void init(){
        mDatabase.child("users").child(uidString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User getUser = dataSnapshot.getValue(User.class);
                user = getUser;
                username.setText(user.getUsername());
                following.setText(Integer.toString(user.getFollowingCount()));
                followers.setText(Integer.toString(user.getFollowerCount()));
                if (mAuth.getUid().equals( getUser.getUid())){
                    followButton.setVisibility(View.GONE);
                    unfollowButton.setVisibility(View.GONE);
                }
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-posts").child(uidString).limitToFirst(50).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                postsCountInt ++;
                postsCount.setText(Integer.toString(postsCountInt));
                posts.add(dataSnapshot.getValue(Post.class));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
        });
        mDatabase.child("user-follower").child(mAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User follower = dataSnapshot.getValue(User.class);
                if(follower.getUid().equals(mAuth.getUid())){
                    followButton.setVisibility(View.GONE);
                    unfollowButton.setVisibility(View.VISIBLE);
                }
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
        pictureView.setAdapter(new PostWallAdapter(this,this.posts));
        pictureView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startImagePagerActivity(i);
            }
        });
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
        childUpdate.put("users/"+currentUser.getUid()+"/"+"followingCount",
                currentUser.getFollowingCount()+1);
        childUpdate.put("user-follower/"+user.getUid()+"/"+currentUser.getUid(), currentUser);
        childUpdate.put("users/"+user.getUid()+"/"+"followerCount",
                user.getFollowerCount()+1);
        UserActivity ua = new UserActivity(currentUser.getUid(),user.getUid(),
                false,"0", Calendar.getInstance().getTimeInMillis());
        String activityKey = mDatabase.child("user-activities").child(currentUser.getUid()).push().
                getKey();
        childUpdate.put("user-activities/"+ currentUser.getUid()+"/"+activityKey, ua);
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

    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(this, ImagePagerActivity.class);
        Bundle b = new Bundle();
        b.putParcelableArrayList("posts", (ArrayList<? extends Parcelable>) posts);
        intent.putExtra("bundle",b);
        intent.putExtra("position",position);
        startActivity(intent);
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
