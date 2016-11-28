package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.RouteLocations;
import com.example.kusha.schoolbus.models.Student;

import java.util.List;

/**
 * Created by kusha on 11/26/2016.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    List<Student> students;
    Context context;
    LayoutInflater layoutInflater;

    public StudentAdapter(Context context, List<Student> student) {
        this.students = student;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.student_card, parent, false);
        return new StudentAdapter.StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StudentAdapter.StudentViewHolder holder, int position) {
        Student myStudents = students.get(position);
        holder.studentName.setText(myStudents.getStuName());
        holder.studentSchool.setText(myStudents.getStuSchool());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public Student getItem(int position){
        return students.get(position);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView studentName;
        TextView studentSchool;

        public StudentViewHolder(final View itemView) {
            super(itemView);

            studentName = (TextView) itemView.findViewById(R.id.txt_temp_stu_name);
            studentSchool = (TextView) itemView.findViewById(R.id.txt_temp_stu_school);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });

        }

    }
}
