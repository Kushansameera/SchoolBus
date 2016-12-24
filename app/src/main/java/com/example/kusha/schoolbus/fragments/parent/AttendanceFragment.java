package com.example.kusha.schoolbus.fragments.parent;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;


public class AttendanceFragment extends Fragment {

    View attendanceFragment;
    TextView txtCurrentChildName,txtCurrentChildType,txtCurrentDate;
    RadioGroup radioGroupMorning,radioGroupEvening;
    Button btnSubmit;
    Student myChild;
    String[] monthArray;
    private ProgressDialog mPrograssDialog;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public AttendanceFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        attendanceFragment = inflater.inflate(R.layout.fragment_attendance, container, false);
        txtCurrentChildName = (TextView)attendanceFragment.findViewById(R.id.txtCurrentChildName);
        txtCurrentChildType = (TextView)attendanceFragment.findViewById(R.id.txtCurrentChildType);
        txtCurrentDate = (TextView)attendanceFragment.findViewById(R.id.txtCurrentDate);
        radioGroupMorning = (RadioGroup)attendanceFragment.findViewById(R.id.radioGroupMorning);
        radioGroupEvening = (RadioGroup)attendanceFragment.findViewById(R.id.radioGroupEvening);
        btnSubmit = (Button)attendanceFragment.findViewById(R.id.btnSubmit);
        monthArray = getResources().getStringArray(R.array.months);
        mPrograssDialog = new ProgressDialog(getActivity());

        if(ParentActivity.selectedChildId.length()==0){
            for (int i = 0; i < radioGroupEvening.getChildCount(); i++) {
                radioGroupEvening.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < radioGroupMorning.getChildCount(); i++) {
                radioGroupMorning.getChildAt(i).setEnabled(false);
            }
            btnSubmit.setEnabled(false);
            Toast.makeText(getActivity(), "You Haven't Selected Child ", Toast.LENGTH_SHORT).show();
        }
        else {
            ref.child("Drivers").child(ParentActivity.selectedDriverID).child("permanent").child("permanentStudent").child(ParentActivity.selectedChildId).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPrograssDialog.show();
                    myChild = dataSnapshot.getValue(Student.class);
                    setFields();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            String responseMorning;
            String responseEvening;

            @Override
            public void onClick(View v) {
                if(myChild.getStuType().equals("Morning Only")){
                    int selectedId = radioGroupMorning.getCheckedRadioButtonId();
                    RadioButton morningRadioButton = (RadioButton)radioGroupMorning.findViewById(selectedId);
                    responseMorning = morningRadioButton.getText().toString();
                    for (int i = 0; i < radioGroupMorning.getChildCount(); i++) {
                        radioGroupMorning.getChildAt(i).setEnabled(false);
                    }
                    ref.child("Drivers").child(ParentActivity.selectedDriverID).child("route").child("morningRoute").child(myChild.getStuID()).child("attend").setValue(responseMorning);
                }else if(myChild.getStuType().equals("Evening Only")){
                    int selectedId = radioGroupEvening.getCheckedRadioButtonId();
                    RadioButton eveningRadioButton = (RadioButton)radioGroupEvening.findViewById(selectedId);
                    responseEvening = eveningRadioButton.getText().toString();
                    for (int i = 0; i < radioGroupEvening.getChildCount(); i++) {
                        radioGroupEvening.getChildAt(i).setEnabled(false);
                    }
                    ref.child("Drivers").child(ParentActivity.selectedDriverID).child("route").child("eveningRoute").child(myChild.getStuID()).child("attend").setValue(responseEvening);
                }else {
                    int selectedIdMorning = radioGroupMorning.getCheckedRadioButtonId();
                    RadioButton morningRadioButton = (RadioButton)radioGroupMorning.findViewById(selectedIdMorning);
                    responseMorning = morningRadioButton.getText().toString();
                    int selectedIdEvening = radioGroupEvening.getCheckedRadioButtonId();
                    RadioButton eveningRadioButton = (RadioButton)radioGroupEvening.findViewById(selectedIdEvening);
                    responseEvening = eveningRadioButton.getText().toString();

                    for (int i = 0; i < radioGroupMorning.getChildCount(); i++) {
                        radioGroupMorning.getChildAt(i).setEnabled(false);
                    }
                    for (int i = 0; i < radioGroupEvening.getChildCount(); i++) {
                        radioGroupEvening.getChildAt(i).setEnabled(false);
                    }

                    ref.child("Drivers").child(ParentActivity.selectedDriverID).child("route").child("morningRoute").child(myChild.getStuID()).child("attend").setValue(responseMorning);
                    ref.child("Drivers").child(ParentActivity.selectedDriverID).child("route").child("eveningRoute").child(myChild.getStuID()).child("attend").setValue(responseEvening);
                }
                Toast.makeText(getActivity(), "Successfully Submitted", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(false);
            }
        });

        return attendanceFragment;
    }

    private void setFields() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        txtCurrentChildName.setText(myChild.getStuName());
        if (myChild.getStuType().equals("Both")){
            txtCurrentChildType.setText("Morning & Evening");
        }else {
            txtCurrentChildType.setText(myChild.getStuType());
        }
        txtCurrentDate.setText(String.valueOf(year)+"-"+monthArray[month]+"-"+String.valueOf(date));

        if(myChild.getStuType().equals("Morning Only")){
            for (int i = 0; i < radioGroupEvening.getChildCount(); i++) {
                radioGroupEvening.getChildAt(i).setEnabled(false);
            }
        }else if(myChild.getStuType().equals("Evening Only")){
            for (int i = 0; i < radioGroupMorning.getChildCount(); i++) {
                radioGroupMorning.getChildAt(i).setEnabled(false);
            }
        }
        mPrograssDialog.dismiss();
    }

}
