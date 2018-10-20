package com.mobile.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.instagram.activities.ProfileActivity;
import com.mobile.instagram.models.User;
import com.mobile.instagram.util.Recommendation;

import com.mobile.instagram.R;
import com.mobile.instagram.adapters.SearchListAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDiscover.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDiscover#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDiscover extends Fragment implements View.OnClickListener {

    private Recommendation recommendation;
    private ArrayList<User> result;
    private ArrayList<User> list;
    private User currentUser;

    private ArrayList<User> searchResult;

    private SearchView searchView;
    private ListView resultView;

    private SearchListAdapter sla;

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
    public static FragmentDiscover newInstance(User currentUser) {
        FragmentDiscover fragment = new FragmentDiscover();
        fragment.currentUser = currentUser;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = new ArrayList<>();
        list = new ArrayList<>();
        searchResult = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discover, container, false);
        searchView = view.findViewById(R.id.searchview);
        resultView = view.findViewById(R.id.searchResultView);
        sla = new SearchListAdapter(this.getActivity(), searchResult);
        resultView.setAdapter(sla);
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResult.clear();
                sla.notifyDataSetChanged();
                df.child("users").orderByChild("username").equalTo(query).limitToFirst(10)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                User user = dataSnapshot.getValue(User.class);
                                searchResult.add(user);
                                sla.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("uid",searchResult.get(i).getUid());
                Bundle bundle = new Bundle();
                bundle.putParcelable("currentUser",currentUser);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        return view;
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

    public void printRecommend(){
        for(User u : result){
            System.out.println(u.getUsername());
        }
        System.out.println("result size is "+ result.size());
        removeDup();
        for(User u : list){
            System.out.print(u.getUsername());
        }
        System.out.println("result size is "+ list.size());
    }

    private void removeDup(){
        list.add(result.get(0));
        for(User u : result){
            for( User l: list){
                while(u.getUid().equals(l.getUid()));
            }
            list.add(u);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void findMayKnow(User user){
        result.clear();
        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // Find followers are following who
        mDatabaseRef.child("user-follower").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                        }else{
                            System.out.println("Looking at this user ");
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                final User u = ds.getValue(User.class);
                                mDatabaseRef.child("user-following").child(u.getUid()).addChildEventListener(
                                        new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                System.out.println("Found one following for " + u.getUsername());
                                                User following = dataSnapshot.getValue(User.class);
                                                if (result.size() < 100){
                                                    result.add(following);
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
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
    public void onClick(View v) {
        int i = v.getId();
    }
}
