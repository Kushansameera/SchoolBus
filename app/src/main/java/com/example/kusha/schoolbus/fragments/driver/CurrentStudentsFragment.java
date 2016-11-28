package com.example.kusha.schoolbus.fragments.driver;


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
import android.widget.PopupMenu;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.adapter.StudentAdapter;
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
public class CurrentStudentsFragment extends Fragment {

    RecyclerView rcvStudents;
    StudentAdapter adapter;
    List<Student> students = new ArrayList<>();

    private Firebase ref = new Firebase("https://schoolbus-708f4.firebaseio.com/");
    public FirebaseAuth mFirebaseAuth;
    private String userId;

    public CurrentStudentsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View currentStudentfragment = inflater.inflate(R.layout.fragment_current_students, container, false);

        rcvStudents = (RecyclerView) currentStudentfragment.findViewById(R.id.rcvStudents);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userId = user.getUid().toString().trim();

        rcvStudents.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StudentAdapter(getActivity(), students);
        showStudents();
        rcvStudents.setAdapter(adapter);

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
                                ViewStudentFragment.currentStudentId = stuId;
                                ViewStudentFragment.driverId = userId;
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.current_student_frame_container, new ViewStudentFragment());
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

        return currentStudentfragment;
    }

    private void showStudents() {
        Query queryRef;
        queryRef = ref.child("Drivers").child(userId).child("permanent").orderByChild("permanentStudent");

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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



}
