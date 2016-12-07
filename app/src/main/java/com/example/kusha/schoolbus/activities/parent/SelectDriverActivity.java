package com.example.kusha.schoolbus.activities.parent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.Children;
import com.example.kusha.schoolbus.models.ManageDrivers;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectDriverActivity extends AppCompatActivity {

    Button select;

    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    public FirebaseAuth mFirebaseAuth;
    private RadioGroup driverRadioGroup;
    private RadioGroup childRadioGroup;
    List<ManageDrivers> manageDrivers = new ArrayList<>();
    List<Children> childrens = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_driver);
        setTitle("Select Driver");

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        mProgressDialog = new ProgressDialog(SelectDriverActivity.this);
        showDrivers();

        select = (Button) findViewById(R.id.btnDone);
        driverRadioGroup = (RadioGroup) findViewById(R.id.radioGroupDrivers);
        childRadioGroup = new RadioGroup(SelectDriverActivity.this);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Wait");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
                int selectedRadioId = driverRadioGroup.getCheckedRadioButtonId();
                //RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioId);
//                ParentActivity.selectedDriverEmail = manageDrivers.get(selectedRadioId - 1).getDriverEmail().toString().trim();
//                ParentActivity.selectedDriverID = manageDrivers.get(selectedRadioId - 1).getDriverId().toString().trim();
                RadioButton selectedRadioButton = (RadioButton)findViewById(selectedRadioId);
                String name = selectedRadioButton.getText().toString();
                for(int i=0;i<manageDrivers.size();i++){
                    if(name.equals(manageDrivers.get(i).getDriverName())){
                        ParentActivity.selectedDriverEmail = manageDrivers.get(i).getDriverEmail();
                        ParentActivity.selectedDriverID = manageDrivers.get(i).getDriverId();
                        ParentActivity.selectedDriverName = manageDrivers.get(i).getDriverName();
                        break;
                    }
                }
                ref.child("Parents").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("children")) {
                            ref.child("Parents").child(userId).child("children").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(ParentActivity.selectedDriverID)){
                                        getChildData();
                                    }
                                    else{
                                        ParentActivity.selectedChildId = "";
                                        ParentActivity.selectedChildName = "";
                                        mProgressDialog.dismiss();
                                        Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });


                        } else {
                            ParentActivity.selectedChildId = "";
                            ParentActivity.selectedChildName = "";
                            mProgressDialog.dismiss();
                            Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

//                Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
//                startActivity(intent);
            }
        });
    }

    public void showDrivers() {
        manageDrivers.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("driver").orderByChild("regDrivers");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        ManageDrivers m = d.getValue(ManageDrivers.class);
                        RadioButton driverRadioButton = new RadioButton(SelectDriverActivity.this);
                        driverRadioButton.setText(m.getDriverName());
                        driverRadioButton.setTextSize(15);
                        driverRadioGroup.addView(driverRadioButton);
                        manageDrivers.add(m);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getChildData() {
        childrens.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("children").child(ParentActivity.selectedDriverID);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrens.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Children c = data.getValue(Children.class);
                    childrens.add(c);
                }
                showChildren();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showChildren() {
        mProgressDialog.dismiss();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select Your Child");

        for (int i = 0; i < childrens.size(); i++) {
            RadioButton childRadioButton = new RadioButton(SelectDriverActivity.this);
            childRadioButton.setText(childrens.get(i).getStuName());
            childRadioButton.setTextSize(15);
            childRadioGroup.addView(childRadioButton);
        }

        alertDialogBuilder.setView(childRadioGroup);

        alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedRadioId = childRadioGroup.getCheckedRadioButtonId()-childrens.size();
                ParentActivity.selectedChildId = childrens.get(selectedRadioId-1).getStuId().toString().trim();
                ParentActivity.selectedChildName = childrens.get(selectedRadioId-1).getStuName().toString().trim();
                Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
                startActivity(intent);
                //Toast.makeText(SelectDriverActivity.this, "id= "+ParentActivity.selectedChildId+"\n Name= "+ParentActivity.selectedChildName, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.show();
    }


}
