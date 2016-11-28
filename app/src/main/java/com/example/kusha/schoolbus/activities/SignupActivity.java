package com.example.kusha.schoolbus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.User;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    private FirebaseAuth mfirebaseAuth;

    private Button btnSignup;
    private EditText txtName, txtAddress, txtPhoneNumber, txtEmail, txtPassword, txtRetypePassword;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    String name, address, phoneNumber, email, password, retypePassword, type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mfirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnSignup = (Button) findViewById(R.id.btnDone);
        txtName = (EditText) findViewById(R.id.txtName);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhone);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtRetypePassword = (EditText) findViewById(R.id.txtRetypePassword);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    registerUser();
                }
            }
        });

    }

    private boolean checkFields() {

        if (txtName.getText().toString().trim().length() == 0) {
            Toast.makeText(SignupActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtAddress.getText().toString().trim().length() == 0) {
            Toast.makeText(SignupActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPhoneNumber.getText().toString().trim().length() == 0) {
            Toast.makeText(SignupActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtEmail.getText().toString().trim().length() == 0) {
            Toast.makeText(SignupActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPassword.getText().toString().length() == 0) {
            Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPassword.getText().toString().length() < 6) {
            Toast.makeText(SignupActivity.this, "Use at least 6 characters for Password", Toast.LENGTH_SHORT).show();
            return false;
        }else if (txtPhoneNumber.getText().toString().length() != 10) {
            Toast.makeText(SignupActivity.this, "Phone Number Not Valid", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtRetypePassword.getText().toString().length() == 0) {
            Toast.makeText(SignupActivity.this, "Re-Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {


            if (validateEmail(txtEmail.getText().toString().trim())) {
                if ((txtPassword.getText().toString()).equals(txtRetypePassword.getText().toString())) {

                    name = txtName.getText().toString().trim();
                    address = txtAddress.getText().toString().trim();
                    phoneNumber = txtPhoneNumber.getText().toString().trim();
                    email = txtEmail.getText().toString().trim();
                    password = txtPassword.getText().toString().trim();
                    retypePassword = txtRetypePassword.getText().toString().trim();

                    int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedRadioId);
                    type = radioButton.getText().toString().trim();
                    return true;
                } else {
                    Toast.makeText(SignupActivity.this, "Enter Same Password", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(SignupActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


    }

    public boolean validateEmail(String passedEmail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(passedEmail);
        return matcher.find();
    }


    private void registerUser() {
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        mfirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
                            String UserId = task.getResult().getUser().getUid();
                            User user = new User();
                            user.setName(name);
                            user.setAddress(address);
                            user.setPhoneNumber(phoneNumber);
                            user.setEmail(email);
                            user.setType(type);

                            ref.child("Users").child(UserId).setValue(user);
                            if (type.equals("Driver")) {
                                ref.child("Drivers").child(UserId).child("email").setValue(email);
                                ref.child("Drivers").child(UserId).child("accessKey").setValue("0");

                            } else {
                                ref.child("Parents").child(UserId).child("email").setValue(email);

                            }

                            Toast.makeText(SignupActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignupActivity.this, "Could not register..Please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
