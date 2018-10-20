package com.mobile.instagram.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.mobile.instagram.R;
import android.net.Uri;
import java.io.IOException;

public class ChangePixel extends Activity {

    private ImageView imageView;
    private Bitmap bitmap;
    private Bitmap bitmap2;
    private Uri uri;
    private Uri uri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepixel);
        Intent intent=getIntent();
        //if(intent!=null) {
        //    bitmap = intent.getParcelableExtra("bitmap");
        //}
        if(intent!=null){
            if(intent.getParcelableExtra("uri")!=null){
                uri=intent.getParcelableExtra("uri");
        }
        try{
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        }catch(IOException e){
            e.printStackTrace();
        }
        imageView = (ImageView) findViewById(R.id.picture);
        imageView.setImageBitmap(ImageHelper.handleImageNegative(bitmap));
        bitmap2=ImageHelper.handleImageNegative(bitmap);
        uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap2, null,null));
        Button finish;
        finish=(Button)findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChangePixel.this,LastActivity.class);
                intent.putExtra("uri",uri2);
                startActivity(intent);
                finish();
            }
        });

    }
}}
