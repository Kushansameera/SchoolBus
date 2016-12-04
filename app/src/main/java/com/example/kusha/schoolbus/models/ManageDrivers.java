package com.example.kusha.schoolbus.models;


/**
 * Created by kusha on 11/7/2016.
 */
public class ManageDrivers {
    private String driverName;
    private String driverEmail;
    private String driverId;


    public ManageDrivers() {
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }
}
