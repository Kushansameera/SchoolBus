package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.app.Fragment;
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
    private EditText txtSearchId, txtAmount;
    private TextView txtStuName, txtMonthlyPayment, txtStudentReceivables, txtLastPaidMonth, txtDate, txtTotalPayment;
    private Spinner spinnerYear, spinnerMonth;
    private Button btnAddPayment;
    private ImageButton btnSearchStudent;
    String mYear, mMonth, mDate;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    StudentPayment studentPayments = new StudentPayment();
    String[] monthArray;
    int year, date, month;

    public AddNewPaymentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addNewPaymentFragment = inflater.inflate(R.layout.fragment_add_new_payment, container, false);
        getActivity().setTitle("Add Payment");
        txtSearchId = (EditText) addNewPaymentFragment.findViewById(R.id.txtSearchId);
        txtAmount = (EditText) addNewPaymentFragment.findViewById(R.id.txtStudentFee);
        txtStuName = (TextView) addNewPaymentFragment.findViewById(R.id.txtStudentName);
        txtMonthlyPayment = (TextView) addNewPaymentFragment.findViewById(R.id.txtStudentMonthleFee);
        txtStudentReceivables = (TextView) addNewPaymentFragment.findViewById(R.id.txtStudentReceivables);
        txtLastPaidMonth = (TextView) addNewPaymentFragment.findViewById(R.id.txtStudentLastPaid);
        txtDate = (TextView) addNewPaymentFragment.findViewById(R.id.txtPaymentdate);
        txtTotalPayment = (TextView) addNewPaymentFragment.findViewById(R.id.txtStudentTotalPayment);
        spinnerYear = (Spinner) addNewPaymentFragment.findViewById(R.id.spinnerPaymentYear);
        spinnerMonth = (Spinner) addNewPaymentFragment.findViewById(R.id.spinnerPaymentMonth);
        btnAddPayment = (Button) addNewPaymentFragment.findViewById(R.id.btnAddPayment);
        btnSearchStudent = (ImageButton) addNewPaymentFragment.findViewById(R.id.btnSearchStudent);
        monthArray = getResources().getStringArray(R.array.months);
        ApplicationClass.bus.register(this);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        date = calendar.get(Calendar.DATE);
        mYear = String.valueOf(year);
        mMonth = String.valueOf(month);
        mDate = String.valueOf(date);
        spinnerMonth.setSelection(month-1);
        //spinnerYear.setSelection(year);
        btnSearchStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearchId.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please Enter Student ID", Toast.LENGTH_SHORT).show();
                } else {
                    CheckStudentExists();
                }
            }
        });


        return addNewPaymentFragment;

    }

    private void CheckStudentExists() {
        ref.child("Drivers").child(DriverActivity.userId).child("permanent").child("permanentStudent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(txtSearchId.getText().toString())) {
                    ApplicationClass.bus.post(1);
                } else {
                    Toast.makeText(getActivity(), "Child ID Doesn't Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Subscribe
    public void getPaymentData(Integer a) {
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

    private void setReceivables() {
        int lastPaidMonth = 0, receivables = 0, monthlyPayment = 0;
        String lastPaidYear = studentPayments.getStuLastPaidYear();
        if (studentPayments.getStuReceivables().toString().length() != 0) {
            receivables = Integer.parseInt(studentPayments.getStuReceivables());
        }
        monthlyPayment = Integer.parseInt(studentPayments.getStuMonthlyFee());
        for (int i = 11; i >= 0; i--) {
            if (studentPayments.getStuLastPaidMonth().equals(monthArray[i])) {
                lastPaidMonth = i + 1;
                break;
            }
        }
        if(lastPaidYear.length()==0){
            txtStudentReceivables.setText("Rs. "+studentPayments.getStuReceivables());
            txtTotalPayment.setText("Rs. "+String.valueOf(receivables+monthlyPayment));
        }
        else if (lastPaidYear.equals(mYear) && lastPaidMonth == month-1) {
            txtStudentReceivables.setText("Rs. "+studentPayments.getStuReceivables());
            txtTotalPayment.setText("Rs. "+String.valueOf(receivables+monthlyPayment));
        }
        else if(lastPaidYear.equals(mYear) && lastPaidMonth != month-1){
            int monthRange = month-lastPaidMonth-1;
            txtStudentReceivables.setText("Rs. "+String.valueOf(monthlyPayment*monthRange+receivables));
            txtTotalPayment.setText("Rs. "+String.valueOf(monthlyPayment*monthRange+receivables+monthlyPayment));
        }
        else if(!lastPaidYear.equals(mYear)){
            int monthRange = month-lastPaidMonth+11;
            txtStudentReceivables.setText("Rs. "+String.valueOf(monthlyPayment*monthRange+receivables));
            txtTotalPayment.setText("Rs. "+String.valueOf(monthlyPayment*monthRange+receivables+monthlyPayment));
        }

    }

    public void showPaymentData() {
        setReceivables();
        txtStuName.setText(studentPayments.getStuName());
        txtMonthlyPayment.setText("Rs. "+studentPayments.getStuMonthlyFee());
        txtLastPaidMonth.setText(studentPayments.getStuLastPaidYear() + "/" + studentPayments.getStuLastPaidMonth());
        txtDate.setText(mYear + "-" + mMonth + "-" + mDate);


    }

}
