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
import com.example.kusha.schoolbus.application.GeoDistance;
import com.example.kusha.schoolbus.models.Children;
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
    public static String studistance = "";
    private static String latestStudentID;

    private TextView txtTempStuType, txtTempStuName, txtTempStuSchool, txtTempStuGrade, txtTempStuClass, txtTempStuPickup, txtTempStuDrop, txtTempStuPickupTime, txtTempStuMonthlyFee;
    private Button btnAccept, btnReject, btnMonthlyFee;

    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    private Student student;
    private Schools school;
    private Double pricePerKm;
    boolean flag = false;
    private ProgressDialog mProgressDialog;
    View stuRequestFragment;
    String stuType;
    String morningUrl = "", eveningUrl = "";
    Double morningDis = 0.0, eveningDis = 0.0;
    int newMorningDis,newEveningDis;

    public TempStudentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stuRequestFragment = inflater.inflate(R.layout.fragment_temp_student, container, false);

        txtTempStuName = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuName);
        txtTempStuSchool = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuSchool);
        txtTempStuGrade = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuGrade);
        txtTempStuClass = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuClass);
        txtTempStuPickup = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickup);
        txtTempStuDrop = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuDrop);
        txtTempStuPickupTime = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuPickupTime);
        txtTempStuMonthlyFee = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuMonthlyFee);
        txtTempStuType = (TextView) stuRequestFragment.findViewById(R.id.txtTempStuType);
        btnAccept = (Button) stuRequestFragment.findViewById(R.id.btnAccept);
        btnReject = (Button) stuRequestFragment.findViewById(R.id.btnReject);
        btnMonthlyFee = (Button) stuRequestFragment.findViewById(R.id.btnMonthlyFee);
        mProgressDialog = new ProgressDialog(getActivity());
        getTempStudentData();
        getSchoolDetails();
        getPricePerKm();

        btnMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStuId();
                calculateMonthlyFee();

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
                            saveStudentWithParent();
                            flag = false;
                        } else {
                            addStudent();
                            saveStudentWithParent();
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


    private void getTempStudentData() {
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                txtTempStuType.setText(student.getStuType());
                stuType = student.getStuType();
                txtTempStuName.setText(student.getStuName());
                txtTempStuSchool.setText(student.getStuSchool());
                txtTempStuGrade.setText(student.getStuGrade());
                txtTempStuClass.setText(student.getStuClass());
                if (!stuType.equals("Morning Only")) {
                    txtTempStuDrop.setText(student.getStuDropLocation());
                    txtTempStuPickup.setText("-");
                }
                if (!stuType.equals("Evening Only")) {
                    txtTempStuPickup.setText(student.getStuPickLocation());
                    txtTempStuDrop.setText("-");
                }
                txtTempStuPickupTime.setText(student.getStuPickTime());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void calculateMonthlyFee() {
        mProgressDialog.setMessage("Calculating");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        if (!stuType.equals("Evening Only")) {
            morningUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + student.getStuPickLatitude() + "," + student.getStuPickLongitude() + "&destinations=" + school.getSchoolLatitude() + "," + school.getSchoolLongitude() + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyBEU7F7R6nHIehl_GcoTYpFJjlLLl8bEAw";
            new GeoDistance(stuRequestFragment.getContext()).execute(morningUrl);
            Log.d("========<>","eveningOnlyFalse");
        }
        if (!stuType.equals("Morning Only")) {
            eveningUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + school.getSchoolLatitude() + "," + school.getSchoolLongitude() + "&destinations=" + student.getStuDropLatitude() + "," + student.getStuDropLongitude() + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyBEU7F7R6nHIehl_GcoTYpFJjlLLl8bEAw";
            new GeoDistance(stuRequestFragment.getContext()).execute(eveningUrl);
            Log.d("========<>","MorningOnlyFalse");
        }
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stuType.equals("Evening Only")) {
                    morningDis = Double.valueOf(studistance) / 1000;
                    newMorningDis = (int) Math.round(morningDis);
                    Log.d("========<>","EveningOnlyFalse  "+String.valueOf(newMorningDis));
                }
                if (!stuType.equals("Morning Only")) {
                    eveningDis = Double.valueOf(studistance) / 1000;
                    newEveningDis = (int) Math.round(eveningDis);
                    Log.d("========<>","MorningOnlyFalse  "+String.valueOf(newEveningDis));
                }

                int monthlyFee = (newMorningDis+newEveningDis) * ((int) Math.round(pricePerKm));
                Log.d("========<>","MorningOnlyFalse  "+String.valueOf(monthlyFee));
                txtTempStuMonthlyFee.setText(String.valueOf(monthlyFee));
                student.setStuMonthlyFee(String.valueOf(monthlyFee));
                mProgressDialog.dismiss();
            }
        }, 2000);


    }

    private void checkStuId() {
        mProgressDialog.setMessage("Calculating");
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
                Integer newValue = currentValue + 1;
                latestStudentID = newValue.toString();
                ref.child("Drivers").child(driverId).child("permanent").child("studentCounter").setValue(latestStudentID);
                //mProgressDialog.dismiss();
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

    private void saveStudentWithParent() {
        Children children = new Children();
        children.setStuId(latestStudentID);
        children.setStuName(student.getStuName());
        ref.child("Parents").child(student.getParentID()).child("children").child(driverId).child(latestStudentID).setValue(children);

    }

    private void deleteData() {
        ref.child("Drivers").child(driverId).child("temp").child("tempStudent").child(tempStudentId).removeValue();
    }

    private void getSchoolDetails() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(driverId).child("schools").orderByChild("routeSchools");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        school = d.getValue(Schools.class);
                        if (school.getSchoolName().equals(student.getStuSchool())) {
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getPricePerKm() {
        ref.child("Drivers").child(driverId).child("pricePerKm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pricePerKm = Double.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}

