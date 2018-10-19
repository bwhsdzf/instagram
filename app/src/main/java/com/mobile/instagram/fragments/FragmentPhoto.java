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
public class FragmentPhoto extends Fragment implements  View.OnClickListener{

    private static final String TAG = "FragmentPhoto";
    private static final int REQUEST_GPS = 1;
    private LocationService ls;

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private TextView gpsX;
    private TextView gpsY;

    private OnFragmentInteractionListener mListener;

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
    public static FragmentPhoto newInstance() {
        FragmentPhoto fragment = new FragmentPhoto();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        gpsX = view.findViewById(R.id.gpsX);
        gpsY =view.findViewById(R.id.gpsY);
        view.findViewById(R.id.gpsTest).setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS);

            }
        }else{
            Log.d(TAG, "Permission on GPS granted");
            ls = LocationService.getLocationManager(this.getActivity());
        }
        return view;
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

            // other 'case' lines to check for other
            // permissions this app might request
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

    private void printGPS(){
        if (ls == null) ls = LocationService.getLocationManager(getActivity());
        double[] coor = ls.getCoordinates();
        gpsX.setText(Double.toString(coor[0]));
        gpsY.setText(Double.toString(coor[1]));
        System.out.println(ls.getCity(coor[0],coor[1]));
    }

    public void onClick(View view){
        int i = view.getId();
        if( i == R.id.gpsTest){
            printGPS();
        }
    }

}
