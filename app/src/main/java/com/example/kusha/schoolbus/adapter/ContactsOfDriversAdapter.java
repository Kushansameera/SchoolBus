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
 * Created by kusha on 1/1/2017.
 */

public class ContactsOfDriversAdapter extends RecyclerView.Adapter<ContactsOfDriversAdapter.ContactsOfDriversViewHolder> {
    List<ManageParents> manageParents;
    Context context;
    LayoutInflater layoutInflater;

    public ContactsOfDriversAdapter(Context context, List<ManageParents> parents) {
        this.manageParents = parents;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsOfDriversAdapter.ContactsOfDriversViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.contacts_card, parent, false);
        return new ContactsOfDriversAdapter.ContactsOfDriversViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactsOfDriversAdapter.ContactsOfDriversViewHolder holder, int position) {
        ManageParents manageParent = manageParents.get(position);
        holder.parentName.setText(manageParent.getParentName());
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

    public class ContactsOfDriversViewHolder extends RecyclerView.ViewHolder {

        TextView parentName;

        public ContactsOfDriversViewHolder(final View itemView) {
            super(itemView);

            parentName = (TextView) itemView.findViewById(R.id.txtContactName);
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
