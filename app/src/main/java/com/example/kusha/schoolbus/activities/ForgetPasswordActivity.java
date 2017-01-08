package com.example.kusha.schoolbus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText txtEmail;
    private Button btnSendEmail;
    private ImageButton btnBack;
    private ProgressDialog progressDialog;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setTitle("Forget Password");
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        btnSendEmail = (Button)findViewById(R.id.btnSend);
        btnBack = (ImageButton)findViewById(R.id.btnBack);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending..");
        progressDialog.setCancelable(false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    public boolean validateEmail(String passedEmail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(passedEmail);
        return matcher.find();
    }

    private void sendMail() {
        String emailAddress = txtEmail.getText().toString();
        if (validateEmail(emailAddress)) {
            progressDialog.show();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        txtEmail.setText("");
                        Toast.makeText(ForgetPasswordActivity.this,"Email Sent", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ForgetPasswordActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }else{
            Toast.makeText(ForgetPasswordActivity.this,"Enter Valid Email", Toast.LENGTH_SHORT).show();
        }
    }
}
