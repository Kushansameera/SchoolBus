package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.ManageDrivers;

import java.util.List;

/**
 * Created by kusha on 12/30/2016.
 */

public class ContactsOfParentsAdapter extends RecyclerView.Adapter<ContactsOfParentsAdapter.ContactsOfParentsViewHolder> {
    List<ManageDrivers> manageDrivers;
    Context context;
    LayoutInflater layoutInflater;

    public ContactsOfParentsAdapter(Context context, List<ManageDrivers> drivers) {
        this.manageDrivers = drivers;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsOfParentsAdapter.ContactsOfParentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.contacts_card, parent, false);
        return new ContactsOfParentsAdapter.ContactsOfParentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactsOfParentsAdapter.ContactsOfParentsViewHolder holder, int position) {
        ManageDrivers manageDriver = manageDrivers.get(position);
        holder.driverName.setText(manageDriver.getDriverName());
    }

    @Override
    public int getItemCount() {
        return manageDrivers.size();
    }

    public ManageDrivers getItem(int position){
        return manageDrivers.get(position);
    }
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ContactsOfParentsViewHolder extends RecyclerView.ViewHolder {

        TextView driverName;

        public ContactsOfParentsViewHolder(final View itemView) {
            super(itemView);

            driverName = (TextView) itemView.findViewById(R.id.txtContactName);
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
