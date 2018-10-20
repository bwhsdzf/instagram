package com.mobile.instagram.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera;
import android.content.Intent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import android.graphics.ImageFormat;
import com.mobile.instagram.R;
import android.widget.Button;

public class CustomCamera extends Activity implements SurfaceHolder.Callback {
    private Camera mCamera;
    private SurfaceView mPreView;
    private SurfaceHolder mHolder;
    private GridLine gl;
    private Button switchon,switchoff;
    private Camera.PictureCallback mPictureCallback=new Camera.PictureCallback(){
        public void onPictureTaken(byte[] data, Camera camera){
            File tempFile=new File("/sdcard/temp.png");
            try{
                FileOutputStream fos=new FileOutputStream(tempFile);
                fos.write(data);
                fos.close();
                Intent intent=new Intent(CustomCamera.this,ResultActivity.class);
                intent.putExtra("picPath",tempFile.getAbsolutePath());
                startActivity(intent);
                CustomCamera.this.finish();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

        }
    };
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.custom);
        mPreView=(SurfaceView)findViewById(R.id.preview);
        mHolder= mPreView.getHolder();
        mHolder.addCallback(this);
        mPreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.autoFocus(null);
            }
        });
        gl=(GridLine)findViewById(R.id.grid);
        switchon=(Button)findViewById(R.id.on);
        switchoff=(Button)findViewById(R.id.off);

        switchon.setOnClickListener(new View.OnClickListener() {


            public void onClick(View arg0) {
                gl.setInf(360, 360, 1280, 1280);
            }
        });
        switchoff.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                gl.clearLine();
            }
        });


    }

    public void capture(View view){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(1920,1080);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b){
                    mCamera.takePicture(null,null,mPictureCallback);
                }
            }
        });
    }
    public void capture2(View view){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(1920,1080);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b){
                    mCamera.takePicture(null,null,mPictureCallback);
                }
            }
        });
    }
    protected  void onResume(){
        super.onResume();
        if(mCamera==null){
            mCamera=getCamera();
            if(mHolder!=null){
                setStartPreview(mCamera,mHolder);
            }
        }
    }
    protected  void onPause(){
        super.onPause();
        releaseCamera();
    }
    private Camera getCamera(){
        Camera camera;
        try{
            camera=Camera.open();
        } catch(Exception e){
            camera=null;
            e.printStackTrace();
        }
        return camera;
    }

    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try{
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void releaseCamera(){
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreView.getHolder().removeCallback(this);
            mCamera.release();
            mCamera=null;
        }

    }

    public void surfaceCreated(SurfaceHolder holder){
        setStartPreview(mCamera,mHolder);
    }

    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
        mCamera.stopPreview();
        setStartPreview(mCamera,mHolder);
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        releaseCamera();
    }
}


