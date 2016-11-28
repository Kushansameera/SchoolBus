package com.example.kusha.schoolbus.fragments.driver;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kusha.schoolbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TempStudentHolderFragment extends Fragment {


    public TempStudentHolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_temp_student_holder, container, false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.temp_student_frame_container, new StudentRequestsFragment());
        ft.commit();

        return rootView;
    }

}
