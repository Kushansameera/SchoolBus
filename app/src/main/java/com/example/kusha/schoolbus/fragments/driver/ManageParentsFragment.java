package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.ManageParentsAdapter;
import com.example.kusha.schoolbus.models.ManageParents;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageParentsFragment extends Fragment {

    RecyclerView rcvParents;
    ManageParentsAdapter adapter;
    List<ManageParents> parents = new ArrayList<>();

    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String userId;


    public ManageParentsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage_parents, container, false);
        rcvParents = (RecyclerView)rootView.findViewById(R.id.rcvManageParents);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvParents.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ManageParentsAdapter(getActivity(),parents);
        showParents();
        rcvParents.setAdapter(adapter);

        return rootView;
    }

    private void showParents() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("parent").orderByChild("regParents");

        parents.clear();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : data.getChildren()) {
                        ManageParents parent = dataSnapshot1.getValue(ManageParents.class);
                        parents.add(parent);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
