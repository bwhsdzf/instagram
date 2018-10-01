package com.mobile.instagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;
import com.google.firebase.auth.*;

import com.mobile.instagram.models.DatabaseCon;
import com.mobile.instagram.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "Fragment Profile";

    private OnFragmentInteractionListener mListener;

    private FirebaseUser currentUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DatabaseCon dc;



    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mAuth Firebase Auth instance.
     * @param mDatabase Firebase database reference.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(FirebaseAuth mAuth, DatabaseReference mDatabase)
        {
        FragmentProfile fragment = new FragmentProfile();
        fragment.mAuth = mAuth;
        fragment.mDatabase = mDatabase;
        fragment.currentUser = mAuth.getCurrentUser();
        fragment.dc = new DatabaseCon(mDatabase);
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
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.signOutButton).setOnClickListener(this);
        String uid = currentUser.getUid();

//        TextView tv = view.findViewById(R.id.userName);
        Log.d(TAG, dc.getUsername(uid).toString());
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

    private void signOut(){
        if(this.mAuth == null){
            Log.d(TAG, "mAuth is null");
        }else {
            mAuth.signOut();
            Toast.makeText(this.getActivity(), "Logged out",
                    Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signOutButton) {
            signOut();
        }
    }
}
