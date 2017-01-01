package com.example.kusha.schoolbus.fragments.driver;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.adapter.ContactsOfDriverAdapter;
import com.example.kusha.schoolbus.models.ManageParents;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverReceivedContactsFragment extends Fragment {
    View driverReceivedContactsFragment;
    List<ManageParents> parents = new ArrayList<>();
    ContactsOfDriverAdapter adapter;
    RecyclerView rcvContacts;
    Fragment fragment;
    ImageButton btnBack;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");


    public DriverReceivedContactsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        driverReceivedContactsFragment = inflater.inflate(R.layout.fragment_driver_received_contacts, container, false);
        rcvContacts = (RecyclerView)driverReceivedContactsFragment.findViewById(R.id.rcvDriverContacts);
        btnBack = (ImageButton)driverReceivedContactsFragment.findViewById(R.id.imageButtonBack);
        rcvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsOfDriverAdapter(getActivity(), parents);
        showParents();
        rcvContacts.setAdapter(adapter);

        adapter.setOnItemClickListener(new ContactsOfDriverAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                DriverReceivedMessageFragment.msgParentId = parents.get(position).getParentId();
                DriverReceivedMessageFragment.msgParentName = parents.get(position).getParentName();
                try {
                    fragment = new DriverReceivedMessageFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_driver, fragment).commit();
                } catch (Exception e) {
                    Log.d("Sent Messages", e.getMessage());
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new DriverMessageFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_driver, fragment).commit();
                } catch (Exception e) {
                    Log.d("Sent Contacts", e.getMessage());
                }
            }
        });

        return driverReceivedContactsFragment;
    }

    private void showParents() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("parent").orderByChild("regParents");

        parents.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageParents m = d.getValue(ManageParents.class);
                        parents.add(m);
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
