package com.mobile.instagram.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.mobile.instagram.models.User;
import com.mobile.instagram.util.LocationService;

import java.io.IOException;

public class NavigationActivity extends AppCompatActivity implements
        FragmentUserFeed.OnFragmentInteractionListener,
        FragmentDiscover.OnFragmentInteractionListener,
        FragmentPhoto.OnFragmentInteractionListener,
        FragmentActivityFeed.OnFragmentInteractionListener,
        FragmentProfile.OnFragmentInteractionListener
{

    private static final int REQUEST_GPS = 1;
    private LocationService ls;
    private User currentUser;
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
        Bundle b = getIntent().getBundleExtra("bundle");
        if (b!= null){
            currentUser = b.getParcelable("user");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();
        init();
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        showNav(R.id.navigation_user_feed);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode==1) { }
        else if(requestCode==2){ }}

        public static Context getContext(){return context; }


    private void init(){
        userFeed =FragmentUserFeed.newInstance(currentUser);
        discover =FragmentDiscover.newInstance(currentUser);
        photo =FragmentPhoto.newInstance(currentUser);
        activityFeed = FragmentActivityFeed.newInstance(currentUser);
        profile = FragmentProfile.newInstance(currentUser);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container,userFeed);
        ft.add(R.id.fragment_container,discover);
        ft.add(R.id.fragment_container,photo);
        ft.add(R.id.fragment_container,activityFeed);
        ft.add(R.id.fragment_container,profile);
        ft.hide(discover).hide(photo).hide(activityFeed).hide(profile);
        ft.commit();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS);

            }
        }else{
            ls = LocationService.getLocationManager(this);
        }
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

