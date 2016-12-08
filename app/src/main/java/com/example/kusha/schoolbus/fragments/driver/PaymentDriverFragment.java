package com.example.kusha.schoolbus.fragments.driver;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kusha.schoolbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDriverFragment extends Fragment {
    Button btnAddPayment,btnViewPayment,btnSummery;
    Fragment fragment;


    public PaymentDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_driver, container, false);
        btnAddPayment = (Button)rootView.findViewById(R.id.btnAddPayment);
        btnViewPayment = (Button)rootView.findViewById(R.id.btnViewPayment);
        btnSummery = (Button)rootView.findViewById(R.id.btnSummery);

        btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = new AddNewPaymentFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
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
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
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
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container, fragment).commit();
                } catch (Exception e) {
                    Log.d("Payment", e.getMessage());
                }
            }
        });
        return rootView;
    }

}
