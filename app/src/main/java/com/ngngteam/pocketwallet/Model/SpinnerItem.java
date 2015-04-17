package com.ngngteam.pocketwallet.Model;

/**
 * Created by Vromia on 7/11/2014.
 */
public class SpinnerItem {

    private String name;
    private int color;
    private char letter;

    public SpinnerItem(String name,int color,char letter){
        this.name=name;
        this.color=color;
        this.letter=letter;
    }

    public String getName(){
        return name;
    }

    public int getColor(){
        return color;
    }

    public char getLetter(){
        return letter;
    }


}
