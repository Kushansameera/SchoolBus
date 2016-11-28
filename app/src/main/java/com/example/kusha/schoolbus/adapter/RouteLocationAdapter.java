package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.RouteLocations;

import java.util.List;

/**
 * Created by kusha on 10/23/2016.
 */

public class RouteLocationAdapter extends RecyclerView.Adapter<RouteLocationAdapter.LocationViewHolder> {
    List<RouteLocations> routeLocations;
    Context context;
    LayoutInflater layoutInflater;

    public RouteLocationAdapter(Context context, List<RouteLocations> locations) {
        this.routeLocations = locations;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.route_location_card, parent, false);
        return new LocationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        RouteLocations routeLocation = routeLocations.get(position);
        //holder.locationId.setText(routeLocation.getLocId());
        holder.locationName.setText(routeLocation.getLocName());
    }

    @Override
    public int getItemCount() {
        return routeLocations.size();
    }

    public RouteLocations getItem(int position){
        return routeLocations.get(position);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView locationId;
        TextView locationName;

        public LocationViewHolder(final View itemView) {
            super(itemView);

            //locationId = (TextView) itemView.findViewById(R.id.txtLocationID);
            locationName = (TextView) itemView.findViewById(R.id.txtLocationName);

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
