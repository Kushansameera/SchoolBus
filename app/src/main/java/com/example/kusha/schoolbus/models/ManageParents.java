package com.example.kusha.schoolbus.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kusha on 11/19/2016.
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ManageParents {
    private String parentName;
    private String parentEmail;
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
}
