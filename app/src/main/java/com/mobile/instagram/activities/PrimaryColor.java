package com.mobile.instagram.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.content.Intent;
import com.mobile.instagram.R;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;
import android.os.Build;

public class PrimaryColor extends Activity implements SeekBar.OnSeekBarChangeListener,View.OnClickListener {

    private ImageView mImageView;
    private SeekBar mSeekbarhue,mSeekbarSaturation, mSeekbarLum;
    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    private float mHue,mStauration, mLum;
    private Bitmap bitmap;
    private Bitmap bitmapbitmap;
    private Uri uri;
    private Uri uri2;
    private boolean sign=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brightness_etc);
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
        mImageView = (ImageView) findViewById(R.id.imageView_b);
        mSeekbarhue = (SeekBar) findViewById(R.id.seekbarHue3);
        mSeekbarSaturation = (SeekBar) findViewById(R.id.seekbarSaturation3);
        mSeekbarLum = (SeekBar) findViewById(R.id.seekbatLum3);
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
        }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbarHue3:
                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;
                break;
            case R.id.seekbarSaturation3:
                mStauration = progress * 1.0F / MID_VALUE;
                break;
            case R.id.seekbatLum3:
                mLum = progress * 1.0F / MID_VALUE;
                break;
        }
        mImageView.setImageBitmap(ImageHelper.handleImageEffect(bitmap, mHue, mStauration, mLum));
        Bitmap bitmap2=ImageHelper.handleImageEffect(bitmap, mHue, mStauration, mLum);
        uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap2, null,null));

        Button r= findViewById(R.id.reset_b);
        r.setOnClickListener(this);
        Button c=findViewById(R.id.store_continue_b);
        c.setOnClickListener(this);


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.reset_b){
            mSeekbarhue.setProgress(MID_VALUE);
            mSeekbarSaturation.setProgress(MID_VALUE);
            mSeekbarLum.setProgress(MID_VALUE);
        }
        else if(i==R.id.store_continue_b){
            Intent intent= new Intent(this,ChangePixel.class);
            intent.putExtra("uri",uri2);
            startActivity(intent);
            finish();
        }
    }
}
