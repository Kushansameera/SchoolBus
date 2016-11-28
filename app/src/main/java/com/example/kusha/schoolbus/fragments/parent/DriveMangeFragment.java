package com.example.kusha.schoolbus.fragments.parent;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.ManageDriversAdapter;
import com.example.kusha.schoolbus.fragments.driver.ViewStudentFragment;
import com.example.kusha.schoolbus.models.ManageDrivers;
import com.example.kusha.schoolbus.models.ManageParents;
import com.example.kusha.schoolbus.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriveMangeFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    private String driverId;
    private boolean flag = false;
    RecyclerView rcvManageDrivers;
    ManageDriversAdapter adapter;
    List<User> drivers = new ArrayList<>();

    public DriveMangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drive_mange, container, false);
        rcvManageDrivers = (RecyclerView) rootView.findViewById(R.id.rcvManageDrivers);
        mProgressDialog = new ProgressDialog(getActivity());
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvManageDrivers.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ManageDriversAdapter(getActivity(), drivers);
        showDrivers();
        rcvManageDrivers.setAdapter(adapter);

        adapter.setOnItemClickListener(new ManageDriversAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String driverEmail = drivers.get(position).getEmail();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_item:
//                                ViewStudentFragment.currentStudentId = stuId;
//                                ViewStudentFragment.driverId = userId;
//                                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                ft.replace(R.id.current_student_frame_container, new ViewStudentFragment());
//                                ft.commit();
                                return true;
                            case R.id.add_item:
                                addDriver(driverEmail);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.add_view, popup.getMenu());
                popup.show();
            }
        });


//

        return rootView;
    }

//    private void showDrivers() {
//        Query queryRef;
//        queryRef = ref.child("Parents").child(userId).child("driver").orderByChild("regDrivers");
//
//        drivers.clear();
//
//        queryRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    for (DataSnapshot d : data.getChildren()) {
//                        ManageDrivers m = d.getValue(ManageDrivers.class);
//                        drivers.add(m);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//    }

    private void showDrivers() {
        Query queryRef;
        queryRef = ref.child("Users").orderByChild("type").equalTo("Driver");

        drivers.clear();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User m = data.getValue(User.class);
                    drivers.add(m);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void regDriver(final String driverID) {
        final ManageDrivers manageDrivers = new ManageDrivers();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                manageDrivers.setDriverEmail(dataSnapshot.child("Users").child(driverID).child("email").getValue().toString().trim());
                manageDrivers.setDriverName(dataSnapshot.child("Users").child(driverID).child("name").getValue().toString().trim());
                ref.child("Parents").child(userId).child("driver").child("regDrivers").child(driverID).setValue(manageDrivers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void checkDriverExists(final String newDriverId) {
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("driver").child("regDrivers");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(newDriverId)) {
                    flag = false;
                } else {
                    flag = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void regParents(final String driverID) {
        final ManageParents manageParents = new ManageParents();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                manageParents.setParentEmail(dataSnapshot.child("Users").child(userId).child("email").getValue().toString().trim());
                manageParents.setParentName(dataSnapshot.child("Users").child(userId).child("name").getValue().toString().trim());
                ref.child("Drivers").child(driverID).child("parent").child("regParents").child(userId).setValue(manageParents);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addDriver(String driverEmail) {
        mProgressDialog.setMessage("Wait");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        Query queryRef;
        queryRef = ref.child("Drivers").orderByChild("email").equalTo(driverEmail);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    driverId = d.getKey();
                    checkDriverExists(driverId);
                    final Handler key = new Handler();
                    key.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (flag) {
                                regDriver(driverId);
                                regParents(driverId);
                                Toast.makeText(getActivity(), "Successfully Added Driver", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            } else {
                                flag = false;
                                Toast.makeText(getActivity(), "Driver Already Exists", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    }, 2000);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

//    private boolean confirmAlert(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//        alertDialogBuilder.setMessage("Are you sure,You wanted to Sign out");
//
//        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//
//            }
//        });
//
//        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }


}
