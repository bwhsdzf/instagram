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

public class SearchListAdapter extends BaseAdapter {
    private ArrayList<User> users;
    private LayoutInflater inflater;
    private DisplayImageOptions options;

    public SearchListAdapter(Context context, ArrayList<User> users) {
        inflater = LayoutInflater.from(context);
        this.users = users;
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_img_ept)
                .showImageOnFail(R.drawable.ic_profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }
    @Override
    public int getCount() {
        return users.size();
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
            view = inflater.inflate(R.layout.layout_search_item, parent, false);
            holder = new ViewHolder();
            holder.user = view.findViewById(R.id.searchUserProfile);
            holder.username = view.findViewById(R.id.searchUsername);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        User user = users.get(position);
        ImageLoader.getInstance().displayImage(user.getProfileUrl(),holder.user,options);
        holder.username.setText(user.getUsername());
        return view;
    }


    static class ViewHolder {
        ImageView user;
        TextView username;
    }

}
