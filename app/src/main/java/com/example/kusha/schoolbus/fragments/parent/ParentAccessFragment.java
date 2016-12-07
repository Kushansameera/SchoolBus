package com.example.kusha.schoolbus.fragments.parent;


import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.example.kusha.schoolbus.models.Children;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentAccessFragment extends Fragment {

    private EditText txtAccessKey;
    TextView txtCurrentChildName;
    private Button btnAddNewChild, btnSelectChild;
    private String actualAccessKey, enteredAccessKey;

    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String driverID;
    View parentAccessFragment;
    private RadioGroup childRadioGroup;
    List<Children> childrens = new ArrayList<>();


    public ParentAccessFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentAccessFragment = inflater.inflate(R.layout.fragment_parent_access, container, false);
        txtCurrentChildName = (TextView) parentAccessFragment.findViewById(R.id.txtCurrentChildName);
        txtAccessKey = new EditText(getActivity());
        btnAddNewChild = (Button) parentAccessFragment.findViewById(R.id.btnAddNewChild);
        btnSelectChild = (Button) parentAccessFragment.findViewById(R.id.btnSelectChild);
        childRadioGroup = new RadioGroup(parentAccessFragment.getContext());
        txtCurrentChildName.setText(ParentActivity.selectedChildName);
        getDriverID();

        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentAccessKey();
            }
        }, 2000);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        btnAddNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                alertDialogBuilder.setMessage("Enter Access Key");
                txtAccessKey.setInputType(InputType.TYPE_CLASS_TEXT);
                txtAccessKey.setText("");
                layout.addView(txtAccessKey);
                alertDialogBuilder.setView(layout);

                alertDialogBuilder.setPositiveButton("Get Access", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layout.removeAllViews();
                        if (validate()) {
                            changeFragmentChildren();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layout.removeAllViews();
                        dialog.cancel();
                    }
                });

                alertDialogBuilder.show();
            }
        });

        btnSelectChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("Parents").child(ParentActivity.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("children")) {
                            ref.child("Parents").child(ParentActivity.userId).child("children").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(ParentActivity.selectedDriverID)) {
                                        getChildData();
                                    } else {
                                        Toast.makeText(getActivity(), "You Haven't Registered Children Yet", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(getActivity(), "You Haven't Registered Children Yet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }
        });


        return parentAccessFragment;
    }

    private void getChildData() {
        childrens.clear();
        Query queryRef;
        queryRef = ref.child("Parents").child(ParentActivity.userId).child("children").child(ParentActivity.selectedDriverID);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrens.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Children c = data.getValue(Children.class);
                    childrens.add(c);
                }
                showChildren();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showChildren() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Select Your Child");

        for (int i = 0; i < childrens.size(); i++) {
            RadioButton childRadioButton = new RadioButton(parentAccessFragment.getContext());
            childRadioButton.setText(childrens.get(i).getStuName());
            childRadioButton.setTextSize(15);
            childRadioGroup.addView(childRadioButton);
        }

        alertDialogBuilder.setView(childRadioGroup);

        alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                int selectedRadioId = childRadioGroup.getCheckedRadioButtonId()-childrens.size();
//                ParentActivity.selectedChildId = childrens.get(selectedRadioId-1).getStuId().toString().trim();
//                ParentActivity.selectedChildName = childrens.get(selectedRadioId-1).getStuName().toString().trim();
                //Intent intent = new Intent(SelectDriverActivity.this, ParentActivity.class);
                //startActivity(intent);
                int selectedChildRadioId = childRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = (RadioButton) childRadioGroup.findViewById(selectedChildRadioId);
                String name = selectedRadioButton.getText().toString();
                for (int i = 0; i < childrens.size(); i++) {
                    if (name.equals(childrens.get(i).getStuName())) {
                        ParentActivity.selectedChildName = childrens.get(i).getStuName();
                        ParentActivity.selectedChildId = childrens.get(i).getStuId();
                        Toast.makeText(getActivity(), "id= " + ParentActivity.selectedChildId + "\n Name= " + ParentActivity.selectedChildName, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), ParentActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        alertDialogBuilder.show();
    }

    private void checkAccessKeyExists() {
        ref.child("Drivers").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("accessKey")) {
                    getCurrentAccessKey();
                } else {
                    btnAddNewChild.setEnabled(false);
                    txtAccessKey.setEnabled(false);
                    Toast.makeText(getActivity(), "Driver Haven't Created a Access Key Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getCurrentAccessKey() {

        ref.child("Drivers").child(driverID).child("accessKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actualAccessKey = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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
                    //Toast.makeText(getActivity(), driverID, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private boolean validate() {
        enteredAccessKey = txtAccessKey.getText().toString();
        if (enteredAccessKey.length() == 0) {
            Toast.makeText(getActivity(), "Please Enter a Access Key", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (enteredAccessKey.equals("0")) {
                Toast.makeText(getActivity(), "Access Key Not Valid", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (enteredAccessKey.equals(actualAccessKey)) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Access Key Not Valid", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void changeFragmentChildren() {
        try {

            Fragment fragment = new AddNewChildFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame_container_parent, fragment).commit();
        } catch (Exception e) {
            Log.d("Add New Child", e.getMessage());
        }
    }


}
