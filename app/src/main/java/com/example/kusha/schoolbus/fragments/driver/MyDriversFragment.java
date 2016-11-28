package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.activities.parent.SelectDriverActivity;
import com.example.kusha.schoolbus.models.ManageDrivers;
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
public class MyDriversFragment extends Fragment {

    Button btnSelect;

    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    public FirebaseAuth mFirebaseAuth;
    private RadioGroup driverRadioGroup;
    List<ManageDrivers> manageDrivers = new ArrayList<>();

    public MyDriversFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myDriverFragment = inflater.inflate(R.layout.fragment_my_drivers, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        manageDrivers.clear();
        showDrivers();

        btnSelect = (Button)myDriverFragment.findViewById(R.id.btnSelectDriver);
        driverRadioGroup = (RadioGroup)myDriverFragment.findViewById(R.id.radioGroupMyDrivers);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioId = driverRadioGroup.getCheckedRadioButtonId()-manageDrivers.size();
                //RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioId);
                ParentActivity.selectedDriverEmail = manageDrivers.get(selectedRadioId-1).getDriverEmail().toString().trim();
            }
        });

        return myDriverFragment;
    }

    public void showDrivers(){
        manageDrivers.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("driver").orderByChild("regDrivers");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageDrivers m = d.getValue(ManageDrivers.class);
                        RadioButton driverRadioButton = new RadioButton(getActivity());
                        driverRadioButton.setText(m.getDriverName());
                        driverRadioButton.setTextSize(15);
                        driverRadioGroup.addView(driverRadioButton);
                        manageDrivers.add(m);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
