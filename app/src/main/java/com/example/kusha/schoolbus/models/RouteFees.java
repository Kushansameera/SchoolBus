package com.example.kusha.schoolbus.models;

/**
 * Created by kusha on 10/13/2016.
 */

public class RouteFees {
    private String from;
    private String to;
    private String fee;
    private String routeFeeID;

    public String getRouteFeeID() {
        return routeFeeID;
    }

    public void setRouteFeeID(String routeFeeID) {
        this.routeFeeID = routeFeeID;
    }

    public RouteFees() {

    }

    public String getFrom() {

        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
