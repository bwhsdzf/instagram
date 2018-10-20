package com.mobile.instagram.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
import com.mobile.instagram.models.User;
import com.mobile.instagram.models.UserActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;

public class ActivityAdapter extends BaseAdapter {

    private ArrayList<UserActivity> userActivities;

    private LayoutInflater inflater;

    private DisplayImageOptions options1, options2;

    public ActivityAdapter(Context context, ArrayList<UserActivity> userActivities) {
        inflater = LayoutInflater.from(context);
        this.userActivities = userActivities;
        options1 = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
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
            holder.user1= view.findViewById(R.id.user1);
            holder.user2= view.findViewById(R.id.user2);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        final UserActivity ua = userActivities.get(position);
        df.child("users").child(ua.getUid1()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        ImageLoader.getInstance().displayImage(user1.getProfileUrl(),
                                holder.user1, options1);
                        holder.activityText.setText(user1.getUsername());

                        if (ua.isLike()){
                            holder.activityText.setText(holder.activityText.getText().toString() + " Liked ");
                        } else {
                            holder.activityText.setText(holder.activityText.getText().toString() + " Started following ");
                        }
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
                                holder.user2, options1);
                        if (user2.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            if (ua.isLike()){
                                holder.activityText.setText(holder.activityText.getText().toString()
                                        + "your post.");
                            } else{
                                holder.activityText.setText(holder.activityText.getText().toString()
                                        + "you.");
                            }
                        } else{
                            if (ua.isLike()){
                                holder.activityText.setText(holder.activityText.getText().toString()
                                        + user2.getUsername() + "'s post.");
                            } else{
                                holder.activityText.setText(holder.activityText.getText().toString()
                                        + user2.getUsername()+ ".");
                            }
                        }
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


    static class ViewHolder {
        ImageView user1;
        ImageView user2;
        TextView activityText;
    }
}
