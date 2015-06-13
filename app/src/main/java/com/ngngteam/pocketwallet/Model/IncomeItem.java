package com.ngngteam.pocketwallet.Model;

import java.io.Serializable;

/**
 * Created by Nikos on 7/26/2014.
 */
public class IncomeItem implements Serializable {

    private double amount;
    private int id;
    private String date, source,notes;


    public IncomeItem(double amount, String date, String source,String notes) {
        this.amount = amount;
        this.date = date;
        this.source = source;
        this.notes=notes;
    }

    public void setNotes(String notes){
        this.notes=notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

    public String getNotes(){
        return notes;
    }

    public int getId() {
        return id;
    }

}
