package com.mobile.instagram;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.database.*;
import com.mobile.instagram.models.DatabaseCon;
import  com.mobile.instagram.models.User;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity implements
    View.OnClickListener{

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mUsernameField = findViewById(R.id.username);

        //[Button]
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);

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
                            User newUser = new User(userId, username, email, new ArrayList<String>(),
                                    new ArrayList<String>(), new ArrayList<String>());
                            Log.d(TAG, "User id is "+userId);
                            Log.d(TAG, "write to db success 1");
                            mDatabase.child("users").child(userId).setValue(newUser);
                            Log.d(TAG, "write to db success 2");
                            Toast.makeText(SignupActivity.this, "Successfully created user",
                                    Toast.LENGTH_SHORT).show();
                            finish();
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
        }
    }
}
