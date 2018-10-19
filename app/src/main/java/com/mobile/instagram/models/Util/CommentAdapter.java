package com.mobile.instagram.models.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.instagram.R;
import com.mobile.instagram.models.Comment;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    private ArrayList<Comment> comments;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, ArrayList<Comment> comments){
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (comments == null) return 0;
        else return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
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
            view = inflater.inflate(R.layout.layout_comment_item, parent, false);
            holder = new ViewHolder();
            holder.commentContent = view.findViewById(R.id.commentContent);
            holder.username = view.findViewById(R.id.commentUsername);
            assert view != null;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.username.setText(comments.get(position).getUsername());
        holder.commentContent.setText(comments.get(position).getUsername());

        return view;
    }


    static class ViewHolder {
        TextView username;
        TextView commentContent;
    }
}
