package com.example.kusha.schoolbus.fragments.driver;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.activities.driver.RequestSortActivity;
import com.example.kusha.schoolbus.adapter.StudentAdapter;
import com.example.kusha.schoolbus.fragments.parent.ChildLocationFragment;
import com.example.kusha.schoolbus.models.Student;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentRequestsFragment extends Fragment {

    RecyclerView rcvTempStudents;
    StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String userId;
    //private Button btnFindBest;
    private ProgressDialog progressDialog;

    public StudentRequestsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View studentRequestFragment = inflater.inflate(R.layout.fragment_student_requests, container, false);
        rcvTempStudents = (RecyclerView) studentRequestFragment.findViewById(R.id.rcvTempStudents);
        //btnFindBest = (Button) studentRequestFragment.findViewById(R.id.btnfindBest);
        progressDialog = new ProgressDialog(getActivity());
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvTempStudents.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StudentAdapter(getActivity(), students);
        showTempStudents();
        rcvTempStudents.setAdapter(adapter);

//        btnFindBest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RequestSortActivity.myStudents = students;
//                Intent intent = new Intent(getActivity(), RequestSortActivity.class);
//                startActivity(intent);
//            }
//        });

        adapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String stuId = students.get(position).getStuID();
                final PopupMenu popup = new PopupMenu(getActivity(), itemView);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_item:
                                TempStudentFragment.tempStudentId = stuId;
                                TempStudentFragment.driverId = userId;
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.temp_student_frame_container, new TempStudentFragment());
                                ft.commit();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.view_source, popup.getMenu());
                popup.show();
            }
        });

        return studentRequestFragment;
    }

    private void showTempStudents() {
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("temp").orderByChild("tempStudent");

        students.clear();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : data.getChildren()) {
                        Student student = dataSnapshot1.getValue(Student.class);
                        students.add(student);
                    }
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
