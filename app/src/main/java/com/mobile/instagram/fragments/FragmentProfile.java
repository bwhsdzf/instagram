package com.mobile.instagram.fragments;

import com.mobile.instagram.R;
import com.mobile.instagram.activities.NavigationActivity;
import com.mobile.instagram.activities.PostActivity;
import com.mobile.instagram.activities.ProfileActivity;
import com.mobile.instagram.models.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridView;

import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;
import com.google.firebase.storage.*;
import com.mobile.instagram.models.relationalModels.UserFollower;
import com.mobile.instagram.models.relationalModels.UserFollowing;
import com.mobile.instagram.models.relationalModels.UserPosts;

import java.io.*;


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

    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private TextView username;
    private TextView posts;
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
        fragment.currentUser = mAuth.getCurrentUser();
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
        String uid = currentUser.getUid();

        iv = view.findViewById(R.id.fragmentProfile);
        iv.setOnClickListener(this);
        setCircleProfile(BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile));
        followers = view.findViewById(R.id.fragmentFollowerNum);
        posts = view.findViewById(R.id.fragmentPostsNum);
        following = view.findViewById(R.id.fragmentFollowingNum);
        pictureView = view.findViewById(R.id.pictureView);

        this.username = view.findViewById(R.id.fragmentUserName);
        mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, user.getUsername());
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mDatabase.child("user-posts").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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
        mDatabase.child("user-follower").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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
        mDatabase.child("user-following").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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

        StorageReference riversRef = mStorageRef.child("profile_images/"+uid+".jpg");
        final long ONE_MEGABYTE = 2048 * 2048;

        riversRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                setCircleProfile(bitmap);
                                hasProfile = true;

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG, exception.getMessage());
                                hasProfile = false;
                            }
                        });



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
                    setCircleProfile(bitmap);
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
}
