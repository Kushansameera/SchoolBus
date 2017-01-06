package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteFeesFragment extends Fragment {

    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private ProgressDialog progressDialog;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    private String newFee = "", newSeats = "";

    EditText txtNewFee, txtNumberOfSeats;
    ImageButton btnAddRouteFee, button_seats;
    TextView txtCurrentFee, txtFullCapacity, txtReservedSeatsMorning, txtFreeSeatsMorning,txtReservedSeatsAfternoon, txtFreeSeatsAfternoon;
    int fullCapasity=0, reservedSeatsMorning =0,reservedSeatsAfternoon=0;
    public RouteFeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View routeLocationFeesFragment = inflater.inflate(R.layout.fragment_route_fees, container, false);

        txtNewFee = (EditText) routeLocationFeesFragment.findViewById(R.id.txtNewFee);
        txtNumberOfSeats = (EditText) routeLocationFeesFragment.findViewById(R.id.txtNumberOfSeats);
        txtCurrentFee = (TextView) routeLocationFeesFragment.findViewById(R.id.txtCurrentFee);
        txtFullCapacity = (TextView) routeLocationFeesFragment.findViewById(R.id.txtFullCapacity);
        txtReservedSeatsMorning = (TextView) routeLocationFeesFragment.findViewById(R.id.txtReservedSeatsMorning);
        txtFreeSeatsMorning = (TextView) routeLocationFeesFragment.findViewById(R.id.txtFreeSeatsMorning);
        txtReservedSeatsAfternoon = (TextView) routeLocationFeesFragment.findViewById(R.id.txtReservedSeatsAfternoon);
        txtFreeSeatsAfternoon = (TextView) routeLocationFeesFragment.findViewById(R.id.txtFreeSeatsAfternoon);
        btnAddRouteFee = (ImageButton) routeLocationFeesFragment.findViewById(R.id.button_fee);
        button_seats = (ImageButton) routeLocationFeesFragment.findViewById(R.id.button_seats);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);
        setCurrentPrice();
        setSeatDetails();

        btnAddRouteFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNewFee.getText().toString().length() == 0 || txtNewFee.getText().toString().equals("0")) {
                    Toast.makeText(getActivity(), "Price Not Valid", Toast.LENGTH_SHORT).show();
                    txtCurrentFee.setText("");
                } else {
                    updateFee();
                    setCurrentPrice();
                }

            }
        });

        button_seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNumberOfSeats.getText().toString().length() == 0 || txtNumberOfSeats.getText().toString().equals("0")) {
                    Toast.makeText(getActivity(), "Value Not Valid", Toast.LENGTH_SHORT).show();
                    txtNumberOfSeats.setText("");
                } else {
                    updateSeats();
                    setSeatDetails();
                }
            }
        });

        return routeLocationFeesFragment;
    }


    private void updateFee() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are You Want To Add New Price Per Km?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newFee = txtNewFee.getText().toString();
                ref.child("Drivers").child(DriverActivity.userId).child("pricePerKm").setValue(newFee);
                txtNewFee.setText("");
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    private void updateSeats() {
        newSeats = txtNumberOfSeats.getText().toString();
        ref.child("Drivers").child(DriverActivity.userId).child("allSeats").setValue(newSeats);
        txtNumberOfSeats.setText("");

    }

    public void setCurrentPrice() {
        ref.child("Drivers").child(DriverActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("pricePerKm")) {
                    getCurrentPrice();
                } else {
                    txtCurrentFee.setText("");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getCurrentPrice() {

        ref.child("Drivers").child(DriverActivity.userId).child("pricePerKm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtCurrentFee.setText("Rs. " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void setSeatDetails() {
        progressDialog.show();
        ref.child("Drivers").child(DriverActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("allSeats")) {
                    getSeatDetails();
                } else {
                    txtReservedSeatsMorning.setText("0 Seats");
                    txtFullCapacity.setText("0 Seats");
                    txtFreeSeatsMorning.setText("0 Seats");
                    txtReservedSeatsAfternoon.setText("0 Seats");
                    txtFreeSeatsAfternoon.setText("0 Seats");
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getSeatDetails() {
        ref.child("Drivers").child(DriverActivity.userId).child("allSeats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullCapasity = Integer.valueOf(dataSnapshot.getValue().toString());
                txtFullCapacity.setText(dataSnapshot.getValue().toString() + " Seats");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ref.child("Drivers").child(DriverActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("reservedSeatsMorning") && dataSnapshot.hasChild("reservedSeatsAfternoon")) {
                    reservedSeatsMorning = Integer.valueOf(dataSnapshot.child("reservedSeatsMorning").getValue().toString());
                    txtReservedSeatsMorning.setText(dataSnapshot.child("reservedSeatsMorning").getValue().toString() + " Seats");
                    txtFreeSeatsMorning.setText(String.valueOf(fullCapasity- reservedSeatsMorning)+" Seats");
                    reservedSeatsAfternoon = Integer.valueOf(dataSnapshot.child("reservedSeatsAfternoon").getValue().toString());
                    txtReservedSeatsAfternoon.setText(dataSnapshot.child("reservedSeatsAfternoon").getValue().toString() + " Seats");
                    txtFreeSeatsAfternoon.setText(String.valueOf(fullCapasity- reservedSeatsAfternoon)+" Seats");
                    progressDialog.dismiss();
                }else if(dataSnapshot.hasChild("reservedSeatsMorning")){
                    reservedSeatsMorning = Integer.valueOf(dataSnapshot.child("reservedSeatsMorning").getValue().toString());
                    txtReservedSeatsMorning.setText(dataSnapshot.child("reservedSeatsMorning").getValue().toString() + " Seats");
                    txtFreeSeatsMorning.setText(String.valueOf(fullCapasity- reservedSeatsMorning)+" Seats");
                    txtReservedSeatsAfternoon.setText("0 Seats");
                    txtFreeSeatsAfternoon.setText(txtFullCapacity.getText());
                    progressDialog.dismiss();
                }else if(dataSnapshot.hasChild("reservedSeatsAfternoon")){
                    reservedSeatsAfternoon = Integer.valueOf(dataSnapshot.child("reservedSeatsAfternoon").getValue().toString());
                    txtReservedSeatsAfternoon.setText(dataSnapshot.child("reservedSeatsAfternoon").getValue().toString() + " Seats");
                    txtFreeSeatsAfternoon.setText(String.valueOf(fullCapasity- reservedSeatsAfternoon)+" Seats");
                    txtReservedSeatsMorning.setText("0 Seats");
                    txtFreeSeatsMorning.setText(txtFullCapacity.getText());
                    progressDialog.dismiss();
                }
                else {
                    txtReservedSeatsMorning.setText("0 Seats");
                    txtReservedSeatsAfternoon.setText("0 Seats");
                    txtFreeSeatsAfternoon.setText(txtFullCapacity.getText());
                    txtFreeSeatsMorning.setText(txtFullCapacity.getText());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
