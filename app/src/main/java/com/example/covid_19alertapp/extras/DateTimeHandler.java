package com.example.covid_19alertapp.extras;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeHandler {

    public static String DateToday()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String today_date = dateFormat.format(cal.getTime()) + " " + monthFormat.format(cal.getTime());
        return today_date;
    }

    public static String TimeNow()
    {
        Calendar cal = Calendar.getInstance();
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String time = timeFormat.format(cal.getTime());
        return  time;
    }

}
