package myexpenses.ng2.com.myexpenses.Data;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Nikos on 7/26/2014.
 */
public class ExpenseItem implements Serializable {

   private int id;
   private String categories,notes,date;
   private double price;
   private byte[] receive;

   public ExpenseItem(){

   }

   public ExpenseItem(String categories,String notes,double price,String date){
       this.categories=categories;
       this.notes=notes;
       this.price=price;
       this.date=date;
   }


   public void setReceive(Bitmap photo){
       ByteArrayOutputStream stream =new ByteArrayOutputStream();
       photo.compress(Bitmap.CompressFormat.PNG,0,stream);
       receive=stream.toByteArray();
   }

   public void setCategories(String categories){
       this.categories=categories;
   }

   public void setNotes(String notes){
       this.notes=notes;
   }

   public void setId(int id){
       this.id=id;
   }

   public void setPrice(double price){
       this.price=price;
   }

   public void setDate(String date){
       this.date=date;
   }

   public String getCategories(){
       return categories;
   }

   public String getNotes(){
       return notes;
   }

   public double getPrice(){
       return price;
   }

   public String getDate(){
       return date;
   }

public byte[] getReceive(){
    return receive;
}

   public int getId(){
       return id;
   }










}
