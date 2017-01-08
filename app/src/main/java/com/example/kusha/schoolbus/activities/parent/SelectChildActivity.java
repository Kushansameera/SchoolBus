package com.example.kusha.schoolbus.activities.parent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.Children;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectChildActivity extends AppCompatActivity {
    private RadioGroup childRadioGroup;
    private Button btnSelect;

    List<Children> childrens = new ArrayList<>();
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String userId;
    int selectedRadioId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child);
        setTitle("Select Child");

        btnSelect = (Button)findViewById(R.id.btnSelect) ;
        childRadioGroup = (RadioGroup)findViewById(R.id.radioGroupChildren);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        showChildren();

        childRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioId = checkedId;
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedRadioId==0){
                    Toast.makeText(SelectChildActivity.this, "Please Select a Child", Toast.LENGTH_SHORT).show();
                }
                else {
                    selectedRadioId = childRadioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton)findViewById(selectedRadioId);
                    String name = selectedRadioButton.getText().toString();
                    for(int i=0;i<childrens.size();i++){
                        if(name.equals(childrens.get(i).getStuName())){
                            ParentActivity.selectedChildId = childrens.get(i).getStuId();
                            ParentActivity.selectedChildName= childrens.get(i).getStuName();
                            break;
                        }
                    }
                    Intent intent = new Intent(SelectChildActivity.this, ParentActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void showChildren() {
        childrens.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(userId).child("children").child(ParentActivity.selectedDriverID);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrens.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Children c = data.getValue(Children.class);
                    RadioButton driverRadioButton = new RadioButton(SelectChildActivity.this);
                    driverRadioButton.setText(c.getStuName());
                    driverRadioButton.setTextSize(15);
                    childRadioGroup.addView(driverRadioButton);
                    childrens.add(c);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

//    private void showChildren() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("Select Your Child");
//        for (int i = 0; i < childrens.size(); i++) {
//            RadioButton childRadioButton = new RadioButton(SelectDriverActivity.this);
//            childRadioButton.setText(childrens.get(i).getStuName());
//            childRadioButton.setTextSize(15);
//            childRadioButton.setId(i+1);
//            childRadioGroup.addView(childRadioButton);
//        }
//
//        alertDialogBuilder.setView(childRadioGroup);
//
//        alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                int selectedRadioId1 = childRadioGroup.getCheckedRadioButtonId();
////                ParentActivity.selectedChildId = childrens.get(selectedRadioId-1).getStuId().toString().trim();
////                ParentActivity.selectedChildName = childrens.get(selectedRadioId-1).getStuName().toString().trim();
//                RadioButton selectedRadioButton1 = (RadioButton)findViewById(selectedRadioId1);
//                String name = selectedRadioButton1.getText().toString();
//                for(int i=0;i<childrens.size();i++){
//                    if(name.equals(childrens.get(i).getStuName())){
//                        ParentActivity.selectedChildId = childrens.get(i).getStuId();
//                        ParentActivity.selectedChildName = childrens.get(i).getStuName();
//                        break;
//                    }
//                }
//
//                Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
//                startActivity(intent);
//                //Toast.makeText(SelectDriverActivity.this, "id= "+ParentActivity.selectedChildId+"\n Name= "+ParentActivity.selectedChildName, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        alertDialogBuilder.show();
//    }
}
