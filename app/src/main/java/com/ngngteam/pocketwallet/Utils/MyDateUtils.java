package com.ngngteam.pocketwallet.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nickstamp on 7/31/2015.
 */
public class MyDateUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isToday(String date) {
        try {
            Date dateSelected = dateFormat.parse(date);
            Calendar cToday = Calendar.getInstance();
            Calendar cDate = Calendar.getInstance();
            cDate.setTime(dateSelected);
            if (cToday.get(Calendar.YEAR) == cDate.get(Calendar.YEAR)
                    && cToday.get(Calendar.DAY_OF_YEAR) == cDate.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isYesterday(String date) {
        try {
            Date dateSelected = dateFormat.parse(date);
            Calendar cYesterday = Calendar.getInstance();
            cYesterday.add(Calendar.DAY_OF_YEAR , -1);

            Calendar cDate = Calendar.getInstance();
            cDate.setTime(dateSelected);
            if (cYesterday.get(Calendar.YEAR) == cDate.get(Calendar.YEAR)
                    && cYesterday.get(Calendar.DAY_OF_YEAR) == cDate.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTomorrow(String date) {
        try {
            Date dateSelected = dateFormat.parse(date);
            Calendar cTomorrow = Calendar.getInstance();
            cTomorrow.add(Calendar.DAY_OF_YEAR , 1);

            Calendar cDate = Calendar.getInstance();
            cDate.setTime(dateSelected);
            if (cTomorrow.get(Calendar.YEAR) == cDate.get(Calendar.YEAR)
                    && cTomorrow.get(Calendar.DAY_OF_YEAR) == cDate.get(Calendar.DAY_OF_YEAR)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
