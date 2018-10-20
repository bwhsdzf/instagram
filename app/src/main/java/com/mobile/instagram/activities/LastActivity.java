package com.mobile.instagram.activities;

import com.google.firebase.auth.FirebaseUser;
import com.mobile.instagram.R;
import com.mobile.instagram.models.Comment;
import com.mobile.instagram.models.Post;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.*;
import com.google.firebase.database.*;
import com.mobile.instagram.models.User;
import com.mobile.instagram.util.LocationService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;

public class LastActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Post Activity";

    private EditText postMessage;
    private ImageView photo;

    private User currentUser;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private Uri uri;
    private Bitmap bitmap;
    private boolean hasPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_activity);
        postMessage = findViewById(R.id.postMessage);
        photo = findViewById(R.id.postImage);

        findViewById(R.id.postButton).setOnClickListener(this);
        photo.setOnClickListener(this);

        Intent intent=getIntent();
        if(intent!=null) {
            uri = intent.getParcelableExtra("uri");
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        photo.setImageBitmap(bitmap);
        hasPicture=true;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentUser = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                selectedImage);
                        photo.setImageBitmap(bitmap);
                        hasPicture = true;
                    } catch (Exception e){
                        Log.d(TAG, "context not found");
                    }
                }
                break;
        }
    }

    private void uploadPost(){
        if(!validateForm()){
        }
        else{
            final long time = Calendar.getInstance().getTimeInMillis();
            final String uid = currentUser.getUid();
            final String key = mDatabaseRef.child("posts").push().getKey();
            final double[] coor = LocationService.getCoordinates();

            Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            Toast.makeText(this, "Uploading photo",
                    Toast.LENGTH_LONG).show();
            StorageReference profileRef = mStorageRef.child("post_images/" + key + ".jpg");
            UploadTask uploadTask = profileRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(LastActivity.this, "Failed to upload photo.",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(LastActivity.this, "Upload success",
                            Toast.LENGTH_LONG).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    String downloadUrl = urlTask.getResult().toString();
                    Post post = new Post(time,key,uid,postMessage.getText().toString(),
                            downloadUrl,coor[0],coor[1],
                            new ArrayList<String>(),new ArrayList<Comment>());
                    Map<String, Object> postValue = post.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("posts/"+key, postValue);
                    childUpdates.put("user-posts/"+uid+"/"+key, postValue);
                    mDatabaseRef.updateChildren(childUpdates);
                    finish();
                }
            });
        }
    }

    private boolean validateForm(){
        boolean validate = true;

        String content = postMessage.getText().toString();
        if (TextUtils.isEmpty(content)) {
            postMessage.setError("Required.");
            validate = false;
        } else {
            postMessage.setError(null);
        }

        if (!hasPicture){
            Toast.makeText(LastActivity.this, "Please select a photo for the post.",
                    Toast.LENGTH_SHORT);
            validate = false;
        }

        return validate;
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        //if (i == R.id.postImage){
           // Intent pickPhoto = new Intent(Intent.ACTION_PICK,
             //       android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
         if (i == R.id.postButton){
            uploadPost();
        }
    }
}