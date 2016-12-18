package com.example.kusha.schoolbus.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kusha on 12/17/2016.
 */

public class CustomLatLng {
    private LatLng latLng;
    private String type;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
