package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.RouteLocations;
import com.example.kusha.schoolbus.models.Schools;

import java.util.List;

/**
 * Created by kusha on 11/20/2016.
 */

public class SchoolsAdapter extends RecyclerView.Adapter<SchoolsAdapter.SchoolsViewHolder> {

    List<Schools> routeSchools;
    Context context;
    LayoutInflater layoutInflater;

    public SchoolsAdapter(Context context, List<Schools> schools) {
        this.routeSchools = schools;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public SchoolsAdapter.SchoolsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.school_card, parent, false);
        return new SchoolsAdapter.SchoolsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SchoolsAdapter.SchoolsViewHolder holder, int position) {
        Schools school = routeSchools.get(position);
        //holder.schoolId.setText(school.getSchoolId());
        holder.schoolName.setText(school.getSchoolName());
    }

    @Override
    public int getItemCount() {
        return routeSchools.size();
    }

    public Schools getItem(int position){
        return routeSchools.get(position);
    }

    private SchoolsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(SchoolsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public class SchoolsViewHolder extends RecyclerView.ViewHolder {

        //TextView schoolId;
        TextView schoolName;

        public SchoolsViewHolder(final View itemView) {
            super(itemView);

            //schoolId = (TextView) itemView.findViewById(R.id.txtSchoolID);
            schoolName = (TextView) itemView.findViewById(R.id.txtSchoolName);

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
