package com.ngngteam.pocketwallet.Model;

/**
 * Created by Vromia on 14/6/2015.
 */
public class ChildItem {

    private String category;
    private double price;
    private String date;
    private String notes;
    private String month;
    private int uniqueID;

    public ChildItem(String category,double price,String date,String notes){
        this.category=category;
        this.date=date;
        this.price=price;
        this.notes=notes;

    }

    public void setUniqueID(int id){
        this.uniqueID=id;
    }

    public void setMonth(String month){
        this.month=month;
    }


    public int getUniqueID(){
        return uniqueID;
    }

    public String getCategory(){
        return category;
    }

    public String getDate(){
        return date;
    }

    public String getNotes(){
        return notes;
    }

    public String getMonth(){
        return month;
    }

    public double getPrice(){
        return price;
    }


}
