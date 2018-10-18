package com.mobile.instagram.fragments;
import com.google.firebase.database.ChildEventListener;
import com.mobile.instagram.models.UserActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.instagram.R;
import com.mobile.instagram.models.User;
import com.mobile.instagram.models.Util.ActivityTimeSorter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentActivityFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentActivityFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentActivityFeed extends Fragment {

    private DatabaseReference mDatabaseRef;
    private User currentUser;

    private ListView activityList;

    private ArrayList<UserActivity> activities;

    private ImageAdapter ia;


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
    public static FragmentActivityFeed newInstance() {
        FragmentActivityFeed fragment = new FragmentActivityFeed();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        ia = new ImageAdapter(getActivity(),activities);
        mDatabaseRef.child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);
                        activities = new ArrayList<>();
                        final ArrayList<String> following = new ArrayList<>();
                        mDatabaseRef.child("user-following").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()){
                                }
                                else{
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User followedUser = ds.getValue(User.class);
                                        System.out.println("folloinh user" + followedUser.getUid());
                                        mDatabaseRef.child("user-activities").child(followedUser.getUid())
                                                .addChildEventListener(
                                                new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                        UserActivity ua = dataSnapshot.getValue(UserActivity.class);
                                                        System.out.println(ua.getUid1() + "u1" + ua.getUid2() + "u2");
                                                        activities.add(ua);
                                                        ia.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                                        ia.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                }
                                        );
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        Collections.sort(activities, new ActivityTimeSorter());
                        activityList.setAdapter(new ImageAdapter(getActivity(), activities));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    private static class ImageAdapter extends BaseAdapter {

        private ArrayList<UserActivity> userActivities;

        private LayoutInflater inflater;

        private DisplayImageOptions options;

        ImageAdapter(Context context, ArrayList<UserActivity> userActivities) {
            inflater = LayoutInflater.from(context);
            this.userActivities = userActivities;
            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.mipmap.ic_img_ept)
                    .showImageOnFail(R.mipmap.ic_img_err)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                    .build();
        }

        @Override
        public int getCount() {
            return userActivities.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.layout_activity, parent, false);
                holder = new ViewHolder();
                holder.activityText = view.findViewById(R.id.activityText);
                holder.username1 = view.findViewById(R.id.username1);
                holder.username2 = view.findViewById(R.id.username2);
                holder.user1= view.findViewById(R.id.user1);
                holder.user2= view.findViewById(R.id.user2);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            DatabaseReference df = FirebaseDatabase.getInstance().getReference();
            UserActivity ua = userActivities.get(position);
            df.child("users").child(ua.getUid1()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user1 = dataSnapshot.getValue(User.class);
                            ImageLoader.getInstance().displayImage(user1.getProfileUrl(),
                                    holder.user1, options);
                            holder.username1.setText(user1.getUsername());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
            df.child("users").child(ua.getUid2()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user2 = dataSnapshot.getValue(User.class);
                            ImageLoader.getInstance().displayImage(user2.getProfileUrl(),
                                    holder.user2, options);
                            holder.username2.setText(user2.getUsername());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
            if (ua.isLike()){
                holder.activityText.setText(" liked post from ");
            } else{
                holder.activityText.setText("followed");
            }


            return view;
        }
    }

    static class ViewHolder {
        ImageView user1;
        ImageView user2;
        TextView activityText;
        TextView username1;
        TextView username2;
    }
}
