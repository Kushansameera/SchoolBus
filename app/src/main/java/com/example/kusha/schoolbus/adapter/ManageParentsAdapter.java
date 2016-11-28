package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.ManageParents;

import java.util.List;

/**
 * Created by kusha on 11/4/2016.
 */

public class ManageParentsAdapter extends RecyclerView.Adapter<ManageParentsAdapter.ManageParentsViewHolder> {

    List<ManageParents> manageParents;
    Context context;
    LayoutInflater layoutInflater;

    public ManageParentsAdapter(Context context, List<ManageParents> parents) {
        this.manageParents = parents;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ManageParentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.manage_parent_card, parent, false);
        return new ManageParentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ManageParentsViewHolder holder, int position) {
        ManageParents manageParent = manageParents.get(position);
        holder.parentEmail.setText(manageParent.getParentEmail());
        holder.parentName.setText(manageParent.getParentName());
    }

    @Override
    public int getItemCount() {
        return manageParents.size();
    }

    public ManageParents getItem(int position){
        return manageParents.get(position);
    }


    public class ManageParentsViewHolder extends RecyclerView.ViewHolder {

        TextView parentEmail;
        TextView parentName;

        public ManageParentsViewHolder(View itemView) {
            super(itemView);

            parentEmail = (TextView) itemView.findViewById(R.id.txtParentEmail);
            parentName = (TextView) itemView.findViewById(R.id.txtParentName);

        }

    }
}
