package com.mobile.instagram.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.instagram.R;
import com.mobile.instagram.models.Post;
import com.mobile.instagram.models.User;

import java.lang.ref.Reference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDiscover.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDiscover#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDiscover extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String Tag = "Fragment Discover";
    private ValueEventListener ValueEventListener;


    // TODO: Rename and change types of parameters
    private SearchView search;
    private AutoCompleteTextView input;
    private ImageView deletbut;
    private Button nextbut;

    private ListView view;
    private DatabaseReference database;
    private StorageReference storage;
    private FirebaseAuth firebase;

    private String name;
    private String id;
    private String Url;
    private String email;
    private Image profile;




//    private ImageView myv;

    private OnFragmentInteractionListener mListener;

    public FragmentDiscover() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FragmentDiscover.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDiscover newInstance(FirebaseAuth firebase, DatabaseReference database) {
        FragmentDiscover fragment = new FragmentDiscover();
//        Bundle args = new Bundle();
        fragment.firebase=firebase;
        fragment.database=database;
        fragment.storage = FirebaseStorage.getInstance().getReference();
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
        View my = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        search = my.findViewById(R.id.search);
        view = my.findViewById(R.id.list);
        String key = (String) search.getQuery();


        Query searchfriend = database.child("user").child("username").equalTo(key);
        searchfriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(Tag, user.getUid());
                    String email = user.getEmail();
                    String name = user.getUsername();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        StorageReference sre = storage.child("profile_images/"+uid+".jpg");
//        final long ONE_MEGABYTE = 2048 * 2048;
//
//        sre.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                setCircleProfile(bitmap);
//                hasProfile = true;
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d(Tag, exception.getMessage());
//                hasProfile = false;
//            }
//        });

        return inflater.inflate(R.layout.fragment_discover, container, false);
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
}
