package com.mobile.instagram.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.instagram.R;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.instagram.R;
import com.mobile.instagram.activities.FileStorage;
import com.mobile.instagram.models.Comment;
import com.mobile.instagram.models.Post;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.*;
import com.google.firebase.database.*;
import com.mobile.instagram.models.User;
import com.mobile.instagram.activities.PermissionsChecker;
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
import com.mobile.instagram.activities.CustomCamera;
import android.content.Context;
import com.mobile.instagram.activities.PrimaryColor;
import com.mobile.instagram.activities.PermissionsActivity;



import com.mobile.instagram.models.User;
import com.mobile.instagram.util.LocationService;

import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPhoto.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPhoto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhoto extends Fragment implements View.OnClickListener{
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private static final String TAG = "FragmentPhoto";
    private static final int REQUEST_GPS = 1;
    private LocationService ls;

    private User currentUser;

    private boolean hasPicture = false;
    private Button album,camera,post;
    private Uri imageUri;
    private Uri outputUri;
    private Bitmap bitmap;
    private String imagePath;
    private PermissionsChecker mPermissionsChecker; // permission detecter

    private OnFragmentInteractionListener mListener;
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private boolean isClickCamera;

    public FragmentPhoto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentPhoto.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPhoto newInstance(User currentUser) {
        FragmentPhoto fragment = new FragmentPhoto();
        fragment.currentUser = currentUser;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_photo, container, false);

         view.findViewById(R.id.button2).setOnClickListener(this);
         view.findViewById(R.id.button).setOnClickListener(this);


        isClickCamera=true;
        mPermissionsChecker = new PermissionsChecker(getActivity());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        return view;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode==1) {

            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitKat(imageReturnedIntent);
            } else {
                handleImageBeforeKitKat(imageReturnedIntent);
            }
            cropPhoto();}

        else if(requestCode==2){
            try{
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(outputUri));
            }catch(IOException e){
                e.printStackTrace();
            }
            Intent intent=new Intent(getActivity(),PrimaryColor.class);
            intent.putExtra("uri",outputUri);
            startActivity(intent);
            getActivity().finish();
        }


        else if(requestCode==3){
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                getActivity().finish();
            }
            else {
                if (isClickCamera) {
                    startActivity(new Intent(getActivity(),CustomCamera.class));
                    getActivity().finish();
                }
                else {
                    selectFromAlbum();
                }
            }}
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
        if (DocumentsContract.isDocumentUri(getActivity(), imageUri)) {
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), 3, PERMISSIONS);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ls = LocationService.getLocationManager(this.getActivity());
                } else {
                }
                return;
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.button2){
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
        else if(i==R.id.button){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                }
                else {
                    startActivity(new Intent(getActivity(),CustomCamera.class));
                }
            }
            else {
                startActivity(new Intent(getActivity(),CustomCamera.class));
            }
        }
    }

}
