package com.example.kusha.schoolbus.fragments.parent;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.ManageDrivers;
import com.example.kusha.schoolbus.models.User;
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

    Button btnSelect, btnDelete;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    public FirebaseAuth mFirebaseAuth;
    private RadioGroup driverRadioGroup;
    List<ManageDrivers> myDrivers = new ArrayList<>();
    //RecyclerView rcvMyeDrivers;
    User user;
    String name;
    int i = 0;

    public MyDriversFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("======>", "onCreate");
        final View myDriverFragment = inflater.inflate(R.layout.fragment_my_drivers, container, false);
        btnSelect = (Button) myDriverFragment.findViewById(R.id.btnSelectDriver);
        btnDelete = (Button) myDriverFragment.findViewById(R.id.btnDeleteDriver);
        //rcvMyeDrivers = (RecyclerView) myDriverFragment.findViewById(R.id.rcvMyDrivers);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        myDrivers.clear();
        showDrivers();
        driverRadioGroup = (RadioGroup) myDriverFragment.findViewById(R.id.radioGroupMyDrivers);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioId = driverRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = (RadioButton) myDriverFragment.findViewById(selectedRadioId);
                String name = selectedRadioButton.getText().toString();
                for (int i = 0; i < myDrivers.size(); i++) {
                    if (name.equals(myDrivers.get(i).getDriverName())) {
                        ParentActivity.selectedDriverEmail = myDrivers.get(i).getDriverEmail();
                        ParentActivity.selectedDriverID = myDrivers.get(i).getDriverId();
                        ParentActivity.selectedDriverName = myDrivers.get(i).getDriverName();
                        ParentActivity.selectedChildId = "";
                        ParentActivity.selectedChildName = "";
                        Toast.makeText(getActivity(), name + " Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            String mSelectedDriverID;

            @Override
            public void onClick(View v) {
                int selectedRadioId = driverRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = (RadioButton) myDriverFragment.findViewById(selectedRadioId);
                name = selectedRadioButton.getText().toString();
                for (i = 0; i < myDrivers.size(); i++) {
                    if (name.equals(myDrivers.get(i).getDriverName())) {
                        mSelectedDriverID = myDrivers.get(i).getDriverId();
                        break;
                    }
                }
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are You Sure, Do You Want to Delete " + name);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child("Parents").child(userId).child("driver").child("regDrivers").child(mSelectedDriverID).removeValue();
                        ref.child("Drivers").child(mSelectedDriverID).child("parent").child("regParents").child(userId).removeValue();
                        if (name.equals(ParentActivity.selectedDriverName)) {
                            ParentActivity.selectedDriverName = "";
                            ParentActivity.selectedDriverID = "";
                            ParentActivity.selectedDriverEmail = "";
                            ParentActivity.selectedChildName = "";
                            ParentActivity.selectedChildId = "";
                        }
                        myDrivers.remove(i);
                        Toast.makeText(getActivity(), "Driver Deleted", Toast.LENGTH_SHORT).show();
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
        });
        return myDriverFragment;
    }


    public void showDrivers() {
        myDrivers.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("driver").orderByChild("regDrivers");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(">>>>", "data Change");
                myDrivers.clear();
                Log.d(">>>>", "clear drivers");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageDrivers m = d.getValue(ManageDrivers.class);
                        myDrivers.add(m);
                    }
                }
                driverRadioGroup.removeAllViews();
                addRadioButtons();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addRadioButtons() {
        for (int i = 0; i < myDrivers.size(); i++) {
            RadioButton driverRadioButton = new RadioButton(getActivity());
            driverRadioButton.setText(myDrivers.get(i).getDriverName());
            driverRadioButton.setTextSize(15);
            driverRadioGroup.addView(driverRadioButton);
        }
    }

//    private void showDriverData(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//        alertDialogBuilder.setMessage("Driver Info");
//        alertDialogBuilder.setIcon(R.drawable.ic_info);
//
//        LinearLayout layout = new LinearLayout(getActivity());
//        layout.setOrientation(LinearLayout.VERTICAL);
//        final TextView name = new TextView(getActivity());
//        final TextView email = new TextView(getActivity());
//        final TextView phone = new TextView(getActivity());
//
//        name.setText("\t\t\t\t\t\tName  : "+user.getName());
//        email.setText("\t\t\t\t\t\tEmail  : "+user.getEmail());
//        phone.setText("\t\t\t\t\t\tPhone : "+user.getPhoneNumber());
//
//        layout.addView(name);
//        layout.addView(email);
//        layout.addView(phone);
//
//        alertDialogBuilder.setView(layout);
//
//        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        alertDialogBuilder.show();
//    }
//
//    private void getDriverId(String driverEmail){
//        Query queryRef;
//        queryRef = ref.child("Drivers").orderByChild("email").equalTo(driverEmail);
//        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            String driverID;
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data:dataSnapshot.getChildren()) {
//                    driverID = data.getKey();
//                    getDrverDetails(driverID);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//    }
//
//    private void getDrverDetails(String driverID){
//        ref.child("Users").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//                showDriverData();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//    }

}
