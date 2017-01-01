package com.example.kusha.schoolbus.fragments.parent;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsParentFragment extends Fragment {
    EditText txtNewPw,txtRetypePw;
    Button btnChangePw;

    public SettingsParentFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_parent, container, false);
        txtNewPw = (EditText)rootView.findViewById(R.id.txtNewPw);
        txtRetypePw = (EditText)rootView.findViewById(R.id.txtRetypePw);
        btnChangePw = (Button)rootView.findViewById(R.id.btnChangePw);
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
        return rootView;
    }

}
