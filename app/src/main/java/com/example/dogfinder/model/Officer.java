package com.example.dogfinder.model;

import com.google.gson.annotations.SerializedName;

public class Officer {

    @SerializedName("officer_id")
    private int officerId;

    @SerializedName("car_id")
    private int carId;

    @SerializedName("officer_name")
    private String officerName;

    @SerializedName("officer_mname")
    private String officerMName;

    @SerializedName("officer_lname")
    private String officerLName;

    @SerializedName("officer_age")
    private int officerAge;

    @SerializedName("officer_suffix")
    private String officerSuffix;

    @SerializedName("officer_address")
    private String officerAddress;

    @SerializedName("officer_username")
    private String officerUsername;

    public int getOfficerId() {
        return officerId;
    }

    public void setOfficerId(int officerId) {
        this.officerId = officerId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOfficerMName() {
        return officerMName;
    }

    public void setOfficerMName(String officerMName) {
        this.officerMName = officerMName;
    }

    public String getOfficerLName() {
        return officerLName;
    }

    public void setOfficerLName(String officerLName) {
        this.officerLName = officerLName;
    }

    public int getOfficerAge() {
        return officerAge;
    }

    public void setOfficerAge(int officerAge) {
        this.officerAge = officerAge;
    }

    public String getOfficerSuffix() {
        return officerSuffix;
    }

    public void setOfficerSuffix(String officerSuffix) {
        this.officerSuffix = officerSuffix;
    }

    public String getOfficerAddress() {
        return officerAddress;
    }

    public void setOfficerAddress(String officerAddress) {
        this.officerAddress = officerAddress;
    }

    public String getOfficerUsername() {
        return officerUsername;
    }

    public void setOfficerUsername(String officerUsername) {
        this.officerUsername = officerUsername;
    }
}
