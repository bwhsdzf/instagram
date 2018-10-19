package com.mobile.instagram.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;

    private ProgressBar progress;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mobile.instagram.R.layout.activity_login);

        // Views
        mEmailField = findViewById(com.mobile.instagram.R.id.email);
        mPasswordField = findViewById(com.mobile.instagram.R.id.password);
        progress = findViewById(R.id.login_progress);
        progress.setVisibility(View.GONE);

        // Buttons
        findViewById(com.mobile.instagram.R.id.emailSignInButton).setOnClickListener(this);
        findViewById(com.mobile.instagram.R.id.signUpButton).setOnClickListener(this);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        ImageLoader.getInstance().init(getDefaultConfig());
    }

    private ImageLoaderConfiguration getDefaultConfig() {
        ImageLoaderConfiguration config = ImageLoaderConfiguration
                .createDefault(getApplicationContext());
        return config;
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Log.d("LoginActivity", "Already logged in");
            toNavigation(currentUser.getEmail());
        }
    }
    // [END on_start_check_user]

    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        progress.setVisibility(View.VISIBLE);

        Toast.makeText(LoginActivity.this, "Signing in",
                Toast.LENGTH_LONG).show();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            toNavigation(email);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
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

        return valid;
    }
    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
        if (user != null) {
            findViewById(com.mobile.instagram.R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(com.mobile.instagram.R.id.password).setVisibility(View.GONE);
            findViewById(com.mobile.instagram.R.id.emailSignInButton).setVisibility(View.VISIBLE);
        } else {
            findViewById(com.mobile.instagram.R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(com.mobile.instagram.R.id.password).setVisibility(View.VISIBLE);
        }
    }

    public void toNavigation(String username){
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        intent.putExtra("Username",username);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == com.mobile.instagram.R.id.signUpButton) {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }
}

