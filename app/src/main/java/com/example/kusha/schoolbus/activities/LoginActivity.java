package com.example.kusha.schoolbus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.DriverActivity;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.activities.parent.SelectDriverActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseInstallation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private ProgressDialog mProgressDialog;
    public FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");

    private Button btnSignup, btnLogin;
    EditText txtEmail, txtPassword;
    String email, password;
    TextView txtLoginForgetPassword;
    public static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressDialog = new ProgressDialog(this);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressDialog.setMessage("Login...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String userId = user.getUid().toString().trim();
                    String userEmail = user.getEmail().toString().trim();
                    ParseInstallation p = ParseInstallation.getCurrentInstallation();
                    p.put("email", userEmail);
                    p.saveInBackground();
                    selectUserType(userId);
                } else {
                    mProgressDialog.dismiss();
                    // User is signed out

                }
                // ...
            }
        };



        btnSignup = (Button) findViewById(R.id.btnDone);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtLoginForgetPassword = (TextView)findViewById(R.id.txtLoginForgetPassword);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    mProgressDialog.setMessage("Login...");
                    mProgressDialog.show();
                    ParseInstallation p = ParseInstallation.getCurrentInstallation();
                    p.put("email", email);
                    p.saveInBackground();
                    login(email, password);
                }

            }
        });

//        txtLoginForgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                String emailAddress = "kushansamee92@gmail.com";
//
//                auth.sendPasswordResetEmail(emailAddress)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private boolean checkFields() {
        if (txtEmail.getText().toString().trim().length() == 0) {
            Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPassword.getText().toString().trim().length() == 0) {
            Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (validateEmail(txtEmail.getText().toString().trim())) {
                email = txtEmail.getText().toString().trim();
                password = txtPassword.getText().toString().trim();
                return true;
            } else {
                Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public boolean validateEmail(String passedEmail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(passedEmail);
        return matcher.find();
    }

    public void login(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid().toString().trim();
                                selectUserType(userId);
                                //mProgressDialog.dismiss();
                            } else {
                                // User is signed out
                                Toast.makeText(LoginActivity.this, "Please Login / Register", Toast.LENGTH_SHORT).show();
                            }
                        }

                        //mProgressDialog.hide();
                    }
                });
    }


    public void selectUserType(final String userId) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("Users").child(userId).child("type").getValue().toString();
                if (userType.equals("Driver")) {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                    startActivity(intent);
                } else {
                    ref.child("Parents").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("driver")){
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, SelectDriverActivity.class);
                                startActivity(intent);
                            }
                            else {
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
