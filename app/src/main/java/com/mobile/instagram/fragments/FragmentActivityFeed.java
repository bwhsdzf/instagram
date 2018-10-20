package com.mobile.instagram.fragments;
import com.google.firebase.database.ChildEventListener;
import com.mobile.instagram.models.UserActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
import com.mobile.instagram.models.User;
import com.mobile.instagram.adapters.ActivityAdapter;
import com.mobile.instagram.util.ActivityTimeSorter;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentActivityFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentActivityFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentActivityFeed extends Fragment {

    private static final String TAG = "FragmentActivityFeed";

    private DatabaseReference mDatabaseRef;
    private User currentUser;

    private ListView activityList;
    private ListView activityNotifyList;

    private ArrayList<UserActivity> activities;
    private ArrayList<UserActivity> activitiesNotify;

    private ActivityAdapter ia1, ia2;

    private OnFragmentInteractionListener mListener;

    public FragmentActivityFeed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentActivityFeed.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentActivityFeed newInstance(User currentUser) {
        FragmentActivityFeed fragment = new FragmentActivityFeed();
        fragment.currentUser = currentUser;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activityfeed, container, false);
        activityList = view.findViewById(R.id.activityList);
        activityNotifyList = view.findViewById(R.id.activityNotifyList);
        activityList.setVisibility(View.GONE);
        ToggleButton toggleButton = view.findViewById(R.id.toggleFollowing);
        initBothList();

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activityNotifyList.setVisibility(View.GONE);
                    activityList.setVisibility(View.VISIBLE);
                }else{
                    activityNotifyList.setVisibility(View.VISIBLE);
                    activityList.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }


    private void initBothList(){
        activities = new ArrayList<>();
        activitiesNotify = new ArrayList<>();
        ia1 = new ActivityAdapter(getActivity(),activities);
        ia2 = new ActivityAdapter(getActivity(),activitiesNotify);
        mDatabaseRef.child("user-following").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                }
                else{
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        try {
                            User followedUser = ds.getValue(User.class);
                            mDatabaseRef.child("user-activities").child(followedUser.getUid()).orderByChild("time")
                                    .addChildEventListener(
                                            new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                    UserActivity ua = dataSnapshot.getValue(UserActivity.class);
                                                    activities.add(0, ua);
                                                    if (activities.size()>20){
                                                        activities.remove(20);
                                                    }
                                                    Collections.sort(activities, new ActivityTimeSorter());
                                                    ia1.notifyDataSetChanged();
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
                        }catch(Exception e){
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabaseRef.child("activities-user").child(currentUser.getUid()).orderByChild("time").limitToFirst(20)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        UserActivity ua = dataSnapshot.getValue(UserActivity.class);
                        activitiesNotify.add(0,ua);
                        if (activitiesNotify.size()>20){
                            activitiesNotify.remove(20);
                        }
                        Collections.sort(activitiesNotify, new ActivityTimeSorter());
                        ia2.notifyDataSetChanged();
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
        activityList.setAdapter(ia1);
        activityNotifyList.setAdapter(ia2);
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
}
