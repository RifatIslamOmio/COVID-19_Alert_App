package com.example.covid_19alertapp.roomdatabase;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VisitedLocationsDao {

    @Insert
    void insertLocations(VisitedLocations visitedLocations);

    @Query("UPDATE visitedlocations SET count = count+1 WHERE conatainerDateTimeComposite = :primaryKey")
    void update(String primaryKey);

    @Query("SELECT * FROM visitedlocations")
    List<VisitedLocations> fetchAll();


}
