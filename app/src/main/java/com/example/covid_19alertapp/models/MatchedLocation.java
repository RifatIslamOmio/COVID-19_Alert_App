package com.example.covid_19alertapp.models;

import android.util.Log;

import com.example.covid_19alertapp.extras.LogTags;

public class MatchedLocation {

    private double latitude, longitude;
    private String address = "";
    private String meaningfulDateTime;
    private long count;

    public MatchedLocation() {
        /*
        hudai
         */
    }

    public MatchedLocation(String latLon, String dateTime, long count) {
        /*
        parameters according to firebase formatted data
         */

        // get latitude, longitude
        latLon = latLon.replace('@', '.');
        String[] splitLatitude = latLon.split(",");

        this.latitude = ( Double.valueOf(splitLatitude[0])+Double.valueOf(splitLatitude[2]) )/ 2.000000d;
        this.longitude = ( Double.valueOf(splitLatitude[1])+ Double.valueOf(splitLatitude[3]) )/ 2.000000d;

        // get meaningfulDateTime
        String[] splitDateTime = dateTime.split("-");
        // Month date, time
        this.meaningfulDateTime = month(
                Integer.parseInt(splitDateTime[0]))
                + " "+splitDateTime[1]
                +", "+time(Integer.parseInt(splitDateTime[2])
        );

        this.count = count;

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeaningfulDateTime() {
        return meaningfulDateTime;
    }

    public void setMeaningfulDateTime(String meaningfulDateTime) {
        this.meaningfulDateTime = meaningfulDateTime;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    private String time(int time) {

        if(time==0)
            return "12AM";

        if(time<12)
            return time+"AM";
        else
            return (time-12)+"PM";

    }

    private String month(int month) {

        switch (month){

            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";

            default:
                return "Unknown month";
        }

    }

    @Override
    public String toString() {
        return "MatchedLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", meaningfulDateTime='" + meaningfulDateTime + '\'' +
                ", count=" + count +
                '}';
    }
}
