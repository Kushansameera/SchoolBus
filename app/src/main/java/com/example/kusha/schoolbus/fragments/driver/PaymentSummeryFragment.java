package com.example.kusha.schoolbus.fragments.driver;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentSummeryFragment extends Fragment {

    View paymentSummeryFragment;
    Spinner spinnerYear, spinnerMonth;
    TextView txtTargetIncome, txtCurrentIncome, txtReceivables;
    ImageButton btnSearch;
    LinearLayout myLayout;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    //private float[] yData;
    private String[] xData = {"Receivables", "Received"};
    PieChart pieChart;

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
        myLayout = (LinearLayout)paymentSummeryFragment.findViewById(R.id.myLayout);
        //pieChart = (PieChart) paymentSummeryFragment.findViewById(R.id.summeryChart);
        pieChart = new PieChart(paymentSummeryFragment.getContext());
        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String year = spinnerYear.getSelectedItem().toString();
                final String month = spinnerMonth.getSelectedItem().toString();
                txtReceivables.setText("");
                txtTargetIncome.setText("");
                txtCurrentIncome.setText("");
                myLayout.removeAllViews();
                ref.child("Drivers").child(DriverActivity.userId).child("budget").child("summery").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(year)){
                            if(dataSnapshot.child(year).hasChild(month)){
                                myLayout.addView(pieChart);
                                searchSummery(year,month);
                            }else {
                                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        });

        return paymentSummeryFragment;
    }

    private void searchSummery(String year,String month){
        ref.child("Drivers").child(DriverActivity.userId).child("budget").child("summery").child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalIncome = Integer.parseInt(dataSnapshot.child("totalIncome").getValue().toString());
                int currentIncome = Integer.parseInt(dataSnapshot.child("currentIncome").getValue().toString());
                setFields(totalIncome,currentIncome);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setFields(int totalIncome,int currentIncome){
        txtTargetIncome.setText("Rs. "+String.valueOf(totalIncome));
        txtCurrentIncome.setText("Rs. "+String.valueOf(currentIncome));
        int receivables = totalIncome-currentIncome;
        txtReceivables.setText("Rs. "+String.valueOf(receivables));
        float data1 = receivables;
        float data2 = currentIncome;
        final float [] yData = {data1,data2};
        addDataSet(yData);
    }

    private void addDataSet(float[] yData) {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , xData[i]));
        }

        for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setTextSize(12);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);
        //legend.setExtra(colors, Arrays.asList(xData));


        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}
