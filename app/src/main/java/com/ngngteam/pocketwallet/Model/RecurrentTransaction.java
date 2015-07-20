package com.ngngteam.pocketwallet.Model;

import android.database.Cursor;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by nickstamp on 7/17/2015.
 */
public class RecurrentTransaction implements Serializable {

    private int id, interval, freq, isExpense;
    private String name, category, date, day, expiration;
    private double amount;

    public RecurrentTransaction() {

    }

    public RecurrentTransaction(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.amount = cursor.getDouble(2);
        this.category = cursor.getString(3);
        this.date = cursor.getString(4);
        this.freq = cursor.getInt(5);
        this.interval = cursor.getInt(6);
        this.day = cursor.getString(7);
        this.expiration = cursor.getString(8);
        this.isExpense = cursor.getInt(9);
    }

    public RecurrentTransaction(String name, double amount, String category, String
            date, int freq, int interval,
                                String day, String expiration, int isExpense) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.freq = freq;
        this.interval = interval;
        this.day = day;
        this.expiration = expiration;
        this.isExpense = isExpense;
    }

    public RecurrentTransaction(String name, double amount, String category, String
            date, String repeatString, int isExpense) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        populateFromDialog(repeatString);
        this.isExpense = isExpense;
    }

    public void populateFromDialog(String s) {
        interval = 1;
        expiration = null;
        if(s != null){
            String[] tokens = s.split(";");
            for (String item : tokens) {
                String name = item.split("=")[0];
                String value = item.split("=")[1];
                if (name.equalsIgnoreCase("freq")) {
                    if (value.equalsIgnoreCase("daily"))
                        freq = 0;
                    else if (value.equalsIgnoreCase("weekly"))
                        freq = 1;
                    else if (value.equalsIgnoreCase("monthly"))
                        freq = 2;
                    else if (value.equalsIgnoreCase("yearly"))
                        freq = 3;
                }
                if (name.equalsIgnoreCase("interval"))
                    interval = Integer.parseInt(value);
                if (name.equalsIgnoreCase("count"))
                    expiration = "count:" + Integer.parseInt(value);
                if (name.equalsIgnoreCase("until")) {
                    value = value.substring(0, 8);
                    Log.i("nikos" , value);
                    try {
                        value = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(value));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.i("nikos" , value);
                    expiration = "date:" + value;
                }
                if (name.equalsIgnoreCase("byday"))
                    day = value;
            }

        }

    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public int getFreq() {
        return freq;
    }

    public double getAmount() {
        return amount;
    }

    public String getExpiration() {
        return expiration;
    }

    public int getIsExpense() {
        return isExpense;
    }

    public String getDay() {
        return day;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void setIsExpense(int isExpense) {
        this.isExpense = isExpense;
    }

    public void setDay(String day) {
        this.day = day;
    }


    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
