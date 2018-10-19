package com.mobile.instagram.fragments;

import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;
import com.mobile.instagram.models.Util.FirebasePushController;
import com.mobile.instagram.models.Util.PostAdapter;

import java.util.ArrayList;


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
                FirebasePushController.likePost(posts.get(position));
            }
            @Override
            public void onCommentClicked(int position, String content){
                FirebasePushController.writeComment( content, posts.get(position));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(pa);
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        df.child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                System.out.println("looking for following for "+ currentUser.getUsername());
                df.child("user-following").child(currentUser.getUid()).addChildEventListener(
                        new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                User following = dataSnapshot.getValue(User.class);
                                System.out.println("looking for posts from "+ following.getUsername());
                                final String userId = following.getUid();
                                df.child("user-posts").child(userId).orderByChild("time").limitToFirst(10)
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                System.out.println("found one post");
                                                Post p = dataSnapshot.getValue(Post.class);
                                                posts.add(p);
                                                System.out.println(p.getPostId());
                                                pa.notifyDataSetChanged();
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
