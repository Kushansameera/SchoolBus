package com.example.kusha.schoolbus.fragments.parent;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.adapter.ContactsOfParentsAdapter;
import com.example.kusha.schoolbus.models.ManageDrivers;
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
public class ParentReceivedContactsFragment extends Fragment {
    View parentReceivedMessageFragment;
    List<ManageDrivers> drivers = new ArrayList<>();
    ContactsOfParentsAdapter adapter;
    RecyclerView rcvContacts;
    Fragment fragment;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ParentReceivedContactsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentReceivedMessageFragment = inflater.inflate(R.layout.fragment_parent_received_contacts, container, false);
        rcvContacts = (RecyclerView)parentReceivedMessageFragment.findViewById(R.id.rcvParentContacts);
        rcvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsOfParentsAdapter(getActivity(), drivers);
        showDrivers();
        rcvContacts.setAdapter(adapter);

        adapter.setOnItemClickListener(new ContactsOfParentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ParentReceivedMessageFragment.msgDriverId = drivers.get(position).getDriverId();
                ParentReceivedMessageFragment.msgDriverName = drivers.get(position).getDriverName();
                try {
                    fragment = new ParentReceivedMessageFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
                } catch (Exception e) {
                    Log.d("Received Messages", e.getMessage());
                }
            }
        });
        return parentReceivedMessageFragment;
    }

    private void showDrivers() {
        Query queryRef;
        queryRef = ref.child("Parents").child(ParentActivity.userId).child("driver").orderByChild("regDrivers");

        drivers.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageDrivers m = d.getValue(ManageDrivers.class);
                        drivers.add(m);
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
