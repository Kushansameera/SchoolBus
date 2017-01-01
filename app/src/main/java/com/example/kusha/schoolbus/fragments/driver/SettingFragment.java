package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.LoginActivity;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    public FirebaseAuth mFirebaseAuth;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    TextView txtCurrentAccessKey;
    EditText txtNewAccessKey,txtNewPw,txtRetypePw;
    Button btnCreateNewKey,btnRemoveCurrentKey,btnChangePw;

    public SettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingFragment = inflater.inflate(R.layout.fragment_setting, container, false);
        txtCurrentAccessKey = (TextView)settingFragment.findViewById(R.id.txtCurrentAccessKey);
        txtNewAccessKey = (EditText)settingFragment.findViewById(R.id.txtNewAccessKey);
        txtNewPw = (EditText)settingFragment.findViewById(R.id.txtNewPw);
        txtRetypePw = (EditText)settingFragment.findViewById(R.id.txtRetypePw);
        btnCreateNewKey = (Button)settingFragment.findViewById(R.id.btnCreateNewKey);
        btnRemoveCurrentKey = (Button)settingFragment.findViewById(R.id.btnRemoveCurrentKey);
        btnChangePw = (Button)settingFragment.findViewById(R.id.btnChangePw);
        mFirebaseAuth = FirebaseAuth.getInstance();
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

        btnChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPw = txtNewPw.getText().toString();
                String RetypePw = txtRetypePw.getText().toString();

                if(newPw.length()==0){
                    Toast.makeText(getActivity(), "Please Enter New Password", Toast.LENGTH_SHORT).show();
                }
                if(newPw.length()<6){
                    Toast.makeText(getActivity(), "Use at least 6 characters for Password ", Toast.LENGTH_SHORT).show();
                }
                if(RetypePw.length()==0){
                    Toast.makeText(getActivity(), "Please Re-Enter New Password", Toast.LENGTH_SHORT).show();
                }
                if(newPw.equals(RetypePw)){
                    LoginActivity.user.updatePassword(txtNewPw.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    txtNewPw.setText("");
                                    txtRetypePw.setText("");
                                    Toast.makeText(getActivity(), "Password Changed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(getActivity(), "Enter Same Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return settingFragment;
    }

    public void setCurrentAccessKey(){
        ref.child("Drivers").child(DriverActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        ref.child("Drivers").child(DriverActivity.userId).child("accessKey").addValueEventListener(new ValueEventListener() {
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
                ref.child("Drivers").child(DriverActivity.userId).child("accessKey").setValue(txtNewAccessKey.getText().toString());
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
            ref.child("Drivers").child(DriverActivity.userId).child("accessKey").setValue("0");
            btnRemoveCurrentKey.setEnabled(false);
        }

    }

}
