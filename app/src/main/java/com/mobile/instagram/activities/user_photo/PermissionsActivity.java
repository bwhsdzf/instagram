package com.mobile.instagram.activities.user_photo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.mobile.instagram.R;

public class PermissionsActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED=0;
    public static final int PERMISSIONS_DENIED=1;
    private static final int PERMISSION_REQUEST_CODE=0;
    private static final String EXTRA_PERMISSIONS = "extra_permission"; // parameter
    private static final String PACKAGE_URL="package:"; // to package url
    private PermissionsChecker mChecker; // permission detecter
    private boolean isRequireCheck; // sign for permission

    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("run out of time");
        }
        setContentView(R.layout.activity_permissions);
        mChecker = new PermissionsChecker(this);
        isRequireCheck = true;
    }

    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            String[] permissions = getPermissions();
            if (mChecker.lacksPermissions(permissions)) {
                requestPermissions(permissions);
            }
            else {
                allPermissionsGranted();
            }
        }
        else {
            isRequireCheck = true;
        }
    }

    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    //low version
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            allPermissionsGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
        builder.setTitle("help");
        builder.setMessage("lack of necessary permission");
        builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });
        builder.setPositiveButton("configuration", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL+getPackageName()));
        startActivity(intent);
    }
}

