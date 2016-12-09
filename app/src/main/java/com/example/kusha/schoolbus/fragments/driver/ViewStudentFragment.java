package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.Student;
import com.example.kusha.schoolbus.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewStudentFragment extends Fragment {

    public static String currentStudentId = "";
    public static String driverId = "";
    private TextView txtCurrentStuName, txtCurrentStuSchool, txtCurrentStuGender, txtCurrentStuGrade, txtCurrentStuClass, txtCurrentStuPickup, txtCurrentStuDrop, txtCurrentStuPickupTime, txtCurrentStuMonthlyFee;
    private Button btnPerStuEdit, btnPerStuSave, btnPerStuDelete;
    ImageButton btnParentInfo;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private Student student;
    private User user;
    private static String latestStudentID;
    boolean flag = false;
    private ProgressDialog mProgressDialog;


    public ViewStudentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewStudentFragment = inflater.inflate(R.layout.fragment_view_student, container, false);

        txtCurrentStuName = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuName);
        txtCurrentStuSchool = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuSchool);
        txtCurrentStuGender = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuGender);
        txtCurrentStuGrade = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuGrade);
        txtCurrentStuClass = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuClass);
        txtCurrentStuPickup = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuPick);
        txtCurrentStuDrop = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuDrop);
        txtCurrentStuPickupTime = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuPickupTime);
        txtCurrentStuMonthlyFee = (EditText) viewStudentFragment.findViewById(R.id.txtPerStuFee);
        btnPerStuEdit = (Button) viewStudentFragment.findViewById(R.id.btnPerStuEdit);
        btnPerStuSave = (Button) viewStudentFragment.findViewById(R.id.btnPerStuSave);
        btnPerStuDelete = (Button) viewStudentFragment.findViewById(R.id.btnPerStuDelete);
        btnParentInfo = (ImageButton) viewStudentFragment.findViewById(R.id.btnParentInfo);

        disableFields();
        getStudentData();

        btnPerStuEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableFields();
            }
        });

        btnPerStuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtCurrentStuGrade.getText().length()==0) {
                    Toast.makeText(getActivity(), "Student Grade Cannot Be Empty", Toast.LENGTH_SHORT).show();
                } else if (txtCurrentStuClass.getText().length()==0) {
                    Toast.makeText(getActivity(), "Student Class Cannot Be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    saveStudentData();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.current_student_frame_container, new ViewStudentFragment());
                    ft.commit();
                }

            }
        });

        btnPerStuDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure,You wanted to Delete This Student ");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteStudentData();
                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.current_student_frame_container, new CurrentStudentsFragment());
                        ft.commit();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnParentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentId();
            }
        });

        return viewStudentFragment;
    }

    private void getStudentData() {
        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(currentStudentId).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                txtCurrentStuName.setText(student.getStuName());
                txtCurrentStuSchool.setText(student.getStuSchool());
                txtCurrentStuGender.setText(student.getStuGender());
                txtCurrentStuGrade.setText(student.getStuGrade());
                txtCurrentStuClass.setText(student.getStuClass());
                txtCurrentStuPickup.setText(student.getStuPickLocation());
                txtCurrentStuDrop.setText(student.getStuDropLocation());
                txtCurrentStuPickupTime.setText(student.getStuPickTime());
                txtCurrentStuMonthlyFee.setText(student.getStuMonthlyFee());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void disableFields() {
        txtCurrentStuName.setEnabled(false);
        txtCurrentStuSchool.setEnabled(false);
        txtCurrentStuGender.setEnabled(false);
        txtCurrentStuClass.setEnabled(false);
        txtCurrentStuGrade.setEnabled(false);
        txtCurrentStuPickup.setEnabled(false);
        txtCurrentStuDrop.setEnabled(false);
        txtCurrentStuPickupTime.setEnabled(false);
        txtCurrentStuMonthlyFee.setEnabled(false);
        btnPerStuSave.setEnabled(false);
    }

    private void enableFields() {
        txtCurrentStuClass.setEnabled(true);
        txtCurrentStuGrade.setEnabled(true);
        btnPerStuSave.setEnabled(true);
    }

    private void saveStudentData() {

        student.setStuGrade(txtCurrentStuGrade.getText().toString());
        student.setStuClass(txtCurrentStuClass.getText().toString());

        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(currentStudentId).child("info").setValue(student);
        Toast.makeText(getActivity(), "Student's Data Edited", Toast.LENGTH_SHORT).show();

        txtCurrentStuClass.setEnabled(false);
        txtCurrentStuGrade.setEnabled(false);
        btnPerStuSave.setEnabled(false);

        getStudentData();
    }

    private void deleteStudentData() {
        ref.child("Drivers").child(driverId).child("permanent").child("permanentStudent").child(currentStudentId).removeValue();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.current_student_frame_container, new CurrentStudentsFragment());
        ft.commit();
    }

    private void getParentId(){
        Query queryRef;
        String parentEmail = student.getParentEmail();
        queryRef = ref.child("Parents").orderByChild("email").equalTo(parentEmail);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String parentID;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    parentID = data.getKey();
                    getParentDetails(parentID);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getParentDetails(String parentID){
        ref.child("Users").child(parentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                showParentData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showParentData(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Parent Info");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView name = new TextView(getActivity());
        final TextView email = new TextView(getActivity());
        final TextView phone = new TextView(getActivity());

        name.setText("\t\t\t\t\t\tName  : "+user.getName());
        email.setText("\t\t\t\t\t\tEmail  : "+user.getEmail());
        phone.setText("\t\t\t\t\t\tPhone : "+user.getPhoneNumber());

        layout.addView(name);
        layout.addView(email);
        layout.addView(phone);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

}
