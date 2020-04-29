package com.example.covid_19alertapp.dataStorage;

public class UserInfoData {
    String name,dob,workAddress,home, contactNumber;

    public UserInfoData(String name, String dob, String workAddress, String home,String contactNumber) {
        this.name = name;
        this.dob = dob;
        this.workAddress = workAddress;
        this.home = home;
        this.contactNumber = contactNumber;
        System.out.println(contactNumber+" contact");


    }

    public UserInfoData(String name, String dob, String home,String contactNumber) {
        this.name = name;
        this.dob = dob;
        this.home = home;
        this.contactNumber = contactNumber;
        System.out.println(contactNumber+" contact");
    }

    public String getContactNumber() {
        return contactNumber;
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
