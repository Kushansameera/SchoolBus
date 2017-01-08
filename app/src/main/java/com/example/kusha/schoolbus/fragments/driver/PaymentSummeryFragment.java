package com.example.kusha.schoolbus.fragments.driver;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.application.ReportCreator;
import com.example.kusha.schoolbus.models.PaymentSummery;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    Button btnCreatePdf;
    private PaymentSummery paymentSummery;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String year;
    private String month;
    private int totalIncome;
    private int currentIncome;
    private Bitmap pieChartImage;

    //private float[] yData;
    private String[] xData = {"Receivables", "Received"};
    PieChart pieChart=null;

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
        btnCreatePdf = (Button)paymentSummeryFragment.findViewById(R.id.btnCreatePdf);
        //pieChart = (PieChart) paymentSummeryFragment.findViewById(R.id.summeryChart);
        spinnerYear.setSelection(1);
        btnCreatePdf.setEnabled(false);
        pieChart = new PieChart(paymentSummeryFragment.getContext());
        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        btnCreatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentSummery = new PaymentSummery();
                paymentSummery.setTargetIncome("Rs. "+String.valueOf(totalIncome));
                paymentSummery.setReceived("Rs. "+String.valueOf(currentIncome));
                paymentSummery.setReceivables("Rs. "+String.valueOf(totalIncome-currentIncome));
                paymentSummery.setYear(year);
                paymentSummery.setMonth(month);

                createPdf();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = spinnerYear.getSelectedItem().toString();
                month = spinnerMonth.getSelectedItem().toString();
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
                                btnCreatePdf.setEnabled(false);
                                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            btnCreatePdf.setEnabled(false);
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

    private void createPdf(){
        try {
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.buslogo);
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            Image imgLogo = Image.getInstance(stream.toByteArray());



            stream = new ByteArrayOutputStream();
            pieChartImage.compress(Bitmap.CompressFormat.PNG, 50, stream);
            Image imgChart = Image.getInstance(stream.toByteArray());


            ReportCreator newReport = new ReportCreator(DriverActivity.folder+"/PaymentSummery_"+paymentSummery.getYear()+"_"+paymentSummery.getMonth()+".pdf",paymentSummery);
            newReport.setImgLogo(imgLogo);
            newReport.setImgChart(imgChart);
            newReport.createPDF();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(DriverActivity.folder + "/PaymentSummery_"+paymentSummery.getYear()+"_"+paymentSummery.getMonth()+".pdf");
            intent.setDataAndType( Uri.fromFile( file ), "application/pdf" );
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchSummery(String year,String month){
        ref.child("Drivers").child(DriverActivity.userId).child("budget").child("summery").child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalIncome = Integer.parseInt(dataSnapshot.child("totalIncome").getValue().toString());
                currentIncome = Integer.parseInt(dataSnapshot.child("currentIncome").getValue().toString());
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
        pieChartImage = pieChart.getChartBitmap();
        pieChart.invalidate();
        btnCreatePdf.setEnabled(true);
    }

}
