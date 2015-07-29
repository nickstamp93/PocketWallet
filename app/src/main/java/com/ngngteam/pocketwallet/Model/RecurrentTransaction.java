package com.ngngteam.pocketwallet.Model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ngngteam.pocketwallet.Data.MoneyDatabase;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nickstamp on 7/17/2015.
 * Represents a recurrent transaction item
 */
public class RecurrentTransaction implements Serializable {

    private int id, interval, freq, day, isExpense, isValid, isPending;
    private String name, category, date, expiration, nextDate;
    private double amount;

    private SimpleDateFormat dateFormat;

    /**
     * Default constructor
     */
    public RecurrentTransaction() {

    }

    /**
     * @param cursor
     */
    public RecurrentTransaction(Cursor cursor) {

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        this.id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.amount = cursor.getDouble(2);
        this.category = cursor.getString(3);
        this.date = cursor.getString(4);
        this.freq = cursor.getInt(5);
        this.interval = cursor.getInt(6);
        this.day = cursor.getInt(7);
        this.expiration = cursor.getString(8);
        this.nextDate = cursor.getString(9);
        this.isExpense = cursor.getInt(10);
        this.isPending = cursor.getInt(11);
        this.isValid = cursor.getInt(12);

    }


    /**
     * @param name
     * @param amount
     * @param category
     * @param date
     * @param isExpense
     */
    public RecurrentTransaction(String name, double amount, String category, String
            date, int freq, int interval, int day, String expiration, int isExpense, int isPending, int isValid) {

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.freq = freq;
        if (interval == 0)
            interval = 1;
        this.interval = interval;
        this.day = day;
        this.expiration = expiration;
        this.nextDate = date;
        this.isExpense = isExpense;
        this.isPending = isPending;
        this.isValid = isValid;
    }

    public void calculateNextDate(String dateStartingPoint) {

        try {
            Date nDate = dateFormat.parse(dateStartingPoint);//next date

            Calendar c = Calendar.getInstance();
            c.setTime(nDate);

            int freqVar = Calendar.MONTH;
            switch (freq) {
                //daily
                case 0:
                    freqVar = Calendar.DAY_OF_YEAR;
                    break;
                //weekly
                case 1:
                    freqVar = Calendar.WEEK_OF_YEAR;
                    break;
                //monthly
                case 2:
                    freqVar = Calendar.MONTH;
                    break;
                //yearly
                case 3:
                    freqVar = Calendar.YEAR;
                    break;
            }
            c.add(freqVar, interval);
            nDate = c.getTime();

            nextDate = dateFormat.format(nDate);

            Date today = Calendar.getInstance().getTime();

            //if expires by date
            if (expiration != null) {
                if (expiration.split(":")[0].equalsIgnoreCase("date")) {
                    Date expDate = dateFormat.parse(expiration.split(":")[1]);
                    if (today.after(expDate)) {
                        isValid = 0;
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void addOneTransaction(Context context) {

        try {
            Date startingDate = dateFormat.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(startingDate);

            if (isExpense == 1) {
                ExpenseItem item = new ExpenseItem(getCategory(), getName(), getAmount(), dateFormat.format(c.getTime()));
                MoneyDatabase db = new MoneyDatabase(context);
                try {
                    db.openDatabase();
                    db.insertExpense(item);
                    db.close();
                    Toast.makeText(context, "Expense transaction added succesfully", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                IncomeItem item = new IncomeItem(getAmount(), dateFormat.format(c.getTime()), getCategory(), getName());
                MoneyDatabase db = new MoneyDatabase(context);
                try {
                    db.openDatabase();
                    db.insertIncome(item);
                    db.close();
                    Toast.makeText(context, "Expense transaction added succesfully", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //if expires by event count
            if (expiration != null) {
                if (expiration.split(":")[0].equalsIgnoreCase("count")) {
                    increaseCountEvent();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        calculateNextDate(date);
    }

    /**
     * this method is called at the creation of a recurrent transaction and adds as many transactions as needed
     * if the date is set to a previous date until the next date is set to a future date
     *
     * @param context passing the context in order to have access to the money database
     */
    public void addPreviousTransactions(Context context) {

        Date today = Calendar.getInstance().getTime();
        try {
            Date startingDate = dateFormat.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(startingDate);

            boolean expense = isExpense == 1 ? true : false;

            //while the next date is in the past
            //and the transaction has not expired
            while (today.after(c.getTime()) && isValid == 1) {

                if (expense) {
                    ExpenseItem item = new ExpenseItem(getCategory(), getName(), getAmount(), dateFormat.format(c.getTime()));
                    MoneyDatabase db = new MoneyDatabase(context);
                    try {
                        db.openDatabase();
                        db.insertExpense(item);
                        db.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    IncomeItem item = new IncomeItem(getAmount(), dateFormat.format(c.getTime()), getCategory(), getName());
                    MoneyDatabase db = new MoneyDatabase(context);
                    try {
                        db.openDatabase();
                        db.insertIncome(item);
                        db.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (expiration != null) {
                    if (expiration.split(":")[0].equalsIgnoreCase("date")) {
                        Date expDate = dateFormat.parse(expiration.split(":")[1]);
                        if (today.after(expDate)) {
                            isValid = 0;
                        }
                    } else if (expiration.split(":")[0].equalsIgnoreCase("count")) {
                        increaseCountEvent();
                    }
                }
//                calculateNextDate();
                c.setTime(dateFormat.parse(nextDate));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function that analyzes the string returned by the RecurrencePicker dialog
     * and populated the item whith the appropriate values
     *
     * @param s the string from the dialog
     */
    public void populateFromDialog(String s) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        interval = 1;
        expiration = null;

        if (s != null) {

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

                if (name.equalsIgnoreCase("count")) {
                    //format : count:0/value
                    //so that we know how many events have been done
                    expiration = "count:0/" + Integer.parseInt(value);

                }

                if (name.equalsIgnoreCase("until")) {
                    //format "until:yyyy-MM-dd
                    //take the first 8 chars which are the date with format yyyyMMdd
                    value = value.substring(0, 8);

                    try {
                        //convert to the db format
                        value = dateFormat.format(new SimpleDateFormat("yyyyMMdd").parse(value));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    expiration = "date:" + value;
                }
                if (name.equalsIgnoreCase("byday"))
                    day = Integer.valueOf(value);
            }

        }

    }

    /**
     * when expiration is by event count , increase events that are done by 1
     */
    private void increaseCountEvent() {
        //count:event/total
        //event shows how many are done

        int events = Integer.parseInt(expiration.split(":")[1].split("/")[0]);
        int total = Integer.parseInt(expiration.split(":")[1].split("/")[1]);

        if (total == (events + 1))
            isValid = 0;

        expiration = "count:" + (events + 1) + "/" + total;
    }

    public void show() {
        Log.i("nikos", "id:" + id);
        Log.i("nikos", "name:" + name);
        Log.i("nikos", "amount:" + amount);
        Log.i("nikos", "category:" + category);
        Log.i("nikos", "date:" + date);
        Log.i("nikos", "freq:" + freq);
        Log.i("nikos", "interval:" + interval);
        Log.i("nikos", "day:" + day);
        Log.i("nikos", "expiration:" + expiration);
        Log.i("nikos", "nextdate:" + nextDate);
        Log.i("nikos", "isExpense:" + isExpense);
        Log.i("nikos", "isPending:" + isPending);
        Log.i("nikos", "isValid:" + isValid + "\n\n\n");
    }

    //GETTERS

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

    public String getNextDate() {
        return nextDate;
    }

    public int getIsExpense() {
        return isExpense;
    }

    public int getDay() {
        return day;
    }

    public int getIsValid() {
        return isValid;
    }

    public int getIsPending() {
        return isPending;
    }


    //SETTERS

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

    public void setDay(int day) {
        this.day = day;
    }


    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }


    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }


    public void setIsPending(int isPending) {
        this.isPending = isPending;
    }


    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }
}
