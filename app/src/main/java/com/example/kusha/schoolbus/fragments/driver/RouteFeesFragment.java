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
import android.widget.TextView;
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

    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private ProgressDialog progressDialog;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;

    private String newFee = "";

    EditText txtNewFee;
    ImageButton btnAddRouteFee;
    TextView txtCurrentFee;

    public RouteFeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View routeLocationFeesFragment = inflater.inflate(R.layout.fragment_route_fees, container, false);

        txtNewFee = (EditText) routeLocationFeesFragment.findViewById(R.id.txtNewFee);
        txtCurrentFee = (TextView) routeLocationFeesFragment.findViewById(R.id.txtCurrentFee);
        btnAddRouteFee = (ImageButton) routeLocationFeesFragment.findViewById(R.id.button_fee);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        setCurrentPrice();

        btnAddRouteFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNewFee.getText().toString().length() == 0 || txtNewFee.getText().toString().equals("0")) {
                    Toast.makeText(getActivity(), "Price Not Valid", Toast.LENGTH_SHORT).show();
                    txtCurrentFee.setText("");
                } else {
                    updateData();
                    setCurrentPrice();
                }

            }
        });

        return routeLocationFeesFragment;
    }


    private void updateData() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are You Want To Add New Price Per Km?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newFee = txtNewFee.getText().toString();
                ref.child("Drivers").child(userId).child("pricePerKm").setValue(newFee);
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

    public void setCurrentPrice() {
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        ref.child("Drivers").child(userId).child("pricePerKm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtCurrentFee.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
