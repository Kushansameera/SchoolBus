package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.RouteFees;

import java.util.List;

/**
 * Created by kusha on 10/13/2016.
 */

public class RouteFeeAdapter extends RecyclerView.Adapter<RouteFeeAdapter.LocationFeeViewHolder> {
    List<RouteFees> routeFee;
    Context context;
    LayoutInflater layoutInflater;

    public RouteFeeAdapter(Context context, List<RouteFees> fees) {
        this.routeFee = fees;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public RouteFeeAdapter.LocationFeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.route_fee_card, parent, false);
        return new LocationFeeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteFeeAdapter.LocationFeeViewHolder holder, int position) {
        RouteFees routeFees = routeFee.get(position);
        holder.locationFrom.setText(routeFees.getFrom());
        holder.locationTo.setText(routeFees.getTo());
        holder.fee.setText(routeFees.getFee());
    }

    @Override
    public int getItemCount() {
        return routeFee.size();
    }

    public RouteFees getItem(int position){
        return routeFee.get(position);
    }

    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class LocationFeeViewHolder extends RecyclerView.ViewHolder {

        TextView locationFrom;
        TextView locationTo;
        TextView fee;

        public LocationFeeViewHolder(final View itemView) {
            super(itemView);

            locationFrom = (TextView) itemView.findViewById(R.id.txtLocationFrom);
            locationTo = (TextView) itemView.findViewById(R.id.txtLocationTo);
            fee = (TextView) itemView.findViewById(R.id.txtFee);

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
