package com.mobile.instagram;

import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.*;


import com.google.firebase.database.*;
import  com.mobile.instagram.models.User;

import java.util.ArrayList;
import java.io.*;

public class SignupActivity extends AppCompatActivity implements
    View.OnClickListener{

    private static final String TAG = "EmailPassword";

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
                            ArrayList<String> followers = new ArrayList<String>();
                            followers.add("0");
                            ArrayList<String> following = new ArrayList<String>();
                            following.add("0");
                            ArrayList<String> post = new ArrayList<String>();
                            post.add("0");
                            User newUser = new User(userId, username, email, post,
                                    following, followers);
                            Log.d(TAG, "User id is "+userId);
                            Log.d(TAG, "write to db success 1");
                            mDatabase.child("users").child(userId).setValue(newUser);
                            Log.d(TAG, "write to db success 2");
                            Toast.makeText(SignupActivity.this, "Successfully created user",
                                    Toast.LENGTH_SHORT).show();

                            if (selectedPhoto) {
                                mProfilePhoto.setDrawingCacheEnabled(true);
                                mProfilePhoto.buildDrawingCache();
                                Bitmap bitmap = ((BitmapDrawable) mProfilePhoto.getDrawable()).getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                                        finish();
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Create user unsuccessful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        String userId = mAuth.getUid();


        // [END create_user_with_email]
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mProfilePhoto.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mProfilePhoto.setImageURI(selectedImage);
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
        } else {
            mPasswordField.setError(null);
        }

        String username = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mUsernameField.setError("Required.");
            valid = false;
        } else {
            mUsernameField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString(),
                    mUsernameField.getText().toString());
            Log.d(TAG, "creating account");
        }else if (i == R.id.profilePhoto){
            selectedPhoto = true;
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
        }
    }
}
