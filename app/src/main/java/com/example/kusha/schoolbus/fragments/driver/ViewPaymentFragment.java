package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.NotPaidStudentAdapter;
import com.example.kusha.schoolbus.adapter.PaidStudentAdapter;
import com.example.kusha.schoolbus.adapter.StudentAdapter;
import com.example.kusha.schoolbus.models.PaymentList;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


public class ViewPaymentFragment extends Fragment {

    View viewPaymentFragment;
    RecyclerView rcvPaid,rcvNotPaid;
    PaidStudentAdapter paidStudentAdapter;
    NotPaidStudentAdapter notPaidStudentAdapter;
    List<PaymentList> paidStudents = new ArrayList<>();
    List<PaymentList> notPaidStudents = new ArrayList<>();
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    public ViewPaymentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewPaymentFragment = inflater.inflate(R.layout.fragment_view_payment, container, false);

        rcvPaid = (RecyclerView)viewPaymentFragment.findViewById(R.id.rcvPaidStudents);
        rcvNotPaid = (RecyclerView)viewPaymentFragment.findViewById(R.id.rcvNotPaidStudents);

        rcvPaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        paidStudentAdapter = new PaidStudentAdapter(getActivity(), paidStudents);
        rcvPaid.setAdapter(paidStudentAdapter);

        rcvNotPaid.setLayoutManager(new LinearLayoutManager(getActivity()));
        notPaidStudentAdapter = new NotPaidStudentAdapter(getActivity(), notPaidStudents);
        rcvNotPaid.setAdapter(notPaidStudentAdapter);

        return viewPaymentFragment;
    }

    private void getPaidStudents(){

    }

    private void getNotPaidStudents(){

    }

}
