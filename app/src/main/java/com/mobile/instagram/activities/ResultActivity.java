package com.mobile.instagram.activities;

import android.graphics.BitmapFactory;
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


public class ResultActivity extends Activity implements View.OnClickListener{
    private Uri outputUri;
    private Uri olduri;
    private Bitmap bitmap2;
    private Uri uri;
    private Uri uri2;
    private Button a;
    private ImageView imageView;
    private String name="test";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        a = (Button) findViewById(R.id.finishcapture);
        a.setOnClickListener(ResultActivity.this);
        String path = getIntent().getStringExtra("picPath");
        olduri = Uri.fromFile(new File(path));
        imageView = (ImageView) findViewById(R.id.pic);
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap2 = bitmap;
            imageView.setImageBitmap(bitmap);
            System.out.println("111");
            //saveMyBitmap(bitmap2,name);
            //bitmap4 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap2, null, null));
            //System.out.println("333");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
<<<<<<< HEAD
        }

      
=======

>>>>>>> 6828406a6c4b57561f87868b447f1173bd0d4f5e
        }
    }
        public void onClick (View v){
            int i = v.getId();
            if (i == R.id.finishcapture) {
                cropPhoto(uri2);
            }
        }

        protected void onActivityResult ( int requestCode, int resultCode, Intent
        imageReturnedIntent){
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case 2:
                    if (resultCode == RESULT_OK) {
                        Intent intent = new Intent(this, PrimaryColor.class);
                        intent.putExtra("uri", outputUri);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }

        private void cropPhoto (Uri imageUri){
            File file = new FileStorage().createCropFile();
            outputUri = Uri.fromFile(file);
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "image/*");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 98);
            intent.putExtra("aspectY", 99);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            startActivityForResult(intent, 2);
        }
    }