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

public class ChangePixel extends Activity implements View.OnClickListener {

    private ImageView imageView;
    private Bitmap bitmap;
    private Bitmap bitmap2;
    private Uri uri;
    private Uri uri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);
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
        imageView = (ImageView) findViewById(R.id.imageView_f);

            imageView.setImageBitmap((bitmap));

            Button r_f= findViewById(R.id.reset_f);
            r_f.setOnClickListener(this);

            Button f1= findViewById(R.id.fi_1);
            f1.setOnClickListener(this);

            Button f2= findViewById(R.id.fi_2);
            f2.setOnClickListener(this);

            Button f3= findViewById(R.id.fi_3);
            f3.setOnClickListener(this);

            Button sc= findViewById(R.id.store_continue_f);
            sc.setOnClickListener(this);

          /*  imageView.setImageBitmap(ImageHelper.handleImageNegative(bitmap));
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
*/
    }
}

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.reset_f){
            imageView.setImageBitmap(bitmap);
        }
        else if(i==R.id.fi_1){
            imageView.setImageBitmap(ImageHelper.handleImageNegative(bitmap));
            uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), ImageHelper.handleImageNegative(bitmap), null,null));
        }
        else if(i==R.id.fi_2){
            imageView.setImageBitmap(ImageHelper.handleImagePixelsOldPhoto(bitmap));
            uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), ImageHelper.handleImagePixelsOldPhoto(bitmap), null,null));

        } else if(i==R.id.fi_3){
            imageView.setImageBitmap(ImageHelper.handleImagePixelsRelief(bitmap));
            uri2 = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), ImageHelper.handleImagePixelsRelief(bitmap), null,null));

        }else if(i==R.id.store_continue_f){
            uri2=uri;
            Intent intent= new Intent(this,LastActivity.class);
            intent.putExtra("uri",uri2);
            startActivity(intent);
            finish();
        }
    }


}
