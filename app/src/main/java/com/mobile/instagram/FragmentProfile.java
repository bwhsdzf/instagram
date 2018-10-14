package com.mobile.instagram;

import com.mobile.instagram.models.DatabaseCon;
import com.mobile.instagram.models.User;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;
import com.google.firebase.storage.*;



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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "Fragment Profile";

    private OnFragmentInteractionListener mListener;

    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private DatabaseCon dc;

    private TextView tv;
    private TextView username;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private ImageView iv;



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
        fragment.dc = new DatabaseCon(mDatabase);
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
        view.findViewById(R.id.button).setOnClickListener(this);
        String uid = currentUser.getUid();

//        TextView tv = view.findViewById(R.id.userName);
        Log.d(TAG, dc.getUsername(uid).toString());
        tv = view.findViewById(R.id.textView);
        iv = view.findViewById(R.id.ivProfile);
        followers = view.findViewById(R.id.followers);
        posts = view.findViewById(R.id.posts);
        following = view.findViewById(R.id.following);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "reading" );
//                Log.d(TAG, value );
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        this.username = view.findViewById(R.id.userName);
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, user.username );
                username.setText(user.username);
                posts.setText(user.posts.size());
                followers.setText(user.followers.size());
                following.setText(user.following.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StorageReference riversRef = mStorageRef.child("profile.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        try {

            riversRef.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            iv.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        }catch (Exception e) {
            Log.d(TAG, "create temp unsuccessful");
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void saveToDb(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        Log.d(TAG, "saving to db" );
        myRef.setValue("Hello, World!");
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
        }
        else if (i == R.id.button) {
            saveToDb();
        }
    }
}
