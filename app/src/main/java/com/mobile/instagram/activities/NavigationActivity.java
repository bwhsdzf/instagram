package com.mobile.instagram.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.content.Context;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.instagram.fragments.FragmentActivityFeed;
import com.mobile.instagram.fragments.FragmentDiscover;
import com.mobile.instagram.fragments.FragmentPhoto;
import com.mobile.instagram.fragments.FragmentProfile;
import com.mobile.instagram.fragments.FragmentUserFeed;
import com.mobile.instagram.R;

public class NavigationActivity extends AppCompatActivity implements
        FragmentUserFeed.OnFragmentInteractionListener,
        FragmentDiscover.OnFragmentInteractionListener,
        FragmentPhoto.OnFragmentInteractionListener,
        FragmentActivityFeed.OnFragmentInteractionListener,
        FragmentProfile.OnFragmentInteractionListener
{

    private FragmentUserFeed userFeed;
    private FragmentDiscover discover;
    private FragmentPhoto photo;
    private FragmentActivityFeed activityFeed;
    private FragmentProfile profile;
    private BottomNavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_user_feed:
                    showNav(R.id.navigation_user_feed);
                    return true;
                case R.id.navigation_discover:
                    showNav(R.id.navigation_discover);
                    return true;
                case R.id.navigation_photo:
                    showNav(R.id.navigation_photo);
                    return true;
                case R.id.navigation_activity_feed:
                    showNav(R.id.navigation_activity_feed);
                    return true;
                case R.id.navigation_profile:
                    showNav(R.id.navigation_profile);
                    return true;
            }
            return false;
        }

    };

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();
        init();
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        showNav(R.id.navigation_user_feed);
    }

    public static Context getContext(){
        return context;
    }


    private void init(){
        userFeed =new FragmentUserFeed();
        discover =new FragmentDiscover();
        photo =new FragmentPhoto();
        activityFeed = FragmentActivityFeed.newInstance();
        profile = FragmentProfile.newInstance(mAuth,mDatabase);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container,userFeed);
        ft.add(R.id.fragment_container,discover);
        ft.add(R.id.fragment_container,photo);
        ft.add(R.id.fragment_container,activityFeed);
        ft.add(R.id.fragment_container,profile);
        ft.hide(discover).hide(photo).hide(activityFeed).hide(profile);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    @Override
    public void onBackPressed(){
        return;
    }

    private void showNav(int navid){
        FragmentTransaction beginTransaction=getSupportFragmentManager().beginTransaction();
        switch (navid){
            case R.id.navigation_user_feed:
                beginTransaction.hide(discover).hide(photo).hide(activityFeed).hide(profile);
                beginTransaction.show(userFeed);
                beginTransaction.commit();
                break;
            case R.id.navigation_discover:
                beginTransaction.hide(userFeed).hide(photo).hide(activityFeed).hide(profile);
                beginTransaction.show(discover);
                beginTransaction.commit();
                break;
            case R.id.navigation_photo:
                beginTransaction.hide(userFeed).hide(discover).hide(activityFeed).hide(profile);
                beginTransaction.show(photo);
//                navigationView.setSelectedItemId(R.id.navigation_photo);
                beginTransaction.commit();
                break;
            case R.id.navigation_activity_feed:
                beginTransaction.hide(userFeed).hide(discover).hide(photo).hide(profile);
                beginTransaction.show(activityFeed);
                beginTransaction.commit();
                break;
            case R.id.navigation_profile:
                beginTransaction.hide(userFeed).hide(discover).hide(activityFeed).hide(photo);
                beginTransaction.show(profile);
                beginTransaction.commit();
                break;
        }
    }

}

