package myexpenses.ng2.com.myexpenses.Data;

/**
 * Created by Nikos on 7/26/2014.
 */
public class IncomeItem {

   private double amount;
   private int id;
   private String date,source;


   public IncomeItem(double amount,String date,String source){
       this.amount=amount;
       this.date=date;
       this.source=source;
   }

    public IncomeItem(){

    }

    public void setId(int id){
        this.id=id;
    }

    public void setAmount(double amount){
        this.amount=amount;
    }


    public void setDate(String date){
        this.date=date;
    }

    public void setSource(String source){
        this.source=source;
    }

    public double getAmount(){
        return amount;
    }

    public String getDate(){
        return date;
    }

    public String getSource(){
        return source;
    }

    public int getId(){
        return id;
    }

}
