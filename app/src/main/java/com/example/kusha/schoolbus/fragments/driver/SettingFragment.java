package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    public FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private ProgressDialog progressDialog;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    private String userId;
    TextView txtCurrentAccessKey;
    EditText txtNewAccessKey;
    Button btnCreateNewKey,btnRemoveCurrentKey;

    public SettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingFragment = inflater.inflate(R.layout.fragment_setting, container, false);
        txtCurrentAccessKey = (TextView)settingFragment.findViewById(R.id.txtCurrentAccessKey);
        txtNewAccessKey = (EditText)settingFragment.findViewById(R.id.txtNewAccessKey);
        btnCreateNewKey = (Button)settingFragment.findViewById(R.id.btnCreateNewKey);
        btnRemoveCurrentKey = (Button)settingFragment.findViewById(R.id.btnRemoveCurrentKey);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        setCurrentAccessKey();

        btnCreateNewKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccessKey();
                getCurrentAccessKey();
            }
        });

        btnRemoveCurrentKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAccessKey();
            }
        });

        return settingFragment;
    }

    public void setCurrentAccessKey(){
        ref.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("accessKey")) {
                    getCurrentAccessKey();
                } else {
                    btnRemoveCurrentKey.setEnabled(false);
                    txtCurrentAccessKey.setText("");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getCurrentAccessKey(){

        ref.child("Drivers").child(userId).child("accessKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("0")){
                    txtCurrentAccessKey.setText("");
                }
                else {
                    txtCurrentAccessKey.setText(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void createNewAccessKey(){
        if(txtNewAccessKey.getText().length()!=0 && !txtNewAccessKey.getText().toString().equals("0")){
            if(txtNewAccessKey.getText().length()<4){
                Toast.makeText(getActivity(), "Please Enter At Least 4 Digits ", Toast.LENGTH_SHORT).show();
            }
            else {
                ref.child("Drivers").child(userId).child("accessKey").setValue(txtNewAccessKey.getText().toString());
                btnRemoveCurrentKey.setEnabled(true);
                txtNewAccessKey.setText("");
            }

        }else {
            txtNewAccessKey.setText("");
            Toast.makeText(getActivity(), "Please Enter a Access Key", Toast.LENGTH_SHORT).show();
        }

    }

    public void removeAccessKey(){
        if(txtCurrentAccessKey.getText().length()!=0){
            ref.child("Drivers").child(userId).child("accessKey").setValue("0");
            btnRemoveCurrentKey.setEnabled(false);
        }

    }

}
