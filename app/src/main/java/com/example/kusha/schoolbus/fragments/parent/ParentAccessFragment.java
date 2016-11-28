package com.example.kusha.schoolbus.fragments.parent;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.parent.ParentActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentAccessFragment extends Fragment {

    private EditText txtAccessKey;
    private Button btnGetAccess;
    private String actualAccessKey, enteredAccessKey;

    Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String driverID ;


    public ParentAccessFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentAccessFragment = inflater.inflate(R.layout.fragment_parent_access, container, false);
        txtAccessKey = (EditText) parentAccessFragment.findViewById(R.id.txtAccessKey);
        btnGetAccess = (Button) parentAccessFragment.findViewById(R.id.btnGetAccess);
        getDriverID();

        final Handler key = new Handler();
        key.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentAccessKey();
            }
        },2000);


        btnGetAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    changeFragmentChildren();
                }
            }
        });


        return parentAccessFragment;
    }

    private void checkAccessKeyExists() {
        ref.child("Drivers").child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("accessKey")) {
                    getCurrentAccessKey();
                } else {
                    btnGetAccess.setEnabled(false);
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
