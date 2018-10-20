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
import com.mobile.instagram.models.relationalModels.UserPosts;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import com.mobile.instagram.activities.user_photo.PermissionsChecker;
import com.mobile.instagram.activities.user_photo.PermissionsActivity;
import com.mobile.instagram.activities.user_photo.CustomCamera;
import com.mobile.instagram.activities.user_photo.PrimaryColor;
import com.mobile.instagram.activities.user_photo.FileStorage;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Post Activity";

    private EditText postMessage;
    private ImageView photo;

    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private User userModel;
    private UserPosts userposts;

    private boolean hasPicture = false;
    private Button album,camera,post;
    private Uri imageUri;
    private Uri outputUri;
    private Bitmap bitmap;
    private String imagePath;
    private PermissionsChecker mPermissionsChecker; // permission detecter
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private boolean isClickCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        postMessage = findViewById(R.id.postMessage);
        photo = findViewById(R.id.postImage);
        album = (Button) findViewById(R.id.album);
        camera=(Button) findViewById(R.id.takephoto);
        post=(Button)findViewById(R.id.post);
        post.setOnClickListener(this);
        photo.setOnClickListener(this);
        album.setOnClickListener(this);
        camera.setOnClickListener(this);
        isClickCamera=true;
        mPermissionsChecker = new PermissionsChecker(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mDatabaseRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userModel = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseRef.child("user-posts").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserPosts up = dataSnapshot.getValue(UserPosts.class);
                userposts = up;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode==1) {

            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitKat(imageReturnedIntent);
            } else {
                handleImageBeforeKitKat(imageReturnedIntent);
            }
            cropPhoto();}
                /*if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                selectedImage);
                        photo.setImageBitmap(bitmap);
                        hasPicture = true;
                    } catch (Exception e){
                        Log.d(TAG, "context not found");
                    }
                }*/

        else if(requestCode==2){
            try{
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
            }catch(IOException e){
                e.printStackTrace();
            }
            photo.setImageBitmap(bitmap);
            hasPicture=true;
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //byte[] datas = baos.toByteArray();
            Intent intent=new Intent(this,PrimaryColor.class);
            intent.putExtra("uri",outputUri);
            startActivity(intent);
            finish();
        }


        else if(requestCode==3){
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                finish();
            }
            else {
                if (isClickCamera) {
                    startActivity(new Intent(this,CustomCamera.class));
                    finish();
                }
                else {
                    selectFromAlbum();
                }
            }}
    }


    private void uploadPost(){
        if(!validateForm()){
        }
        else{

            long time = Calendar.getInstance().getTimeInMillis();
            String uid = currentUser.getUid();
            String key = mDatabaseRef.child("posts").push().getKey();
            Post post = new Post(time,key,uid,postMessage.getText().toString(),"0",
                    new ArrayList<String>(),new ArrayList<Comment>());
            Map<String, Object> postValue = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("posts/"+key, postValue);
            childUpdates.put("user-posts/"+uid+"/"+key, postValue);
            mDatabaseRef.updateChildren(childUpdates);

            Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            Toast.makeText(PostActivity.this, "Uploading photo",
                    Toast.LENGTH_LONG).show();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageRef.child("post_images/" + key + ".jpg");
            UploadTask uploadTask = profileRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(PostActivity.this, "Failed to upload photo.",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostActivity.this, "Upload success",
                            Toast.LENGTH_LONG).show();
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
            Toast.makeText(PostActivity.this, "Please select a photo for the post.",
                    Toast.LENGTH_SHORT);
            validate = false;
        }

        return validate;
    }

    @Override
    public void onClick(View v){
        photo.setImageBitmap(bitmap);
        int i = v.getId();
        if (i == R.id.album){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                }
                else {
                    selectFromAlbum();
                }
            }
            else {
                selectFromAlbum();
            }
            isClickCamera = false;
            //Intent pickPhoto = new Intent(Intent.ACTION_PICK,
            //        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
        }
        else if (i == R.id.post){
            uploadPost();
        }
        else if(i==R.id.takephoto){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                }
                else {
                    startActivity(new Intent(this,CustomCamera.class));
                    finish();
                }
            }
            else {
                startActivity(new Intent(this,CustomCamera.class));
                finish();
            }
        }
    }

    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 98);
        intent.putExtra("aspectY", 99);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, 2);
    }

    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        imageUri = data.getData();
        if (DocumentsContract.isDocumentUri(this, imageUri)) {
            String docId = DocumentsContract.getDocumentId(imageUri);
            if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            imagePath = getImagePath(imageUri, null);
        }
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            imagePath = imageUri.getPath();
        }
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, 3, PERMISSIONS);
    }
}
