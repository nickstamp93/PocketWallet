package com.ngngteam.pocketwallet.Model;

import java.util.ArrayList;

/**
 * Created by nickstamp on 7/17/2015.
 */
public class RecurrentTransaction {

    private int id, interval, freq, isExpense;
    private String name, category, date, day, expiration;
    private double amount;


    public RecurrentTransaction(String name, double amount, String category, String date, int freq, int interval,
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

    public RecurrentTransaction(String name, double amount, String category, String date, String repeatString, int isExpense) {
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
        String[] tokens = s.split(";");
        for (String item : tokens) {
            String name = item.split("=")[0];
            String value = item.split("=")[1];
            if (name.equalsIgnoreCase("freq")) {
                if (value.equalsIgnoreCase("daily"))
                    freq = 0;
                else if (value.equalsIgnoreCase("daily"))
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
            if (name.equalsIgnoreCase("until"))
                expiration = "date:" + value;
            if (name.equalsIgnoreCase("byday"))
                day = value;
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
