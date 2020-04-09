package com.example.covid_19alertapp.dataStorage;

public class UserInfoData {
    String name,dob,workAddress,home;

    public UserInfoData(String name, String dob, String workAddress, String home) {
        this.name = name;
        this.dob = dob;
        this.workAddress = workAddress;
        this.home = home;
    }

    public UserInfoData(String name, String dob, String home) {
        this.name = name;
        this.dob = dob;
        this.home = home;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public String getHome() {
        return home;
    }
}
