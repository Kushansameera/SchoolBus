package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.ManageDrivers;
import com.example.kusha.schoolbus.models.User;

import java.util.List;

/**
 * Created by kusha on 11/7/2016.
 */

public class ManageDriversAdapter extends RecyclerView.Adapter<ManageDriversAdapter.ManageDriversViewHolder>{
    List<User> manageDrivers;
    Context context;
    LayoutInflater layoutInflater;

    public ManageDriversAdapter(Context context, List<User> drivers) {
        this.manageDrivers = drivers;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ManageDriversAdapter.ManageDriversViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.manage_driver_card, parent, false);
        return new ManageDriversAdapter.ManageDriversViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ManageDriversAdapter.ManageDriversViewHolder holder, int position) {
        User manageDriver = manageDrivers.get(position);
        holder.driverName.setText(manageDriver.getName());
        holder.driverEmail.setText(manageDriver.getEmail());
    }

    @Override
    public int getItemCount() {
        return manageDrivers.size();
    }

    public User getItem(int position){
        return manageDrivers.get(position);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ManageDriversViewHolder extends RecyclerView.ViewHolder {

        TextView driverEmail;
        TextView driverName;

        public ManageDriversViewHolder(final View itemView) {
            super(itemView);

            driverEmail = (TextView) itemView.findViewById(R.id.txtDriverEmail);
            driverName = (TextView) itemView.findViewById(R.id.txtDriverName);

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
