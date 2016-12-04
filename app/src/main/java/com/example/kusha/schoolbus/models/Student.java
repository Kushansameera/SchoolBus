package com.example.kusha.schoolbus.models;

/**
 * Created by kusha on 11/15/2016.
 */

public class Student {
    private String stuID;
    private String stuName;
    private String stuSchool;
    private String stuGender;
    private String stuGrade;
    private String stuClass;
    private String stuPickLatitude;
    private String stuPickLongitude;
    private String stuDropLatitude;
    private String stuDropLongitude;
    private String stuPickLocation;
    private String stuDropLocation;
    private String stuPickTime;
    private String stuFrom;
    private String stuPayment;
    private String parentEmail;
    private String stuMonthlyFee;
    private String parentID;

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getStuGender() {
        return stuGender;
    }

    public void setStuGender(String stuGender) {
        this.stuGender = stuGender;
    }

    public String getStuMonthlyFee() {
        return stuMonthlyFee;
    }

    public void setStuMonthlyFee(String stuMonthlyFee) {
        this.stuMonthlyFee = stuMonthlyFee;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getStuID() {
        return stuID;
    }

    public void setStuID(String stuID) {
        this.stuID = stuID;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuSchool() {
        return stuSchool;
    }

    public void setStuSchool(String stuSchool) {
        this.stuSchool = stuSchool;
    }

    public String getStuGrade() {
        return stuGrade;
    }

    public void setStuGrade(String stuGrade) {
        this.stuGrade = stuGrade;
    }

    public String getStuClass() {
        return stuClass;
    }

    public void setStuClass(String stuClass) {
        this.stuClass = stuClass;
    }

    public String getStuPickLatitude() {
        return stuPickLatitude;
    }

    public void setStuPickLatitude(String stuPickLatitude) {
        this.stuPickLatitude = stuPickLatitude;
    }

    public String getStuPickLongitude() {
        return stuPickLongitude;
    }

    public void setStuPickLongitude(String stuPickLongitude) {
        this.stuPickLongitude = stuPickLongitude;
    }

    public String getStuDropLatitude() {
        return stuDropLatitude;
    }

    public void setStuDropLatitude(String stuDropLatitude) {
        this.stuDropLatitude = stuDropLatitude;
    }

    public String getStuDropLongitude() {
        return stuDropLongitude;
    }

    public void setStuDropLongitude(String stuDropLongitude) {
        this.stuDropLongitude = stuDropLongitude;
    }

    public String getStuPickLocation() {
        return stuPickLocation;
    }

    public void setStuPickLocation(String stuPickLocation) {
        this.stuPickLocation = stuPickLocation;
    }

    public String getStuDropLocation() {
        return stuDropLocation;
    }

    public void setStuDropLocation(String stuDropLocation) {
        this.stuDropLocation = stuDropLocation;
    }

    public String getStuPickTime() {
        return stuPickTime;
    }

    public void setStuPickTime(String stuPickTime) {
        this.stuPickTime = stuPickTime;
    }

    public String getStuFrom() {
        return stuFrom;
    }

    public void setStuFrom(String stuFrom) {
        this.stuFrom = stuFrom;
    }

    public String getStuPayment() {
        return stuPayment;
    }

    public void setStuPayment(String stuPayment) {
        this.stuPayment = stuPayment;
    }
}
