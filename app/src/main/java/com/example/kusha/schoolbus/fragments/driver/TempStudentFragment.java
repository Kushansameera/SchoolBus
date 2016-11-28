package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.CustomAdapter;
import com.example.kusha.schoolbus.models.RouteFees;
import com.example.kusha.schoolbus.models.RouteLocations;
import com.example.kusha.schoolbus.models.Schools;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TempStudentFragment extends Fragment {
    public static String tempStudentId = "";
    public static String driverId = "";
    private TextView txtTempStuName, txtTempStuSchool, txtTempStuGrade, txtTempStuClass, txtTempStuPickup, txtTempStuDrop, txtTempStuPickupTime, txtTempStuMonthlyFee;
    private Spinner spinnerTempStuFrom;
    private Button btnAccept, btnReject, btnMonthlyFee;
    List<String> tempStuFrom = new ArrayList<>();
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private Student student;
    private static String latestStudentID;
    boolean flag = false;
    private ProgressDialog mProgressDialog;

    public TempStudentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View stuRequestFragment = inflater.inflate(R.layout.fragment_temp_student, container, false);

        txtTempStuName = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuName);
        txtTempStuSchool = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuSchool);
        txtTempStuGrade = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuGrade);
        txtTempStuClass = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuClass);
        txtTempStuPickup = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickup);
        txtTempStuDrop = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuDrop);
        txtTempStuPickupTime = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickupTime);
        txtTempStuMonthlyFee = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuMonthlyFee);
        spinnerTempStuFrom = (Spinner) stuRequestFragment.findViewById(R.id.spinnerTempStuFrom);
        btnAccept = (Button) stuRequestFragment.findViewById(R.id.btnAccept);
        btnReject = (Button) stuRequestFragment.findViewById(R.id.btnReject);
        btnMonthlyFee = (Button) stuRequestFragment.findViewById(R.id.btnMonthlyFee);
        mProgressDialog = new ProgressDialog(getActivity());
        setSpinnerValues();
        getTempStudentData();

        btnMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerTempStuFrom.getSelectedItemPosition() != 0) {
                    checkStuId();
                    getMonthlyFee();
                } else {
                    Toast.makeText(getActivity(), "Please Select Nearest Pickup Location", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtTempStuMonthlyFee.getText().equals("")) {
                    if (flag) {
                        if (latestStudentID.equals("1")) {
                            ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").setValue(1);
                            addStudent();
                            flag = false;
                        } else {
                            addStudent();
                            flag = false;
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Add Monthly Fee", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure,You wanted to Reject This Student ");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteData();
                        Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.temp_student_frame_container, new StudentRequestsFragment());
                        ft.commit();
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
        });


        return stuRequestFragment;
    }

    private void setSpinnerValues() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(driverId).child("route").orderByChild("routeLocations");
        tempStuFrom.clear();
        tempStuFrom.add("Select Nearest Pickup Location");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        RouteLocations s = d.getValue(RouteLocations.class);
                        tempStuFrom.add(s.getLocName());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        CustomAdapter locFrom = new CustomAdapter(getActivity(), android.R.layout.simple_spinner_item, tempStuFrom, 0);
        locFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTempStuFrom.setAdapter(locFrom);
    }

    private void getTempStudentData() {
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                txtTempStuName.setText(student.getStuName());
                txtTempStuSchool.setText(student.getStuSchool());
                txtTempStuGrade.setText(student.getStuGrade());
                txtTempStuClass.setText(student.getStuClass());
                txtTempStuPickup.setText(student.getStuPickLocation());
                txtTempStuDrop.setText(student.getStuDropLocation());
                txtTempStuPickupTime.setText(student.getStuPickTime());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getMonthlyFee() {
        ref.child("Drivers").child(driverId).child("fees").child("routeFees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String from = spinnerTempStuFrom.getSelectedItem().toString();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    RouteFees routeFees = data.getValue(RouteFees.class);
                    if (routeFees.getTo().equals(student.getStuSchool()) && routeFees.getFrom().equals(from)) {
                        txtTempStuMonthlyFee.setText(routeFees.getFee());
                        student.setStuFrom(routeFees.getFrom());
                        student.setStuMonthlyFee(routeFees.getFee());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void checkStuId() {
        mProgressDialog.setMessage("Wait");
        
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        ref.child("Drivers").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("permanent")) {
                    getLatestStuID();

                } else {
                    latestStudentID = "1";
                    flag = true;
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void getLatestStuID() {
        ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer currentValue = Integer.parseInt(dataSnapshot.getValue().toString());
                Integer newValue = currentValue+1;
                latestStudentID = newValue.toString();
                ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").setValue(latestStudentID);
                mProgressDialog.dismiss();
                flag = true;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addStudent() {
        student.setStuID(latestStudentID);
        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(latestStudentID).setValue(student, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    deleteData();
                    Toast.makeText(getActivity(), "Student Accepted", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.temp_student_frame_container, new StudentRequestsFragment());
                    ft.commit();

                } else {
                    Toast.makeText(getActivity(), "Cannot Accept Student", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    private void deleteData() {
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).removeValue();
    }


}

