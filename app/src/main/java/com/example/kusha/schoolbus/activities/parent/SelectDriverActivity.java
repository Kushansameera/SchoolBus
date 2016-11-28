package com.example.kusha.schoolbus.activities.parent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kusha.schoolbus.R;
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
    List<ManageDrivers> manageDrivers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_driver);
        setTitle("Select Driver");

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        showDrivers();

        select = (Button)findViewById(R.id.btnDone);
        driverRadioGroup = (RadioGroup)findViewById(R.id.radioGroupDrivers);




        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioId = driverRadioGroup.getCheckedRadioButtonId();
                //RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioId);
                ParentActivity.selectedDriverEmail = manageDrivers.get(selectedRadioId-1).getDriverEmail().toString().trim();
                Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDrivers(){
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


}
