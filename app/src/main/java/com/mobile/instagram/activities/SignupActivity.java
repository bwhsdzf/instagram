package com.mobile.instagram.activities;

import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.*;


import com.google.firebase.database.*;
import com.mobile.instagram.R;
import  com.mobile.instagram.models.User;

import java.io.*;

public class SignupActivity extends AppCompatActivity implements
    View.OnClickListener{

    private static final String TAG = "EmailPassword";

    private static final int SELECT_PHOTO  = 1;
    private static final int CROP_PHOTO = 0;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private ImageView mProfilePhoto;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mUsernameField = findViewById(R.id.username);
        mProfilePhoto = findViewById(R.id.profilePhoto);
        selectedPhoto = false;

        //[Button]
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.profilePhoto).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void createAccount(final String email, String password, final String username) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        Toast.makeText(SignupActivity.this, "Creating user",
                Toast.LENGTH_LONG).show();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            String userId = mAuth.getUid();
                            final User newUser = new User(userId, username, email,"",
                                    0,0);
                            Log.d(TAG, "User id is "+userId);
                            mDatabase.child("users").child(userId).setValue(newUser);
                            Log.d(TAG, "write to db success");
                            Toast.makeText(SignupActivity.this, "Successfully created user",
                                    Toast.LENGTH_SHORT).show();
                            if (selectedPhoto) {
                                mProfilePhoto.setDrawingCacheEnabled(true);
                                mProfilePhoto.buildDrawingCache();
                                Bitmap bitmap = ((RoundedBitmapDrawable) mProfilePhoto.getDrawable()).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                final byte[] data = baos.toByteArray();
                                Toast.makeText(SignupActivity.this, "Uploading profile photo",
                                        Toast.LENGTH_LONG).show();
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference profileRef = storageRef.child("profile_images/" + userId + ".jpg");
                                UploadTask uploadTask = profileRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(SignupActivity.this, "Failed to upload photo." +
                                                "Please try again in profile page", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!urlTask.isSuccessful());
                                        Uri downloadUrl = urlTask.getResult();
                                        newUser.setProfileUrl(downloadUrl.toString());
                                        mDatabase.child("users").child(newUser.getUid())
                                                .setValue(newUser);
                                        finish();
                                    }
                                });
                            }else{
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Create user unsuccessful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // [END create_user_with_email]
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    selectedPhoto = true;
                    Bundle b = imageReturnedIntent.getExtras();
                    Bitmap bitmap = b.getParcelable("data");
                    setCircleProfile(bitmap);
                }

                break;
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    doCrop(imageReturnedIntent.getData());
                }
                break;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if(password.length() < 6){
            mPasswordField.setError("Password length must be longer than 6");
            valid = false;
        }else{
            mPasswordField.setError(null);
        }

        String username = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError("Required.");
            valid = false;
        } else {
            mUsernameField.setError(null);
        }

        return valid;
    }

    private void doCrop(Uri uri){
        //call the standard crop action intent (the user device may not support it)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(uri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_PHOTO);
    }

    // Set the profile pic to circular
    private void setCircleProfile(Bitmap bitmap){
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        mProfilePhoto.setImageDrawable(mDrawable);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString(),
                    mUsernameField.getText().toString());
            Log.d(TAG, "creating account");
        }else if (i == R.id.profilePhoto){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , SELECT_PHOTO);//one can be replaced with any action code
        }
    }
}
