package com.ngngteam.pocketwallet.Model;

import java.util.ArrayList;

/**
 * Created by Vromia on 14/6/2015.
 */
public class ParentItem {

    private String month;
    private int year;
    private ArrayList<ChildItem> childItems;

    public ParentItem(String month,int year){
        this.month=month;
        this.year=year;
        childItems=new ArrayList<>();
    }

    public void addChild(ChildItem childItem){
        childItems.add(childItem);
    }

    public String getMonth(){
        return month;
    }

    public ArrayList<ChildItem> getChildItems(){
        return childItems;
    }

    public double getTotalAmount(){
        double total=0;
        for (int i=0; i<childItems.size(); i++){
            total+=childItems.get(i).getPrice();
        }

        return total;
    }

    public int getYear(){
        return year;
    }


}
