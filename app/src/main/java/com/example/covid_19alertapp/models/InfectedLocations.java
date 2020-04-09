package com.example.covid_19alertapp.models;

public class InfectedLocations {
/*
 firebase model
 */

    //@Exclude
    private String key = null;

    private int count = 0;
    private String address = null, dateTime;

    public InfectedLocations() {
        /*
        required for firebase
         */
    }

    public InfectedLocations(String key, int count, String dateTime) {
        this.key = key;
        this.count = count;
        this.dateTime = dateTime;
    }

    //@Exclude
    public boolean allFieldsSet(){
        return key!=null && count!=0 && dateTime!=null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
