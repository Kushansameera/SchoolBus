package com.example.kusha.schoolbus.models;

/**
 * Created by kusha on 1/8/2017.
 */

public class PaymentSummery {
    private String targetIncome;
    private String received;
    private String receivables;
    private String year;
    private String month;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTargetIncome() {
        return targetIncome;
    }

    public void setTargetIncome(String targetIncome) {
        this.targetIncome = targetIncome;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getReceivables() {
        return receivables;
    }

    public void setReceivables(String receivables) {
        this.receivables = receivables;
    }
}
