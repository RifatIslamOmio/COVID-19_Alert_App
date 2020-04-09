package com.example.covid_19alertapp.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class VisitedLocations {

    @PrimaryKey
    @NonNull
    private String conatainerDateTimeComposite;

    private int count;

    public VisitedLocations() {
        /*
        necessary for room(?)
         */
    }

    @Ignore
    public VisitedLocations(String conatainerDateTimeComposite, int count) {
        this.conatainerDateTimeComposite = conatainerDateTimeComposite;
        this.count = count;
    }

    @Ignore
    public String[] splitPrimaryKey(String conatainerDateTimeComposite){
        /*
        returns 'latLon' and 'dateTime'
         */
        return conatainerDateTimeComposite.split("_");
    }

    public void setConatainerDateTimeComposite(String conatainerDateTimeComposite) {
        this.conatainerDateTimeComposite = conatainerDateTimeComposite;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getConatainerDateTimeComposite() {
        return conatainerDateTimeComposite;
    }

    public int getCount() {
        return count;
    }

}
