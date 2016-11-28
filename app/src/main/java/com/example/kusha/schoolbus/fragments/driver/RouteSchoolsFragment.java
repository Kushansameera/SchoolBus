package com.example.kusha.schoolbus.fragments.driver;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import com.example.kusha.schoolbus.adapter.SchoolsAdapter;
import com.example.kusha.schoolbus.models.Schools;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteSchoolsFragment extends Fragment {

    RecyclerView rcvSchools;
    SchoolsAdapter adapter;
    List<Schools> schools = new ArrayList<>();
    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    private String latestSchoolID = "";
    private boolean flag = false;
    private String newSchoolName = "";

    EditText txtRouteSchoolName;
    ImageButton btnAddRouteSchool;


    public RouteSchoolsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View routeSchoolFragment = inflater.inflate(R.layout.fragment_route_schools, container, false);
        rcvSchools = (RecyclerView) routeSchoolFragment.findViewById(R.id.rcvSchools);
        txtRouteSchoolName = (EditText) routeSchoolFragment.findViewById(R.id.txtRouteSchoolName);
        btnAddRouteSchool = (ImageButton) routeSchoolFragment.findViewById(R.id.btnAddRouteSchool);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvSchools.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SchoolsAdapter(getActivity(), schools);
        showSchools();
        rcvSchools.setAdapter(adapter);

        btnAddRouteSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtRouteSchoolName.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    checkSchoolExists();
                    flag = false;
                }
            }
        });

        adapter.setOnItemClickListener(new SchoolsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String schoolId = schools.get(position).getSchoolId();
                final String schoolName = schools.get(position).getSchoolName();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editRoute:
                                updateData(schoolId, schoolName);
                                return true;
                            case R.id.deleteRoute:
                                Log.d(">>>>>>>","delete");
                                deleteData(schoolId, schoolName);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete, popup.getMenu());
                popup.show();
            }
        });

        return routeSchoolFragment;
    }

    private void deleteData(final String schoolId, final String schoolName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure,You wanted to Delete " + schoolName);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ref.child("Drivers").child(userId).child("schools").child("routeSchools").child(schoolId).removeValue();
                schools.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), schoolName + " Deleted From Schools", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    private void updateData(final String schoolId, final String schoolName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Enter New School Name");
        final EditText newName = new EditText(getActivity());
        newName.setInputType(InputType.TYPE_CLASS_TEXT);
        newName.setText(schoolName);
        alertDialogBuilder.setView(newName);

        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newSchoolName = newName.getText().toString();
                if (newName.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child("Drivers").child(userId).child("schools").child("routeSchools").child(schoolId).child("schoolName").setValue(newSchoolName);
                    schools.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), schoolName + " Changed as " + newSchoolName, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    private void addSchool() {
        final Schools routeSchools = new Schools();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routeSchools.setSchoolId(latestSchoolID);
                routeSchools.setSchoolName(txtRouteSchoolName.getText().toString().trim());
                ref.child("Drivers").child(userId).child("schools").child("routeSchools").child(latestSchoolID).setValue(routeSchools);
                Toast.makeText(getActivity(), "Successfully Added Location", Toast.LENGTH_SHORT).show();
                txtRouteSchoolName.setText("");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getLatestSchoolID() {
        ref.child("Drivers").child(userId).child("schools").child("schoolCounter").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class) + 1;
                latestSchoolID = currentValue.toString();
                mutableData.setValue(currentValue);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                addSchool();
                schools.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkSchoolId() {
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("schools")) {
                    getLatestSchoolID();
                } else {
                    latestSchoolID = "1";
                    ref.child("Drivers").child(userId).child("schools").child("schoolCounter").setValue(1);
                    addSchool();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showSchools() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("schools").orderByChild("routeSchools");

        schools.clear();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Schools s = d.getValue(Schools.class);
                        schools.add(s);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void checkSchoolExists() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("schools").child("routeSchools").orderByValue();
        final String oldSchoolName = txtRouteSchoolName.getText().toString();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Schools routeSco = data.getValue(Schools.class);
                        if (routeSco.getSchoolName().equalsIgnoreCase(oldSchoolName)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Toast.makeText(getActivity(), "School Name is Already Exists", Toast.LENGTH_SHORT).show();
                        txtRouteSchoolName.setText("");
                    } else {
                        checkSchoolId();
                    }

                } else {
                    checkSchoolId();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
