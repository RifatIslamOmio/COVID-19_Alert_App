package com.example.covid_19alertapp.models;

public class InfectedLocations {
/*
 firebase model
 */

    private int count;
    private String address = null, dateTime;

    public InfectedLocations() {
        /*
        required for firebase
         */
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateTime() { return dateTime; }

    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

}
