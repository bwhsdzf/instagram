package com.mobile.instagram.fragments;

import com.mobile.instagram.R;
import com.mobile.instagram.activities.NavigationActivity;
import com.mobile.instagram.activities.PostActivity;
import com.mobile.instagram.activities.ProfileActivity;
import com.mobile.instagram.models.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import	android.support.v4.graphics.drawable.*;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.*;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAG = "Fragment Profile";

    private OnFragmentInteractionListener mListener;

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
    private ImageView iv;
    private GridView pictureView;
    private boolean hasProfile = false;



    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mAuth Firebase Auth instance.
     * @param mDatabase Firebase database reference.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(FirebaseAuth mAuth, DatabaseReference mDatabase)
        {
        FragmentProfile fragment = new FragmentProfile();
        fragment.mAuth = mAuth;
        fragment.mDatabase = mDatabase;
        fragment.mStorageRef = FirebaseStorage.getInstance().getReference();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.signOutButton).setOnClickListener(this);
        view.findViewById(R.id.toPost).setOnClickListener(this);
        view.findViewById(R.id.toProfile).setOnClickListener(this);
        String uid = FirebaseAuth.getInstance().getUid();
        iv = view.findViewById(R.id.fragmentProfile);
        iv.setOnClickListener(this);
        setCircleProfile(BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile));
        followers = view.findViewById(R.id.fragmentFollowerNum);
        postsCount = view.findViewById(R.id.fragmentPostsNum);
        following = view.findViewById(R.id.fragmentFollowingNum);
        pictureView = view.findViewById(R.id.fragmentPictureView);

        posts = new ArrayList<Post>();



        this.username = view.findViewById(R.id.fragmentUserName);
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                Log.d(TAG, currentUser.getUsername());
                username.setText(currentUser.getUsername());
                if (currentUser.getProfileUrl() != null) {
                    ImageLoader.getInstance().displayImage(currentUser.getProfileUrl(), iv);
                    hasProfile = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mDatabase.child("user-posts").child(uid).addChildEventListener(new ChildEventListener() {

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
        mDatabase.child("user-follower").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    followers.setText("0");
                }
                else{
                    int followerCount = 0;
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        followerCount ++;
                    }
                    following.setText(Integer.toString(followerCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase.child("user-following").child(uid).addValueEventListener(new ValueEventListener() {
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

        pictureView.setAdapter(new ImageAdapter(getActivity(),posts));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try{
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(NavigationActivity.getContext().getContentResolver(),
                            selectedImage);
                    ImageLoader.getInstance().displayImage(selectedImage.toString(),iv);
                    uploadProfile(bitmap);
                    } catch (Exception e){
                        Log.d(TAG, "didn't find context");
                    }
                }
                break;
        }
    }

    private void uploadProfile(Bitmap bitmap){
        String uid = currentUser.getUid();
        StorageReference desertRef = mStorageRef.child("profile_images/"+uid+".jpg");

        Toast.makeText(FragmentProfile.this.getActivity(), "Uploading new photo",
                Toast.LENGTH_LONG).show();
        // Delete the origin profile
        if (hasProfile) {
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Origin uploaded profile deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Delete failed");
                }
            });
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        final byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageRef.child("profile_images/" + uid + ".jpg");
        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(FragmentProfile.this.getActivity(), "Failed to upload photo.",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                currentUser.setProfileUrl(downloadUrl.toString());
                mDatabase.child("users").child(currentUser.getUid())
                        .setValue(currentUser);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Set the profile pic to circular
    private void setCircleProfile(Bitmap bitmap){
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        iv.setImageDrawable(mDrawable);
    }

    private void signOut(){
        if(this.mAuth == null){
            Log.d(TAG, "mAuth is null");
        }else {
            mAuth.signOut();
            Toast.makeText(this.getActivity(), "Logged out",
                    Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.fragmentProfile){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
        } else if (i == R.id.toPost){
            Intent intent = new Intent(getActivity(),PostActivity.class );
            startActivity(intent);
        }else if (i == R.id.toProfile){
            Intent intent = new Intent(getActivity(),ProfileActivity.class );
            intent.putExtra("uid","p8x03XRZFvfa6W0tu8VIHdfxKtI3");
            System.out.println("passing " + intent.getStringExtra("uid"));
            startActivity(intent);
        }
    }


    private class ImageAdapter extends BaseAdapter {

        private ArrayList<Post> posts;

        private LayoutInflater inflater;

        private DisplayImageOptions options;

        ImageAdapter(Context context, ArrayList<Post> posts) {
            inflater = LayoutInflater.from(context);

            this.posts = posts;
            options = new DisplayImageOptions.Builder()
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
            return null;
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
                view = inflater.inflate(R.layout.post_layout, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.photo);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage(posts.get(position).getImgUrl(), holder.imageView, options, new SimpleImageLoadingListener() {
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
