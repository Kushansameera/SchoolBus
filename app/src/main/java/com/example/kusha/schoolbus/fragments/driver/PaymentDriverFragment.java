package com.example.kusha.schoolbus.fragments.driver;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.application.ApplicationClass;
import com.example.kusha.schoolbus.models.PaymentList;
import com.example.kusha.schoolbus.models.Student;
import com.example.kusha.schoolbus.models.StudentPayment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDriverFragment extends Fragment {
    Button btnAddPayment, btnViewPayment, btnSummery, btnUpdadte;
    Fragment fragment;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    List<Student> students = new ArrayList<>();
    List<StudentPayment> studentPayments = new ArrayList<>();
    String[] monthArray;
    String mYear;
    private ProgressDialog mProgressDialog;


    public PaymentDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_driver, container, false);
        btnAddPayment = (Button) rootView.findViewById(R.id.btnAddPayment);
        btnViewPayment = (Button) rootView.findViewById(R.id.btnViewPayment);
        btnSummery = (Button) rootView.findViewById(R.id.btnSummery);
        btnUpdadte = (Button) rootView.findViewById(R.id.btnUpdte);
        mProgressDialog = new ProgressDialog(getActivity());
        ApplicationClass.bus.register(this);
        monthArray = getResources().getStringArray(R.array.months);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        mYear = String.valueOf(year);

        btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new AddNewPaymentFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_driver, fragment).commit();
                } catch (Exception e) {
                    Log.d("Payment", e.getMessage());
                }
            }
        });

        btnViewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new ViewPaymentFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_driver, fragment).commit();
                } catch (Exception e) {
                    Log.d("Payment", e.getMessage());
                }
            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new PaymentSummeryFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_driver, fragment).commit();
                } catch (Exception e) {
                    Log.d("Payment", e.getMessage());
                }
            }
        });
        btnUpdadte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                String mYear = String.valueOf(year);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you Want to Update Your Payment Data For Year: " + mYear);

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mProgressDialog.setMessage("Updating");
                        mProgressDialog.show();
                        mProgressDialog.setCancelable(false);
                        updatePayment();
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
        return rootView;
    }

    private void updatePayment() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("permanent").orderByChild("permanentStudent");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot data1 : data.getChildren()) {
                        for (DataSnapshot data2 : data1.getChildren()) {
                            if (data2.getKey().equals("info")) {
                                Student student = data2.getValue(Student.class);
                                students.add(student);
                            }
                        }

                    }
                }
                ApplicationClass.bus.post("Done");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Subscribe
    public void update(String tag) {
        for (int j = 0; j < students.size(); j++) {
            PaymentList paymentList = new PaymentList();
            paymentList.setStuID(students.get(j).getStuID());
            paymentList.setStuName(students.get(j).getStuName());
            for (int i = 11; i >= 0; i--) {
                //2017 need to use as mYear
                ref.child("Drivers").child(DriverActivity.userId).child("permanent").child("permanentStudent").child(students.get(j).getStuID()).child("payments").child("2017").child(monthArray[i]).child("status").setValue("Not Paid");
                //ref.child("Drivers").child(DriverActivity.userId).child("budget").child("paymentList").child("2017").child(monthArray[i]).child("notPaid").child(students.get(j).getStuID()).child("StuID").setValue(students.get(j).getStuID());
                ref.child("Drivers").child(DriverActivity.userId).child("budget").child("paymentList").child("2017").child(monthArray[i]).child("notPaid").child(students.get(j).getStuID()).setValue(paymentList);

            }
        }
        //
        mProgressDialog.dismiss();
        //Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();
        ApplicationClass.bus.unregister(this);

    }

}
