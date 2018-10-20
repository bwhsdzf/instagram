package com.mobile.instagram.activities.user_photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.mobile.instagram.R;

import java.io.IOException;

public class PrimaryColor extends Activity implements SeekBar.OnSeekBarChangeListener{

    private ImageView mImageView;
    private SeekBar mSeekbarhue,mSeekbarSaturation, mSeekbarLum;
    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    private float mHue,mStauration, mLum;
    private Bitmap bitmap;
    private Uri uri;
    private Uri uri2;
    private boolean sign=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_color);
        Intent intent=getIntent();
        //if(intent!=null) {
        //    bitmap = intent.getParcelableExtra("bitmap");
        //}
        if(intent!=null){
            if(intent.getParcelableExtra("uri")!=null){
                sign=true;
                uri=intent.getParcelableExtra("uri");
                try{
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            else{
                bitmap=intent.getParcelableExtra("bitmap");
            }
        }
        //byte[] x=intent.getByteArrayExtra("bitmap");
        //bitmap=BitmapFactory.decodeByteArray(x, 0, x.length);
        mImageView = (ImageView) findViewById(R.id.imageview);
        mSeekbarhue = (SeekBar) findViewById(R.id.seekbarHue);
        mSeekbarSaturation = (SeekBar) findViewById(R.id.seekbarSaturation);
        mSeekbarLum = (SeekBar) findViewById(R.id.seekbatLum);
        mSeekbarhue.setOnSeekBarChangeListener(this);
        mSeekbarSaturation.setOnSeekBarChangeListener(this);
        mSeekbarLum.setOnSeekBarChangeListener(this);
        mSeekbarhue.setMax(MAX_VALUE);
        mSeekbarSaturation.setMax(MAX_VALUE);
        mSeekbarLum.setMax(MAX_VALUE);
        mSeekbarhue.setProgress(MID_VALUE);
        mSeekbarSaturation.setProgress(MID_VALUE);
        mSeekbarLum.setProgress(MID_VALUE);
        mImageView.setImageBitmap(bitmap);
        uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        Button a;
        a=(Button)findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrimaryColor.this,ChangePixel.class);
                intent.putExtra("uri",uri2);
                startActivity(intent);
                finish();
            }
        });
        Button b;
        b=(Button)findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrimaryColor.this,ChangePixel2.class);
                intent.putExtra("uri",uri2);
                startActivity(intent);
                finish();
            }
        });
        Button c;
        c=(Button)findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrimaryColor.this,ChangePixel3.class);
                intent.putExtra("uri",uri2);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbarHue:
                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;
                break;
            case R.id.seekbarSaturation:
                mStauration = progress * 1.0F / MID_VALUE;
                break;
            case R.id.seekbatLum:
                mLum = progress * 1.0F / MID_VALUE;
                break;
        }
        mImageView.setImageBitmap(ImageHelper.handleImageEffect(bitmap, mHue, mStauration, mLum));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
