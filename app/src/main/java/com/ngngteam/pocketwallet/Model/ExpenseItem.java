package com.ngngteam.pocketwallet.Model;

import java.io.Serializable;

/**
 * Created by Nikos on 7/26/2014.
 */
public class ExpenseItem implements Serializable {

    private int id;
    private String category, notes, date;
    private double price;

    public ExpenseItem(String category, String notes, double price, String date) {
        this.category = category;
        this.notes = notes;
        this.price = price;
        this.date = date;
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

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }


}
