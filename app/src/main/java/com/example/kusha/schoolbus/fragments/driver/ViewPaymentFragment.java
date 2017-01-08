package com.example.kusha.schoolbus.fragments.driver;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.adapter.NotPaidStudentAdapter;
import com.example.kusha.schoolbus.adapter.PaidStudentAdapter;
import com.example.kusha.schoolbus.adapter.StudentAdapter;
import com.example.kusha.schoolbus.application.ApplicationClass;
import com.example.kusha.schoolbus.models.PaymentList;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ViewPaymentFragment extends Fragment {

    View viewPaymentFragment;
    RecyclerView rcvPaid, rcvNotPaid;
    Spinner spinnerYear, spinnerMonth;
    ImageButton btnSearchPayment;
    PaidStudentAdapter paidStudentAdapter;
    NotPaidStudentAdapter notPaidStudentAdapter;
    List<PaymentList> paidStudents = new ArrayList<>();
    List<PaymentList> notPaidStudents = new ArrayList<>();
    Student student;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ViewPaymentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewPaymentFragment = inflater.inflate(R.layout.fragment_view_payment, container, false);
        ApplicationClass.bus.register(this);
        rcvPaid = (RecyclerView) viewPaymentFragment.findViewById(R.id.rcvPaidStudents);
        rcvNotPaid = (RecyclerView) viewPaymentFragment.findViewById(R.id.rcvNotPaidStudents);
        spinnerYear = (Spinner) viewPaymentFragment.findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) viewPaymentFragment.findViewById(R.id.spinnerMonth);
        btnSearchPayment = (ImageButton) viewPaymentFragment.findViewById(R.id.btnSearchPayment);
        spinnerYear.setSelection(1);

        rcvPaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        paidStudentAdapter = new PaidStudentAdapter(getActivity(), paidStudents);
        rcvPaid.setAdapter(paidStudentAdapter);

        rcvNotPaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        notPaidStudentAdapter = new NotPaidStudentAdapter(getActivity(), notPaidStudents);
        rcvNotPaid.setAdapter(notPaidStudentAdapter);

        btnSearchPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = spinnerYear.getSelectedItem().toString();
                String month = spinnerMonth.getSelectedItem().toString();
                getPaidStudents(year, month);
                getNotPaidStudents(year, month);
            }
        });

        paidStudentAdapter.setOnItemClickListener(new PaidStudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String stuID = paidStudents.get(position).getStuID();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_item:
                                getStudentData(stuID);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.view_source, popup.getMenu());
                popup.show();
            }
        });

        notPaidStudentAdapter.setOnItemClickListener(new NotPaidStudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String stuID = notPaidStudents.get(position).getStuID();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_item:
                                getStudentData(stuID);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.view_source, popup.getMenu());
                popup.show();
            }
        });

        return viewPaymentFragment;
    }

    private void getPaidStudents(String year, String month) {
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("budget").child("paymentList").child(year).child(month).orderByChild("paid");
        paidStudents.clear();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paidStudents.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals("paid")) {
                        for (DataSnapshot data1 : data.getChildren()) {
                            PaymentList list = data1.getValue(PaymentList.class);
                            paidStudents.add(list);
                        }
                    }

                }
                paidStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getNotPaidStudents(String year, String month) {
        Query queryRef;
        queryRef = ref.child("Drivers").child(DriverActivity.userId).child("budget").child("paymentList").child(year).child(month).orderByChild("notPaid");
        notPaidStudents.clear();
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notPaidStudents.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals("notPaid")) {
                        for (DataSnapshot data1 : data.getChildren()) {
                            PaymentList list = data1.getValue(PaymentList.class);
                            notPaidStudents.add(list);
                        }
                    }

                }
                notPaidStudentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getStudentData(String stuID){
        ref.child("Drivers").child(DriverActivity.userId).child("permanent").child("permanentStudent").child(stuID).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                ApplicationClass.bus.post(student);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Subscribe
    public void showStudentData(Student student){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Student Info");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView name = new TextView(getActivity());
        final TextView school = new TextView(getActivity());
        final TextView payment = new TextView(getActivity());

        name.setText   ("\t\t\t\t\t\tName            : "+student.getStuName());
        school.setText ("\t\t\t\t\t\tSchool           : "+student.getStuSchool());
        payment.setText("\t\t\t\t\t\tMonthly Fee : Rs. "+student.getStuMonthlyFee());

        layout.addView(name);
        layout.addView(school);
        layout.addView(payment);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ApplicationClass.bus.unregister(this);
    }
}
