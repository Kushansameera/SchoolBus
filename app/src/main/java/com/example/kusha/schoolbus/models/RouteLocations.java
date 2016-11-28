package com.example.kusha.schoolbus.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kusha on 10/12/2016.
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteLocations {
    private String locId;
    private String locName;

    public RouteLocations() {
        super();
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }
}
