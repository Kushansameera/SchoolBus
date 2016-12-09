package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.application.ApplicationClass;
import com.example.kusha.schoolbus.models.StudentPayment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewPaymentFragment extends Fragment {
    View addNewPaymentFragment;
    private EditText txtSearchId,txtAmount;
    private TextView txtStuName,txtMonthlyPayment,txtStudentReceivables,txtLastPaidMonth,txtDate;
    private Spinner spinnerYear,spinnerMonth;
    private Button btnAddPayment;
    private ImageButton btnSearchStudent;
    String mYear,mMonth;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    StudentPayment studentPayments = new StudentPayment();
    String[] monthArray;


    public AddNewPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addNewPaymentFragment = inflater.inflate(R.layout.fragment_add_new_payment, container, false);
        getActivity().setTitle("Add Payment");
        txtSearchId = (EditText)addNewPaymentFragment.findViewById(R.id.txtSearchId);
        txtAmount = (EditText)addNewPaymentFragment.findViewById(R.id.txtStudentFee);
        txtStuName = (TextView)addNewPaymentFragment.findViewById(R.id.txtStudentName);
        txtMonthlyPayment = (TextView)addNewPaymentFragment.findViewById(R.id.txtStudentMonthleFee);
        txtStudentReceivables = (TextView)addNewPaymentFragment.findViewById(R.id.txtStudentReceivables);
        txtLastPaidMonth = (TextView)addNewPaymentFragment.findViewById(R.id.txtStudentLastPaid);
        txtDate = (TextView)addNewPaymentFragment.findViewById(R.id.txtPaymentdate);
        spinnerYear = (Spinner)addNewPaymentFragment.findViewById(R.id.spinnerPaymentYear);
        spinnerMonth = (Spinner)addNewPaymentFragment.findViewById(R.id.spinnerPaymentMonth);
        btnAddPayment = (Button)addNewPaymentFragment.findViewById(R.id.btnAddPayment);
        btnSearchStudent = (ImageButton) addNewPaymentFragment.findViewById(R.id.btnSearchStudent);
        monthArray = getResources().getStringArray(R.array.months);
        ApplicationClass.bus.register(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        mYear = String.valueOf(year);
        mMonth = String.valueOf(month);
        spinnerMonth.setSelection(month-1);
        //spinnerYear.setSelection(year);
        btnSearchStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSearchId.getText().length()==0){
                    Toast.makeText(getActivity(), "Please Enter Student ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    CheckStudentExists();
                }
            }
        });



        return addNewPaymentFragment;

    }
    private void CheckStudentExists(){
        ref.child("Drivers").child(DriverActivity.userId).child("permanent").child("permanentStudent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(txtSearchId.getText().toString())){
                    Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
                    ApplicationClass.bus.post(1);
                }else {
                    Toast.makeText(getActivity(), "Child ID Doesn't Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Subscribe
    public void getPaymentData(Integer a){
        Log.d("<<<<>>>>","getPaymentData");
        ref.child("Drivers").child(DriverActivity.userId).child("permanent").child("permanentStudent").child(txtSearchId.getText().toString()).child("paymentInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentPayments = dataSnapshot.getValue(StudentPayment.class);
                //ApplicationClass.bus.post(1);
                showPaymentData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showPaymentData(){
        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        int date = calender.get(Calendar.DATE);
        int month = calender.get(Calendar.MONTH);
        int lastPaidMonth;
//        String thisMonth = monthArray[month-1];
//        String lastMonth = monthArray[month-2];
        for(int i=11;i>=0;i--){
            if(studentPayments.getStuLastPaidMonth().equals(monthArray[i])){
                lastPaidMonth = i+1;
                break;
            }
        }
        txtStuName.setText(studentPayments.getStuName());
        txtMonthlyPayment.setText(studentPayments.getStuMonthlyFee());
        txtStudentReceivables.setText(studentPayments.getStuReceivables());
        txtLastPaidMonth.setText(studentPayments.getStuLastPaidMonth());
        txtDate.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(date));


    }

}
