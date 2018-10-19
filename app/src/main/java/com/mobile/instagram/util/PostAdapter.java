package com.mobile.instagram.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private ArrayList<Post> posts;

    private User currentUser;

    private final ClickListener listener;

    private LayoutInflater inflater;
    private Context context;

    private DisplayImageOptions option1, option2;

    public interface ClickListener {

        void onLikeClicked(int position);

        void onCommentClicked(int position, String text);

        void onUnlikeClicked(int position);
    }

    public PostAdapter(Context context, ArrayList<Post> posts, User user, ClickListener listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.currentUser = user;
        this.posts = posts;
        this.listener = listener;
        option1 = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_img_ept)
                .showImageOnFail(R.mipmap.ic_img_err)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new FadeInBitmapDisplayer(100))
                .build();

        option2 = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_img_ept)
                .showImageOnFail(R.mipmap.ic_img_err)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootV = inflater.inflate(R.layout.layout_post_item, parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootV.setLayoutParams(lp);
        return new PostViewHolder(rootV, listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position)
    {
        Post post = posts.get(position);
        holder.commentList.setAdapter(new CommentAdapter( this.context, post.getComments()));
        setListViewHeightBasedOnChildren(holder.commentList);
        final ImageLoader il = ImageLoader.getInstance();
        il.displayImage(post.getImgUrl(), holder.postImageView
                , option1, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view){
                        holder.progressBar.setVisibility(View.GONE);
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
        String uid = post.getUid();
        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        df.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                holder.usernameView.setText(user.getUsername());
                il.displayImage(user.getProfileUrl(),holder.userView,option2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        holder.postMessageView.setText(post.getMessage());
        ArrayList<String> likes = post.getLikes();
        if (likes == null || likes.size() == 0) {} else {
            if (likes.size() > 3) {
                holder.likeListView.setText(likes.get(0) + ", "+likes.get(1) + ", " + likes.get(2) + " and "+
                        (likes.size()-3) + " Others liked this post");
            }else if (likes.size() == 3){
                holder.likeListView.setText(likes.get(0) + ", "+ likes.get(1) + " and "+ likes.get(2) +
                        " liked this post");
            }else if (likes.size() == 2){
                holder.likeListView.setText(likes.get(0) + " and "+ likes.get(1) + " liked this post");
            }else{
                holder.likeListView.setText(likes.get(0) + " liked this post");
            }
        }
        if (post.getLikes() != null) {
            for (String username : post.getLikes()) {
                if (username.equals(currentUser.getUsername())) {
                    holder.setUnlikeButton();
                }
            }
        }
        long timeDiff = Calendar.getInstance().getTimeInMillis() - post.getTime();
        int days = (int)Math.floor(timeDiff/1000/60/60/14);
        if(days < 1) holder.timeview.setText("Today");
        else if(days == 1) holder.timeview.setText("1 Day ago");
        else holder.timeview.setText(days+ " Days ago");

        holder.locationView.setText(LocationService.getCity(post.getLat(),post.getLng()));


    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight()*(listAdapter.getCount()-1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userView;
        TextView usernameView;
        TextView locationView;
        ImageView postImageView;
        TextView postMessageView;
        TextView likeListView;
        ProgressBar progressBar;
        TextView timeview;
        ImageView likeButton;
        ImageView unlikeButton;
        Button commentButton;
        EditText commentField;
        ListView commentList;


        private WeakReference<ClickListener> listenerRef;

        public PostViewHolder(View view, ClickListener listener){
            super(view);
            userView = view.findViewById(R.id.userView);
            usernameView = view.findViewById(R.id.usernameView);
            locationView = view.findViewById(R.id.locationView);
            postImageView = view.findViewById(R.id.postImageView);
            postMessageView = view.findViewById(R.id.postMessageView);
            timeview = view.findViewById(R.id.timeView);
            progressBar = view.findViewById(R.id.progressBar2);
            likeButton = view.findViewById(R.id.likeButton);
            unlikeButton = view.findViewById(R.id.unlikeButton);
            commentButton = view.findViewById(R.id.commentButton);
            likeListView = view.findViewById(R.id.likeListView);
            commentField = view.findViewById(R.id.commentInput);
            commentList = view.findViewById(R.id.commentList);
            likeButton.setOnClickListener(this);
            commentButton.setOnClickListener(this);
            unlikeButton.setOnClickListener(this);
            setLikeButton();
            listenerRef = new WeakReference<>(listener);
        }

        public void setUnlikeButton(){
            likeButton.setVisibility(View.GONE);
            unlikeButton.setVisibility(View.VISIBLE);
        }

        public void setLikeButton(){
            likeButton.setVisibility(View.VISIBLE);
            unlikeButton.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            System.out.println("pressed");
            if (v.getId() == likeButton.getId()) {
                listenerRef.get().onLikeClicked(getAdapterPosition());
                setUnlikeButton();
            } else if (v.getId() == commentButton.getId()){
                if (!TextUtils.isEmpty(commentField.getText().toString())){
                listenerRef.get().onCommentClicked(getAdapterPosition(), commentField.getText().toString());
                }
            } else if (v.getId() == unlikeButton.getId()){

                listenerRef.get().onUnlikeClicked(getAdapterPosition());
                setLikeButton();
            }

        }


    }
}
