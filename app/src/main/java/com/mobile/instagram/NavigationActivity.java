package com.mobile.instagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import android.app.FragmentTransaction;

public class NavigationActivity extends AppCompatActivity implements
        FragmentUserFeed.OnFragmentInteractionListener,
        FragmentDiscover.OnFragmentInteractionListener,
        FragmentPhoto.OnFragmentInteractionListener,
        FragmentActivityFeed.OnFragmentInteractionListener,
        FragmentProfile.OnFragmentInteractionListener
{

    private TextView mTextMessage;
    private FragmentUserFeed userFeed;
    private FragmentDiscover discover;
    private FragmentPhoto photo;
    private FragmentActivityFeed activityFeed;
    private FragmentProfile profile;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_user_feed:
                    mTextMessage.setText(R.string.title_user_feed);
                    showNav(R.id.navigation_user_feed);
                    return true;
                case R.id.navigation_discover:
                    mTextMessage.setText(R.string.title_discover);
                    showNav(R.id.navigation_discover);
                    return true;
                case R.id.navigation_photo:
                    mTextMessage.setText(R.string.title_photo);
                    showNav(R.id.navigation_photo);
                    return true;
                case R.id.navigation_activity_feed:
                    mTextMessage.setText(R.string.title_activity_feed);
                    showNav(R.id.navigation_activity_feed);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    showNav(R.id.navigation_profile);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        init();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //init（）用来初始化组件
    private void init(){
        userFeed =new FragmentUserFeed();
        discover =new FragmentDiscover();
        photo =new FragmentPhoto();
        activityFeed = new FragmentActivityFeed();
        profile = new FragmentProfile();
        FragmentTransaction beginTransaction=getFragmentManager().beginTransaction();
        beginTransaction.add(R.id.content, userFeed).add(R.id.content, discover).add(R.id.content, photo).add(R.id.content, activityFeed).add(R.id.content, profile);
        beginTransaction.hide(userFeed).hide(discover).hide(photo).hide(activityFeed).hide(profile);//隐藏fragment
        beginTransaction.addToBackStack(null);//返回到上一个显示的fragment
        beginTransaction.commit();//每一个事务最后操作必须是commit（），否则看不见效果
        showNav(R.id.navigation_user_feed);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private void showNav(int navid){
        FragmentTransaction beginTransaction=getFragmentManager().beginTransaction();
        switch (navid){
            case R.id.navigation_user_feed:
                beginTransaction.hide(discover).hide(photo).hide(activityFeed).hide(profile);
                beginTransaction.show(userFeed);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
                break;
            case R.id.navigation_discover:
                beginTransaction.hide(userFeed).hide(photo).hide(activityFeed).hide(profile);
                beginTransaction.show(discover);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
                break;
            case R.id.navigation_photo:
                beginTransaction.hide(discover).hide(userFeed).hide(activityFeed).hide(profile);
                beginTransaction.show(photo);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
                break;
            case R.id.navigation_activity_feed:
                beginTransaction.hide(discover).hide(userFeed).hide(photo).hide(profile);
                beginTransaction.show(activityFeed);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
                break;
            case R.id.navigation_profile:
                beginTransaction.hide(discover).hide(userFeed).hide(photo).hide(activityFeed);
                beginTransaction.show(profile);
                beginTransaction.addToBackStack(null);
                beginTransaction.commit();
                break;
        }
    }

}

