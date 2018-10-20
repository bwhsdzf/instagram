package com.mobile.instagram.activities;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.View;
import android.content.Intent;
import android.graphics.Matrix;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.net.Uri;
import com.mobile.instagram.R;

public class ResultActivity extends Activity/* implements View.OnClickListener*/{
    private Uri outputUri;
    private Uri imageUri;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private Bitmap bitmap4;
    private Uri uri;
    private Uri uri2;
    //private Button a;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        //a=(Button)findViewById(R.id.finishcapture);
        //a.setOnClickListener(ResultActivity.this);
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
            imageView.setImageBitmap(bitmap2);
            //bitmap4 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            //uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap4, null,null));
            //System.out.println("333");
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

    /*public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.finishcapture) {
            System.out.println("222");
            Intent intent = new Intent();

            intent.putExtra("uri",uri2);
            System.out.println("222222");
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle b = imageReturnedIntent.getExtras();
                    bitmap3 = b.getParcelable("data");
                    outputUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap3, null,null));
                    Intent intent=new Intent(this,PrimaryColor.class);
                    intent.putExtra("uri",outputUri);
                    startActivity(intent);
                    finish();
                }
                break;
            case 1:
                System.out.println("111");
                Intent intent=getIntent();
                uri=intent.getParcelableExtra("uri");
                doCrop(uri);
                break;
        }
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

