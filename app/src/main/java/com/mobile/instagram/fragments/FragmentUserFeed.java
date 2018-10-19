package com.mobile.instagram.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
import com.mobile.instagram.models.Comment;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;
import com.mobile.instagram.util.FirebasePushController;
import com.mobile.instagram.util.PostAdapter;
import com.mobile.instagram.util.PostLocationSorter;
import com.mobile.instagram.util.PostTimeSorter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUserFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUserFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserFeed extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private ArrayList<Post> posts;

    private User currentUser;

    private RecyclerView recyclerView;


    private PostAdapter pa;

    public FragmentUserFeed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FragmentUserFeed.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUserFeed newInstance(User currentUser) {
        FragmentUserFeed fragment = new FragmentUserFeed();
        fragment.currentUser = currentUser;
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
        View view = inflater.inflate(R.layout.fragment_userfeed, container, false);
        this.recyclerView = view.findViewById(R.id.postList);
        this.posts = new ArrayList<>();
        this.pa = new PostAdapter(this.getActivity(), posts, currentUser, new PostAdapter.ClickListener() {
            @Override
            public void onLikeClicked(int position) {
                posts.get(position).addLike(currentUser.getUsername());
                FirebasePushController.likePost(posts.get(position));
                pa.notifyDataSetChanged();
            }
            @Override
            public void onCommentClicked(int position, String content){
                long time = Calendar.getInstance().getTimeInMillis();
                Comment comment1 = new Comment(currentUser.getUsername(),
                        posts.get(position).getPostId(),content,time);
                posts.get(position).addComment(currentUser.getUsername(),content,time);
                FirebasePushController.writeComment(comment1, posts.get(position));
                pa.notifyDataSetChanged();
            }
            @Override
            public void onUnlikeClicked(int position){
                posts.get(position).removeLike(currentUser.getUsername());
                FirebasePushController.unlikePost(posts.get(position));
                pa.notifyDataSetChanged();
            }
        });
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(pa);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) lm).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        df.child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                df.child("user-following").child(currentUser.getUid()).addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                User following = dataSnapshot.getValue(User.class);
                                final String userId = following.getUid();
                                df.child("user-posts").child(userId).orderByChild("time").limitToFirst(10)
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                Post p = dataSnapshot.getValue(Post.class);
                                                posts.add(0,p);
                                                Collections.sort(posts, new PostTimeSorter());
                                                pa.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                Post post = dataSnapshot.getValue(Post.class);
                                                System.out.println(post.getPostId());
                                                for(Post p : posts){
                                                    if (p.getPostId().equals(post)){
                                                        posts.remove(p);
                                                        posts.add(post);
                                                        System.out.println("removed and added");
                                                        Collections.sort(posts, new PostTimeSorter());
                                                        pa.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }


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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        df.child("user-posts").child(currentUser.getUid()).orderByChild("time").limitToFirst(20).addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        posts.add(0,post);
                        Collections.sort(posts, new PostTimeSorter());
                        pa.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        System.out.println(post.getPostId());
                        for(Post p : posts){
                            if (p.getPostId().equals(post)){
                                posts.remove(p);
                                posts.add(post);
                                System.out.println("removed and added");
                                Collections.sort(posts, new PostTimeSorter());
                                pa.notifyDataSetChanged();
                                break;
                            }
                        }
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
        ToggleButton sortButtom = view.findViewById(R.id.locationSortToggle);
        sortButtom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Collections.sort(posts, new PostLocationSorter());
                }else{
                    Collections.sort(posts, new PostTimeSorter());
                }
            }
        });
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
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}
