package com.example.kusha.schoolbus.models;

/**
 * Created by kusha on 11/20/2016.
 */

public class Schools {
    private String schoolId;
    private String schoolName;
    private String schoolLatitude;
    private String schoolLongitude;

    public String getSchoolLatitude() {
        return schoolLatitude;
    }

    public void setSchoolLatitude(String schoolLatitude) {
        this.schoolLatitude = schoolLatitude;
    }

    public String getSchoolLongitude() {
        return schoolLongitude;
    }

    public void setSchoolLongitude(String schoolLongitude) {
        this.schoolLongitude = schoolLongitude;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
