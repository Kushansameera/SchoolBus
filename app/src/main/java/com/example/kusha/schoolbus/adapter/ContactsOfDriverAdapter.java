package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.ManageDrivers;
import com.example.kusha.schoolbus.models.ManageParents;

import java.util.List;

/**
 * Created by kusha on 12/30/2016.
 */

public class ContactsOfDriverAdapter extends RecyclerView.Adapter<ContactsOfDriverAdapter.ContactsOfDriverViewHolder>  {

    List<ManageParents> manageParents;
    Context context;
    LayoutInflater layoutInflater;

    public ContactsOfDriverAdapter(Context context, List<ManageParents> parents) {
        this.manageParents = parents;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsOfDriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.contacts_card, parent, false);
        return new ContactsOfDriverAdapter.ContactsOfDriverViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactsOfDriverViewHolder holder, int position) {
        ManageParents manageParent = manageParents.get(position);
        holder.ParentName.setText(manageParent.getParentName());
    }

    @Override
    public int getItemCount() {
        return manageParents.size();
    }

    public ManageParents getItem(int position){
        return manageParents.get(position);
    }
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ContactsOfDriverViewHolder extends RecyclerView.ViewHolder {

        TextView ParentName;

        public ContactsOfDriverViewHolder(final View itemView) {
            super(itemView);

            ParentName = (TextView) itemView.findViewById(R.id.txtContactName);
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
