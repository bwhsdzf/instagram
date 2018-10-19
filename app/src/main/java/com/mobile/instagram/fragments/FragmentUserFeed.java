package com.mobile.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.instagram.R;
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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUserFeed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUserFeed#newInstance} factory method to
 * create an instance of this fragment.
 */
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


    public FragmentUserFeed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUserFeed.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUserFeed newInstance(String param1, String param2) {
        FragmentUserFeed fragment = new FragmentUserFeed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_userfeed, container, false);
        mCircleLv = (ListView) view.findViewById(R.id.circleLv);
        mEditTextBody = (LinearLayout)view.findViewById(R.id.editTextBodyLl);
        mEditText = (EditText) view.findViewById(R.id.circleEt);
        sendTv = (TextView) view.findViewById(R.id.sendTv);
        initView();
        loadData();

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

}
