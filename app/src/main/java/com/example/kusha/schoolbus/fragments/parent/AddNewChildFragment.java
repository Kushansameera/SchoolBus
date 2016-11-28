package com.example.kusha.schoolbus.fragments.parent;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.adapter.CustomAdapter;
import com.example.kusha.schoolbus.application.ApplicationClass;
import com.example.kusha.schoolbus.models.Schools;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewChildFragment extends Fragment {

    private View addNewStudentFragment;
    private ImageButton btnStuPickLocation, btnStuDropLocation, btnStuPickTime;
    private ImageView stuImage;
    private TextView txtPickLocation, txtDropLocation;
    public static TextView txtPickTime;
    private Button btnSubmit;
    private EditText txtStuName;
    private Spinner spinnerStuGrade, spinnerStuClass, spinnerStuFrom, spinnerStuSchool;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private String stuId, stuName, stuSchool, stuGrade, stuClass, stuGender, stuPickupTime;
    public static double stuPickLatitude, stuPickLongitude, stuDropLatitude, stuDropLongitude;
    public static String stuPickLoc, stuDropLoc, stuPickTime;
    public static int selectedSchool = 0, selectedLocation = 0;
    List<String> pickupLoc = new ArrayList<>();
    List<String> locSchools = new ArrayList<>();

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private String userId,userEmail;
    boolean flag = false;
    private String driverID;
    private String latestTempStudentID;
    boolean set = true;


    public AddNewChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addNewStudentFragment = inflater.inflate(R.layout.fragment_add_new_child, container, false);
//        ApplicationClass.bus.register(this);
        progressDialog = new ProgressDialog(getActivity());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        userEmail = user.getEmail();

        stuImage = (ImageView) addNewStudentFragment.findViewById(R.id.imageButtonStu);
        //txtStuID = (TextView) addNewStudentFragment.findViewById(R.id.txtStuID);
        txtStuName = (EditText) addNewStudentFragment.findViewById(R.id.txtStudentName);
        genderRadioGroup = (RadioGroup) addNewStudentFragment.findViewById(R.id.radioGroupGender);
        spinnerStuSchool = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerStuSchool);
        spinnerStuGrade = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerGrade);
        spinnerStuClass = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerClass);
        //spinnerStuFrom = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerFrom);
        txtPickLocation = (TextView) addNewStudentFragment.findViewById(R.id.txtLocationPick);
        txtDropLocation = (TextView) addNewStudentFragment.findViewById(R.id.txtLocationDrop);
        txtPickTime = (TextView) addNewStudentFragment.findViewById(R.id.txtPickuptime);
        btnStuPickLocation = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuPickLocation);
        btnStuDropLocation = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuDropLocation);
        btnStuPickTime = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuPickupTime);
        btnSubmit = (Button) addNewStudentFragment.findViewById(R.id.btnSubmit);


        txtPickLocation.setText(stuPickLoc);
        txtDropLocation.setText(stuDropLoc);



        getDriverID();
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTempStuId();
            }
        },2000);


        stuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(intent, 1);
            }
        });

        btnStuPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame_container_parent, new ChildLocationFragment());
                ChildLocationFragment.buttonIdentifier = "pick";
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnStuDropLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame_container_parent, new ChildLocationFragment());
                ChildLocationFragment.buttonIdentifier = "drop";
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnStuPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFields()) {
                    if(flag){
                        if(latestTempStudentID.equals("1")){
                            ref.child("Drivers").child(driverID).child("temp").child("tempStudentCounter").setValue(1);
                            addTempStudent();
                            flag = false;
                        }
                        else {
                            addTempStudent();
                            flag = false;
                        }

                    }
                    final Handler key = new Handler();
                    key.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeFragment();
                        }
                    }, 2000);
                }

            }
        });
        return addNewStudentFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            stuImage.setImageURI(imageUri);
        }
    }

    private void getDriverID() {
        Query queryRef;
        String driverEmail = ParentActivity.selectedDriverEmail;
        queryRef = ref.child("Drivers").orderByChild("email").equalTo(driverEmail);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    driverID = data.getKey();
                    setSpinnerValues();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void setSpinnerValues() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(driverID).child("schools").orderByChild("routeSchools");
        pickupLoc.clear();
        locSchools.clear();
        locSchools.add("Select School");

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.getChildren()) {
                        Schools s = d.getValue(Schools.class);
                        locSchools.add(s.getSchoolName());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        CustomAdapter locTo = new CustomAdapter(getActivity(), android.R.layout.simple_spinner_item, locSchools, 0);
        locTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStuSchool.setAdapter(locTo);
    }

    private boolean checkFields() {

        if (txtStuName.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Enter Student Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPickLocation.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please Select Pick Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtDropLocation.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please Select Drop Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtPickTime.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please Select Pickup Time", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spinnerStuSchool.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Select Student School", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            stuName = txtStuName.getText().toString();
            int selectedRadioId = genderRadioGroup.getCheckedRadioButtonId();
            genderRadioButton = (RadioButton) addNewStudentFragment.findViewById(selectedRadioId);
            stuGender = genderRadioButton.getText().toString().trim();
            stuGrade = spinnerStuGrade.getSelectedItem().toString();
            stuClass = spinnerStuClass.getSelectedItem().toString();
            stuPickTime = txtPickTime.getText().toString();
            stuSchool = spinnerStuSchool.getSelectedItem().toString();

            return true;
        }
    }

    private void getLatestTempStuID() {
        ref.child("Drivers").child(driverID).child("temp").child("tempStudentCounter").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class) + 1;
                latestTempStudentID = currentValue.toString();
                mutableData.setValue(currentValue);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                flag = true;
            }
        });
    }

    @Subscribe
    private void checkTempStuId() {
        ref.child("Drivers").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("temp")) {
                    getLatestTempStuID();
                } else {
                    latestTempStudentID = "1";
                    flag = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addTempStudent() {

        Student student = new Student();

        student.setStuID(latestTempStudentID);
        student.setStuName(stuName);
        student.setStuSchool(stuSchool);
        student.setStuGrade(stuGrade);
        student.setStuClass(stuClass);
        student.setStuPickLatitude(Double.toString(stuPickLatitude));
        student.setStuPickLongitude(Double.toString(stuPickLongitude));
        student.setStuDropLatitude(Double.toString(stuDropLatitude));
        student.setStuDropLongitude(Double.toString(stuDropLongitude));
        student.setStuPickLocation(stuPickLoc);
        student.setStuDropLocation(stuDropLoc);
        student.setStuPickTime(stuPickTime);
        student.setParentEmail(userEmail);
        student.setStuGender(stuGender);


        ref.child("Drivers").child(driverID).child("temp").child("tempStudent").child(latestTempStudentID).setValue(student, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    txtStuName.setText("");
                    stuPickLatitude = 0;
                    stuPickLatitude = 0;
                    stuDropLatitude = 0;
                    stuDropLongitude = 0;
                    stuPickLoc = "";
                    stuDropLoc = "";
                    stuPickTime = "";
                    txtPickLocation.setText("");
                    txtDropLocation.setText("");
                    txtPickTime.setText("");
                    spinnerStuSchool.setSelection(0);
                    spinnerStuGrade.setSelection(0);
                    spinnerStuClass.setSelection(0);

                    Toast.makeText(getActivity(), "Add Student Complete", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Cannot Add Student", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    private void changeFragment() {
        ref.child("Drivers").child(driverID).child("accessKey").setValue("0", new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                try {
                    Fragment fragment = new ParentAccessFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
                } catch (Exception e) {
                    Log.d("Access Key", e.getMessage());
                }
            }
        });
    }



}
