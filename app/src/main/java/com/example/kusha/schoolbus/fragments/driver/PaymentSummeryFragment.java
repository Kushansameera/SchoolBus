package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentSummeryFragment extends Fragment {

    View paymentSummeryFragment;
    Spinner spinnerYear, spinnerMonth;
    TextView txtTargetIncome, txtCurrentIncome, txtReceivables;
    ImageButton btnSearch;

    public PaymentSummeryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paymentSummeryFragment = inflater.inflate(R.layout.fragment_payment_summery, container, false);

        spinnerYear = (Spinner) paymentSummeryFragment.findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) paymentSummeryFragment.findViewById(R.id.spinnerMonth);
        txtCurrentIncome = (TextView) paymentSummeryFragment.findViewById(R.id.txtCurrentIncome);
        txtTargetIncome = (TextView) paymentSummeryFragment.findViewById(R.id.txtTargetIncome);
        txtReceivables = (TextView) paymentSummeryFragment.findViewById(R.id.txtReceivables);
        btnSearch = (ImageButton) paymentSummeryFragment.findViewById(R.id.btnSearchSummery);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return paymentSummeryFragment;
    }

}
