package com.mobile.instagram.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mobile.instagram.R;
import com.mobile.instagram.fragments.FragmentProfile;
import com.mobile.instagram.models.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import	android.support.v4.graphics.drawable.*;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;

import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;
import com.google.firebase.storage.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
        mDatabase.child("users").child(uidString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User getUser = dataSnapshot.getValue(User.class);
                username.setText(getUser.getUsername());
                user = getUser;
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
        mDatabase.child("user-posts").child(uidString).addChildEventListener(new ChildEventListener() {

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
                        try{
                            if (ds.getValue(User.class).getUid().equals(currentUser.getUid())){
                                followButton.setVisibility(View.GONE);
                                unfollowButton.setVisibility(View.VISIBLE);
                            }
                        } catch(NullPointerException e){
                            Log.e(TAG,"Null pointer for user again");
                        }

                    }
                    followers.setText(Integer.toString(followerCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-following").child(uidString).child("num").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    following.setText("0");
                }
                else{
                    Integer followingCount = dataSnapshot.getValue(Integer.class);
                    following.setText(followingCount.toString());
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
        pictureView.setAdapter(new ImageAdapter(this,this.posts));
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
        childUpdate.put("user-following/"+currentUser.getUid()+"/num",
                Integer.parseInt(following.getText().toString()) + 1);
        childUpdate.put("user-follower/"+user.getUid()+"/"+currentUser.getUid(), currentUser);
        childUpdate.put("user-follower/"+user.getUid()+"/num",
                Integer.parseInt(followers.getText().toString()) + 1);
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
    private class ImageAdapter extends BaseAdapter {

        private ArrayList<Post> posts;

        private LayoutInflater inflater;

        private DisplayImageOptions option;

        ImageAdapter(Context context, ArrayList<Post> posts) {
            inflater = LayoutInflater.from(context);

            this.posts = posts;
            option = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.mipmap.ic_img_ept)
                    .showImageOnFail(R.mipmap.ic_img_err)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }

        @Override
        public int getCount() {
            return posts.size();
        }

        @Override
        public Object getItem(int position) {
            return posts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.layout_post, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.photo);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage(posts.get(position).getImgUrl(), holder.imageView, option, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
