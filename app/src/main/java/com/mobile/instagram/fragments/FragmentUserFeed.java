package com.mobile.instagram.fragments;

import android.content.Context;
import android.content.Intent;
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
<<<<<<< HEAD
import android.widget.Button;
=======
import android.widget.CompoundButton;
import android.widget.ToggleButton;
>>>>>>> eric_branch

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.R;
<<<<<<< HEAD
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.instagram.R;
import com.mobile.instagram.adapter.CircleAdapter;
import com.mobile.instagram.bean.CircleItem;
import com.mobile.instagram.contral.CirclePublicCommentContral;
import com.mobile.instagram.listener.SwpipeListViewOnScrollListener;
import com.mobile.instagram.utils.CommonUtils;
import com.mobile.instagram.utils.DatasUtil;
=======
import com.mobile.instagram.models.Comment;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;
import com.mobile.instagram.util.FirebasePushController;
import com.mobile.instagram.adapters.PostAdapter;
import com.mobile.instagram.util.PostLocationSorter;
import com.mobile.instagram.util.PostTimeSorter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
>>>>>>> eric_branch

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUserFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUserFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
<<<<<<< HEAD
public class FragmentUserFeed extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {

            "android.permission.WRITE_EXTERNAL_STORAGE" };


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

   // protected static final String TAG = FragmentUserFeed.class.getSimpleName();
    private ListView mCircleLv;
   // private SwipeRefreshLayout mSwipeRefreshLayout;
    private CircleAdapter mAdapter;
    private LinearLayout mEditTextBody;
    private EditText mEditText;
    private TextView sendTv;

    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private CirclePublicCommentContral mCirclePublicCommentContral;

=======
public class FragmentUserFeed extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private ArrayList<Post> posts;

    private User currentUser;

    private RecyclerView recyclerView;


    private PostAdapter pa;
>>>>>>> eric_branch

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
<<<<<<< HEAD


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
=======
>>>>>>> eric_branch
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
<<<<<<< HEAD
        View view=inflater.inflate(R.layout.fragment_userfeed, container, false);
        mCircleLv = (ListView) view.findViewById(R.id.circleLv);
        mEditTextBody = (LinearLayout)view.findViewById(R.id.editTextBodyLl);
        mEditText = (EditText) view.findViewById(R.id.circleEt);
        sendTv = (TextView) view.findViewById(R.id.sendTv);
        initView();
        loadData();

=======
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
>>>>>>> eric_branch
        return view;
    }

    @SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
    private void initView() {
        //  mCircleLv.setOnScrollListener(new SwpipeListViewOnScrollListener(mSwipeRefreshLayout));
        //  mCircleLv = (ListView) findViewById(R.id.circleLv);
        mCircleLv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {/*一系列与标触摸事件发生位置相关的函数*/
                if (mEditTextBody.getVisibility() == View.VISIBLE) {
                    mEditTextBody.setVisibility(View.GONE);/*设置控件隐藏,不可见也不占用空间*/
                    CommonUtils.hideSoftInput(FragmentUserFeed.this.getActivity(), mEditText);/*隐藏软键盘*/
                    return true;
                }
                return false;
            }
        });
        //  mSwipeRefreshLayout.setOnRefreshListener(this);
        //  mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
        //   android.R.color.holo_orange_light, android.R.color.holo_red_light)

            mAdapter = new CircleAdapter(this.getActivity());
            mCircleLv.setAdapter(mAdapter);

       /* mEditTextBody = (LinearLayout) findViewById(R.id.editTextBodyLl);
        mEditText = (EditText) findViewById(R.id.circleEt);
        sendTv = (TextView) findViewById(R.id.sendTv);*/

            mCirclePublicCommentContral = new CirclePublicCommentContral(this.getActivity(), mEditTextBody, mEditText, sendTv);
            mCirclePublicCommentContral.setmListView(mCircleLv);
            mAdapter.setCirclePublicCommentContral(mCirclePublicCommentContral);

            // setViewTreeObserver();
        }

  /*  private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = mSwipeRefreshLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mSwipeRefreshLayout.getWindowVisibleDisplayFrame(r);
                int screenH = mSwipeRefreshLayout.getRootView().getHeight();
                int keyH = screenH - (r.bottom - r.top);
                if(keyH == MyApplication.mKeyBoardH){//有变化时才处理，否则会陷入死循环
                    return;
                }
                Log.d(TAG, "keyH = " + keyH + " &r.bottom=" + r.bottom + " &top=" + r.top);
                MyApplication.mKeyBoardH = keyH;
                mScreenHeight = screenH;//应用屏幕的高度
                mEditTextBodyHeight = mEditTextBody.getHeight();
                if(mCirclePublicCommentContral != null){
                    mCirclePublicCommentContral.handleListViewScroll();
                }
            }
        });
    }*/
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void loadData() {
        List<CircleItem> datas = DatasUtil.createCircleDatas();
        mAdapter.setDatas(datas);
        mAdapter.notifyDataSetChanged();
    }

    public int getScreenHeight(){
        return mScreenHeight;
    }

    public int getEditTextBodyHeight(){
        return mEditTextBodyHeight;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(mEditTextBody != null && mEditTextBody.getVisibility() == View.VISIBLE){
                mEditTextBody.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
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

<<<<<<< HEAD
=======
    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
>>>>>>> eric_branch
}
