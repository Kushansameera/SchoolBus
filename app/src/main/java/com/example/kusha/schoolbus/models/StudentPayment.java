package com.example.kusha.schoolbus.models;

/**
 * Created by kusha on 12/8/2016.
 */

public class StudentPayment {
    private String stuId;
    private String stuName;
    private String stuMonthlyFee;
    private String stuLastPaidMonth;
    private String stuLastPaidYear;
    private String stuReceivables;
    private String stuRegMonth;
    private String stuRegYear;

    public String getStuRegMonth() {
        return stuRegMonth;
    }

    public void setStuRegMonth(String stuRegMonth) {
        this.stuRegMonth = stuRegMonth;
    }

    public String getStuRegYear() {
        return stuRegYear;
    }

    public void setStuRegYear(String stuRegYear) {
        this.stuRegYear = stuRegYear;
    }

    public String getStuLastPaidYear() {
        return stuLastPaidYear;
    }

    public void setStuLastPaidYear(String stuLastPaidYear) {
        this.stuLastPaidYear = stuLastPaidYear;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuMonthlyFee() {
        return stuMonthlyFee;
    }

    public void setStuMonthlyFee(String stuMonthlyFee) {
        this.stuMonthlyFee = stuMonthlyFee;
    }

    public String getStuLastPaidMonth() {
        return stuLastPaidMonth;
    }

    public void setStuLastPaidMonth(String stuLastPaidMonth) {
        this.stuLastPaidMonth = stuLastPaidMonth;
    }

    public String getStuReceivables() {
        return stuReceivables;
    }

    public void setStuReceivables(String stuReceivables) {
        this.stuReceivables = stuReceivables;
    }
}
