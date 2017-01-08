package com.example.kusha.schoolbus.fragments.parent;


import android.app.ProgressDialog;
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
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentPaymentFragment extends Fragment {
    View parentPaymentFragment;
    Spinner spinnerYear, spinnerMonth;
    TextView txtChildName, txtStatus,txtChildMonthlyPayment;
    ImageButton btnSearch;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    ProgressDialog mProgressDialog;

    public ParentPaymentFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentPaymentFragment = inflater.inflate(R.layout.fragment_parent_payment, container, false);
        spinnerMonth = (Spinner)parentPaymentFragment.findViewById(R.id.spinnerMonth);
        spinnerYear = (Spinner)parentPaymentFragment.findViewById(R.id.spinnerYear);
        txtChildName = (TextView)parentPaymentFragment.findViewById(R.id.txtChildName);
        txtStatus = (TextView)parentPaymentFragment.findViewById(R.id.txtStatus);
        txtChildMonthlyPayment = (TextView)parentPaymentFragment.findViewById(R.id.txtChildMonthlyPayment);
        btnSearch = (ImageButton) parentPaymentFragment.findViewById(R.id.btnSearchPayment);
        txtChildName.setText(ParentActivity.selectedChildName);
        mProgressDialog = new ProgressDialog(getActivity());
        spinnerYear.setSelection(1);
        if(ParentActivity.selectedChildId.length()==0){
            Toast.makeText(getActivity(), "Please Select a Child", Toast.LENGTH_SHORT).show();
            btnSearch.setEnabled(false);
        }
        if(ParentActivity.selectedChildId.length()!=0){
            ref.child("Drivers").child(ParentActivity.selectedDriverID).child("permanent").child("permanentStudent").child(ParentActivity.selectedChildId).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Student mStudent = dataSnapshot.getValue(Student.class);
                    txtChildMonthlyPayment.setText("Rs."+mStudent.getStuMonthlyFee());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setText("");
                if(ParentActivity.selectedChildId.length()!=0){
                    mProgressDialog.setMessage("Searching...");
                    mProgressDialog.show();
                    final String year = spinnerYear.getSelectedItem().toString();
                    final String month = spinnerMonth.getSelectedItem().toString();
                    ref.child("Drivers").child(ParentActivity.selectedDriverID).child("permanent").child("permanentStudent").child(ParentActivity.selectedChildId).child("payments").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(year)){
                                if(dataSnapshot.child(year).hasChild(month)){
                                    searchStatus(year,month);
                                }else {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "Please Select a Child", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return parentPaymentFragment;
    }

    private void searchStatus(String year,String month){
        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("permanent").child("permanentStudent").child(ParentActivity.selectedChildId).child("payments").child(year).child(month).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtStatus.setText(dataSnapshot.getValue().toString());
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
