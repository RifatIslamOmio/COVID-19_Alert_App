package com.example.covid_19alertapp.models;

public class InfectedLocations {
/*
 firebase model
 */
    private double uLatitude, uLongitude, lLatitude, lLongitude;
    private String address = null, dateTime;

    public InfectedLocations() {
        /*
        required for firebase
         */
    }

    public double getuLatitude() {
        return uLatitude;
    }

    public void setuLatitude(double uLatitude) {
        this.uLatitude = uLatitude;
    }

    public double getuLongitude() {
        return uLongitude;
    }

    public void setuLongitude(double uLongitude) {
        this.uLongitude = uLongitude;
    }

    public double getlLatitude() {
        return lLatitude;
    }

    public void setlLatitude(double lLatitude) {
        this.lLatitude = lLatitude;
    }

    public double getlLongitude() {
        return lLongitude;
    }

    public void setlLongitude(double lLongitude) {
        this.lLongitude = lLongitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
