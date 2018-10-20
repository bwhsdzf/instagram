package com.mobile.instagram.activities.user_photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mobile.instagram.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ResultActivity extends Activity {
    private Uri outputUri;
    private Uri imageUri;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
                String path = getIntent().getStringExtra("picPath");
        imageUri = Uri.fromFile(new File(path));
        ImageView imageView = (ImageView) findViewById(R.id.pic);
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap2 = bitmap;
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Button a;
        a=(Button)findViewById(R.id.finishcapture);
        a.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
           Intent intent=new Intent(ResultActivity.this,PrimaryColor.class);
             intent.putExtra("bitmap",bitmap2);
               startActivity(intent);
               finish();
             }
           });
        }
    }
    /*public void onClick(View v){
        int i=v.getId();
        if(i==R.id.finishcapture){
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode,resultCode,imageReturnedIntent);
        switch(requestCode){
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle b=imageReturnedIntent.getExtras();
                    bitmap3=b.getParcelable("data");
                }
                break;
            case 1:
                if(resultCode==RESULT_OK){
                    doCrop(imageReturnedIntent.getData());
                }
                break;
        }
        Intent intent=new Intent(this,PrimaryColor.class);
        intent.putExtra("bitmap",bitmap3);
        startActivity(intent);

    }

    private void doCrop(Uri uri){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 2);
    }*/

