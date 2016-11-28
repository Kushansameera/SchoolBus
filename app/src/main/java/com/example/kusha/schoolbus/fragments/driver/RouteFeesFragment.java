package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.CustomAdapter;
import com.example.kusha.schoolbus.models.RouteFees;
import com.example.kusha.schoolbus.adapter.RouteFeeAdapter;
import com.example.kusha.schoolbus.models.RouteLocations;
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
public class RouteFeesFragment extends Fragment {
    RecyclerView rcvRouteFee;
    RouteFeeAdapter adapter;
    List<RouteFees> fees = new ArrayList<>();
    private View routeLocationFeesFragment;
    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private ProgressDialog progressDialog;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    private String latestFeeID = "";
    private boolean flag = false;
    private String newFee= "";

    List<String> pickupLoc = new ArrayList<>();
    List<String> locSchools = new ArrayList<>();


    EditText txtFee;
    ImageButton btnAddRouteFee;
    Spinner spinnerFrom, spinnerTo;

    public RouteFeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        routeLocationFeesFragment = inflater.inflate(R.layout.fragment_route_fees, container, false);
        rcvRouteFee = (RecyclerView) routeLocationFeesFragment.findViewById(R.id.rcvRouteFee);
        txtFee = (EditText) routeLocationFeesFragment.findViewById(R.id.locFee);
        btnAddRouteFee = (ImageButton) routeLocationFeesFragment.findViewById(R.id.button_fee);
        spinnerFrom = (Spinner) routeLocationFeesFragment.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) routeLocationFeesFragment.findViewById(R.id.spinnerTo);
        rcvRouteFee.setLayoutManager(new LinearLayoutManager(getActivity()));

        pickupLoc.clear();
        locSchools.clear();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        setSpinnerValues();

        adapter = new RouteFeeAdapter(getActivity(), fees);
        showFees();
        rcvRouteFee.setAdapter(adapter);

        btnAddRouteFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){
                    checkFeesExists();
                    flag = false;
                }

            }
        });

        adapter.setOnItemClickListener(new RouteFeeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String feeId = fees.get(position).getRouteFeeID();
                final String fee = fees.get(position).getFee();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editRoute:
                                updateData(feeId, fee);
                                return true;
                            case R.id.deleteRoute:
                                deleteData(feeId, fee);
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
        return routeLocationFeesFragment;
    }

    private void deleteData(final String feeId, final String fee) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure,You wanted to Delete ");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ref.child("Drivers").child(userId).child("fees").child("routeFees").child(feeId).removeValue();
                fees.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
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

    private void updateData(final String feeId, final String fee) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Enter New Fee");
        final EditText newFees = new EditText(getActivity());
        newFees.setInputType(InputType.TYPE_CLASS_NUMBER);
        newFees.setText(fee);
        alertDialogBuilder.setView(newFees);

        alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newFee = newFees.getText().toString();
                if (newFees.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Fee", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child("Drivers").child(userId).child("fees").child("routeFees").child(feeId).child("fee").setValue(newFee);
                    fees.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"Fee Successfully Changed", Toast.LENGTH_SHORT).show();
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

    private void setSpinnerValues() {

        Query queryRef1, queryRef2;
        queryRef1 = ref.child("Drivers").child(userId).child("route").orderByChild("routeLocations");
        queryRef2 = ref.child("Drivers").child(userId).child("schools").orderByChild("routeSchools");

        pickupLoc.add("From");
        locSchools.add("To");

        queryRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        RouteLocations r = d.getValue(RouteLocations.class);
                        pickupLoc.add(r.getLocName());
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        queryRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Schools s = d.getValue(Schools.class);
                        locSchools.add(s.getSchoolName());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        CustomAdapter locFrom = new CustomAdapter(getActivity(), android.R.layout.simple_spinner_item, pickupLoc, 0);
        locFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(locFrom);
        int from = locFrom.getPosition("From");
        spinnerFrom.setSelection(from);

        CustomAdapter locTo = new CustomAdapter(getActivity(), android.R.layout.simple_spinner_item, locSchools, 0);
        locTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(locTo);
        int to = locFrom.getPosition("To");
        spinnerFrom.setSelection(to);
    }

    private boolean checkFields(){
        if(spinnerFrom.getSelectedItemPosition()==0){
            Toast.makeText(getActivity(), "Please Select [From] Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(spinnerTo.getSelectedItemPosition()==0){
            Toast.makeText(getActivity(), "Please Select [To] Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtFee.getText().length()==0){
            Toast.makeText(getActivity(), "Please Enter Fee", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Integer.parseInt(txtFee.getText().toString())<=0){
            Toast.makeText(getActivity(), "Fee Not Valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    private void addFee() {
        final RouteFees routeFee = new RouteFees();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routeFee.setRouteFeeID(latestFeeID);
                routeFee.setFee(txtFee.getText().toString().trim());
                routeFee.setFrom(spinnerFrom.getSelectedItem().toString());
                routeFee.setTo(spinnerTo.getSelectedItem().toString());
                ref.child("Drivers").child(userId).child("fees").child("routeFees").child(latestFeeID).setValue(routeFee);
                Toast.makeText(getActivity(), "Successfully Added Fee", Toast.LENGTH_SHORT).show();
                txtFee.setText("");
                spinnerFrom.setSelection(0);
                spinnerTo.setSelection(0);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getLatestFeeID() {
        ref.child("Drivers").child(userId).child("fees").child("feesCounter").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class) + 1;
                latestFeeID = currentValue.toString();
                mutableData.setValue(currentValue);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                addFee();
                fees.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkFeelId() {
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("fees")) {
                    getLatestFeeID();
                } else {
                    latestFeeID = "1";
                    ref.child("Drivers").child(userId).child("fees").child("feesCounter").setValue(1);
                    addFee();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showFees() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("fees").orderByChild("routeFees");

        fees.clear();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        RouteFees f = d.getValue(RouteFees.class);
                        fees.add(f);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void checkFeesExists() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("fees").child("routeFees").orderByValue();
        final String oldFromName = spinnerFrom.getSelectedItem().toString();
        final String oldToName = spinnerTo.getSelectedItem().toString();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        RouteFees routeFee = data.getValue(RouteFees.class);
                        if (routeFee.getFrom().equalsIgnoreCase(oldFromName) && routeFee.getTo().equalsIgnoreCase(oldToName)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Toast.makeText(getActivity(), "This Fee is Already Exists", Toast.LENGTH_SHORT).show();
                        spinnerFrom.setSelection(0);
                        spinnerTo.setSelection(0);
                        txtFee.setText("");
                    } else {
                        checkFeelId();
                    }

                } else {
                    checkFeelId();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
