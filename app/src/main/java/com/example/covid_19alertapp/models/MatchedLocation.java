package com.example.covid_19alertapp.models;

public class MatchedLocation {

    private double blLatitude, blLongitude;
    private String address = "";
    private String meaningfulDateTime;
    private long count;

    public MatchedLocation() {
        /*
        hudai
         */
    }

    public MatchedLocation(double blLatitude, double blLongitude, String address, long count) {
        this.blLatitude = blLatitude;
        this.blLongitude = blLongitude;
        this.address = address;
        this.meaningfulDateTime = "last 7 days";
        this.count = count;
    }

    public MatchedLocation(String latLon, String dateTime, long count) {
        /*
        parameters according to firebase formatted data
         */

        // get latitude, longitude
        latLon = latLon.replace('@', '.');
        String[] splitLatitude = latLon.split(",");

        this.blLatitude = Double.valueOf(splitLatitude[0]);
        this.blLongitude = Double.valueOf(splitLatitude[1]);

        // get meaningfulDateTime
        String[] splitDateTime = dateTime.split("-");
        // Month date, time
        this.meaningfulDateTime = month(
                Integer.parseInt(splitDateTime[0]))
                + " "+splitDateTime[1]
                +", "+time(Integer.parseInt(splitDateTime[2])
        );

        this.count = count;

        this.address = "fetching address";

    }

    public double getBlLatitude() {
        return blLatitude;
    }

    public void setBlLatitude(double blLatitude) {
        this.blLatitude = blLatitude;
    }

    public double getBlLongitude() {
        return blLongitude;
    }

    public void setBlLongitude(double blLongitude) {
        this.blLongitude = blLongitude;
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
                "bottom left=" + blLatitude +","+blLongitude+
                ", address='" + address + '\'' +
                ", meaningfulDateTime='" + meaningfulDateTime + '\'' +
                ", count=" + count +
                '}';
    }
}
