package com.example.kusha.schoolbus.fragments.parent;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.squareup.otto.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewChildFragment extends Fragment {

    private View addNewStudentFragment;
    private ImageButton btnStuPickLocation, btnStuDropLocation, btnStuPickTime;
    private ImageView stuImageView;
    private TextView txtPickLocation, txtDropLocation;
    public static TextView txtPickTime;
    private Button btnSubmit,btnBrowse;
    private EditText txtStuName;
    private Spinner spinnerStuGrade, spinnerStuClass, spinnerStuFrom, spinnerStuSchool;
    private RadioGroup genderRadioGroup, stuTypeRadioGroup;
    private RadioButton genderRadioButton;
    private String stuImage, stuName, stuSchool, stuGrade, stuClass, stuGender;
    public static double stuPickLatitude, stuPickLongitude, stuDropLatitude, stuDropLongitude;
    public static String stuPickLoc, stuDropLoc, stuPickTime;
    public static int selectedSchool = 0, selectedLocation = 0;
    List<String> pickupLoc = new ArrayList<>();
    List<String> locSchools = new ArrayList<>();
    Student student = new Student();
    String studentType = "Both";

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private String userId, userEmail;
    boolean flag = false;
    private String driverID;
    private String latestTempStudentID;
    boolean set = true;
    private int PICK_IMAGE_REQUEST = 1;
    public static Bitmap stuBitmap;
    public static String stuImageEncoded;

    public AddNewChildFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addNewStudentFragment = inflater.inflate(R.layout.fragment_add_new_child, container, false);
        progressDialog = new ProgressDialog(getActivity());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();
        userEmail = user.getEmail();

        stuImageView = (ImageView) addNewStudentFragment.findViewById(R.id.imageButtonStu);
        txtStuName = (EditText) addNewStudentFragment.findViewById(R.id.txtStudentName);
        genderRadioGroup = (RadioGroup) addNewStudentFragment.findViewById(R.id.radioGroupGender);
        stuTypeRadioGroup = (RadioGroup) addNewStudentFragment.findViewById(R.id.radioGroupStudentTpe);
        spinnerStuSchool = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerStuSchool);
        spinnerStuGrade = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerGrade);
        spinnerStuClass = (Spinner) addNewStudentFragment.findViewById(R.id.spinnerClass);
        txtPickLocation = (TextView) addNewStudentFragment.findViewById(R.id.txtLocationPick);
        txtDropLocation = (TextView) addNewStudentFragment.findViewById(R.id.txtLocationDrop);
        txtPickTime = (TextView) addNewStudentFragment.findViewById(R.id.txtPickuptime);
        btnStuPickLocation = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuPickLocation);
        btnStuDropLocation = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuDropLocation);
        btnStuPickTime = (ImageButton) addNewStudentFragment.findViewById(R.id.imageButtonStuPickupTime);
        btnSubmit = (Button) addNewStudentFragment.findViewById(R.id.btnSubmit);
        btnBrowse = (Button) addNewStudentFragment.findViewById(R.id.btnBrowse);

        txtPickLocation.setText(stuPickLoc);
        txtDropLocation.setText(stuDropLoc);
        stuImageView.setImageBitmap(stuBitmap);

        //getDriverID();
        setSpinnerValues();
        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTempStuId();
            }
        }, 2000);


        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnStuPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame_container_parent, new ChildLocationFragment());
                ChildLocationFragment.buttonIdentifier = "pick";
                ChildLocationFragment.mStuBitmap = stuBitmap;
                ChildLocationFragment.mStuImageEncoded = stuImageEncoded;
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
                ChildLocationFragment.mStuBitmap = stuBitmap;
                ChildLocationFragment.mStuImageEncoded = stuImageEncoded;
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
                    if (flag) {
                        if (latestTempStudentID.equals("1")) {
                            ref.child("Drivers").child(driverID).child("temp").child("tempStudentCounter").setValue(1);
                            addTempStudent();
                            sendPush(ParentActivity.selectedDriverEmail);
                            flag = false;
                        } else {
                            addTempStudent();
                            sendPush(ParentActivity.selectedDriverEmail);
                            flag = false;
                        }

                    }
//                    final Handler key = new Handler();
//                    key.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            changeFragment();
//                        }
//                    }, 2000);
                }

            }
        });

        stuTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton) group.findViewById(checkedId);
                studentType = checkedButton.getText().toString();
                if (studentType.equals("Morning Only")) {
                    btnStuDropLocation.setEnabled(false);
                    btnStuPickLocation.setEnabled(true);
                    txtDropLocation.setText("");

                } else if (studentType.equals("Evening Only")) {
                    btnStuDropLocation.setEnabled(true);
                    btnStuPickLocation.setEnabled(false);
                    txtPickLocation.setText("");
                } else if (studentType.equals("Both")) {
                    btnStuDropLocation.setEnabled(true);
                    btnStuPickLocation.setEnabled(true);
                }
            }
        });
        return addNewStudentFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                stuBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                stuImageView.setImageBitmap(stuBitmap);
                encodeBitmap(stuBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        stuImageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
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
        queryRef = ref.child("Drivers").child(ParentActivity.selectedDriverID).child("schools").orderByChild("routeSchools");
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
        } else if (txtPickLocation.getText().toString().trim().length() == 0 && !studentType.equals("Evening Only")) {
            Toast.makeText(getActivity(), "Please Select Pick Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtDropLocation.getText().toString().trim().length() == 0 && !studentType.equals("Morning Only")) {
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
        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("temp").child("tempStudentCounter").runTransaction(new Transaction.Handler() {
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
        ref.child("Drivers").child(ParentActivity.selectedDriverID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        progressDialog.setMessage("Submitting...");
        progressDialog.show();
        student.setStuID(latestTempStudentID);
        student.setStuName(stuName);
        student.setStuSchool(stuSchool);
        student.setStuGrade(stuGrade);
        student.setStuClass(stuClass);
        student.setStuImage(stuImageEncoded);
        if (!studentType.equals("Evening Only")) {
            student.setStuPickLatitude(Double.toString(stuPickLatitude));
            student.setStuPickLongitude(Double.toString(stuPickLongitude));
            student.setStuPickLocation(stuPickLoc);
        }
        if (!studentType.equals("Morning Only")) {
            student.setStuDropLatitude(Double.toString(stuDropLatitude));
            student.setStuDropLongitude(Double.toString(stuDropLongitude));
            student.setStuDropLocation(stuDropLoc);
        }
        student.setStuPickTime(stuPickTime);
        student.setParentEmail(userEmail);
        student.setStuGender(stuGender);
        student.setParentID(userId);
        student.setStuType(studentType);

        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("temp").child("tempStudent").child(latestTempStudentID).setValue(student, new Firebase.CompletionListener() {
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
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Add Student Complete", Toast.LENGTH_SHORT).show();
                    changeFragment();
                } else {
                    Toast.makeText(getActivity(), "Cannot Add Student", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    private void changeFragment() {
        ref.child("Drivers").child(ParentActivity.selectedDriverID).child("accessKey").setValue("0", new Firebase.CompletionListener() {
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

    private void sendPush(String to) {
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("email", to);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("You Got New Student Request From " + ParentActivity.parentName);
        push.sendInBackground();
        Log.d("PUSH MESSAGE", "SENT " + to);
    }

}
