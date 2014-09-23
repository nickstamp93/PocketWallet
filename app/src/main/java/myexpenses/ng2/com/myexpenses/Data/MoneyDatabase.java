package myexpenses.ng2.com.myexpenses.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Nikos on 7/26/2014.
 */
public class MoneyDatabase extends SQLiteOpenHelper {

    private static final int Database_Version=1;
    private static final String Database_Name="MoneyDatabase";

    private static final String Table_Expense="Expense";
    private static final String Table_Income="Income";

    //Table Expense Columns
    private static final String Key_EId ="_id";
    private static final String Key_ECategory ="category";
    private static final String Key_EDate ="date";
    private static final String Key_EPrice ="price";
    private static final String Key_ENotes ="notes";
    private static final String Key_EReceive ="receive";

    //Table Income Columns
    private static final String Key_Iid="_id";
    private static final String Key_IAmount="amount";
    private static final String Key_ISource="source";
    private static final String Key_IDate="date";

    private SQLiteDatabase mydb;

    private static final String Create_Expense_Table="CREATE TABLE " + Table_Expense + "(" + Key_EId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Key_ECategory + " TEXT NOT NULL," + Key_EDate + " TEXT NOT NULL," + Key_EPrice + " DOUBLE,"+ Key_ENotes + " TEXT,"+ Key_EReceive + " BLOB" + ")"  ;

    private static final String Create_Income_Table="CREATE TABLE " + Table_Income + "(" + Key_Iid + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Key_IAmount + " DOUBLE," + Key_ISource + " TEXT NOT NULL," + Key_IDate + " TEXT NOT NULL" + ")";

    public MoneyDatabase(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      sqLiteDatabase.execSQL(Create_Expense_Table);
       sqLiteDatabase.execSQL(Create_Income_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ Table_Expense);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ Table_Income);

        onCreate(sqLiteDatabase);
    }

    public void openDatabase() throws SQLException{
        mydb=this.getWritableDatabase();
    }

//we take from parameter expense all the attributes we create a tuple in table Expense
    public void InsertExpense(ExpenseItem expense){

        ContentValues values=new ContentValues();
        values.put(Key_ECategory,expense.getCategories());
        values.put(Key_EDate,expense.getDate());
        values.put(Key_EPrice,expense.getPrice());
        values.put(Key_ENotes,expense.getNotes());
        values.put(Key_EReceive, expense.getReceive());

        mydb.insert(Table_Expense,null,values);

    }

    //we take from parameter income all the attributes we create a tuple in table Income
    public void InsertIncome(IncomeItem income){

        ContentValues values=new ContentValues();
        values.put(Key_IAmount,income.getAmount());
        values.put(Key_ISource, income.getSource());
        values.put(Key_IDate,income.getDate());
        mydb.insert(Table_Income,null,values);

    }

    //return a ArrayList with IncomeItem that contains all of the tuples in table Incomes
    public ArrayList<IncomeItem> getAllIncomes(){

        ArrayList<IncomeItem> incomes=new ArrayList<IncomeItem>();
        Cursor c=mydb.rawQuery("SELECT * FROM "+ Table_Income,null);


        int iRow=c.getColumnIndex(Key_Iid);
        int iAmount=c.getColumnIndex(Key_IAmount);
        int iSource=c.getColumnIndex(Key_ISource);
        int iDate=c.getColumnIndex(Key_IDate);

        if(c!=null){

            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

               int id=Integer.parseInt(c.getString(iRow));
               double amount=Double.parseDouble(c.getString(iAmount));
               String source=c.getString(iSource);
               String date=c.getString(iDate);
               IncomeItem income=new IncomeItem(amount,date,source);
               income.setId(id);
               incomes.add(income);

            }

        }
        return incomes;

    }
//return a ArrayList with ExpenseItem that contains all of the tuples in table Expenses
    public ArrayList<ExpenseItem> getAllExpenses(){

        ArrayList<ExpenseItem> expenses=new ArrayList<ExpenseItem>();
        Cursor c=mydb.rawQuery("SELECT * FROM " + Table_Expense,null);


       int eRow=c.getColumnIndex(Key_EId);
       int eCategory=c.getColumnIndex(Key_ECategory);
       int eDate=c.getColumnIndex(Key_EDate);
       int ePrice=c.getColumnIndex(Key_EPrice);
       int eNotes=c.getColumnIndex(Key_ENotes);

       if(c!=null){

           for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

            int id=Integer.parseInt(c.getString(eRow));
            String category=c.getString(eCategory);
            String date=c.getString(eDate);
            double price=Double.parseDouble(c.getString(ePrice));
            String notes=c.getString(eNotes);

            ExpenseItem expense=new ExpenseItem(category,notes,price,date);
            expense.setId(id);
            expenses.add(expense);

           }

       }


        return expenses;
    }

    //return a cursor which contains the whole table expense (select *)
    public Cursor getCursorExpense() {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense,
                null);
    }

    //return a cursor which contains the tuples with Category equal to the parameter category of table Expenses
    public Cursor getExpensesByCategory(String category){

        return getReadableDatabase().rawQuery("SELECT * FROM "+ Table_Expense + " WHERE " + Key_ECategory + " LIKE " + "'"+category+"'" ,
                null);
    }

  //return a cursor which contains the table expense order by price depended on variable asc (ASC-DESC)
    public Cursor getExpensesByPriceOrder(boolean asc){
        String order=" ASC";
        if(!asc){
            order=" DESC";
        }
        return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Expense + " ORDER BY "+ Key_EPrice + order,null);

    }

 //return a cursor which contains the tuples of table expense with Date equal to parameter date
    public Cursor getExpensesByDate(String date){

        String dateTokens[]=date.split("-");
        String reformedDate=dateTokens[2]+"-"+dateTokens[1]+"-"+dateTokens[0];

       return getReadableDatabase().rawQuery("SELECT * FROM "+ Table_Expense + " WHERE " + Key_EDate + " LIKE " + "'"+reformedDate+"'",
               null);
    }

 //return a cursor which contains the tuples of table expense with date between of parameter date1 and parameter date2
    public Cursor getExpensesByDateToDate(String date1,String date2){

        String DateFrom[]=date1.split("-");
        String DateTo[]=date2.split("-");
        String reformedDateFrom=DateFrom[2]+"-"+DateFrom[1]+"-"+DateFrom[0];
        String reformedDateTo=DateTo[2]+"-"+DateTo[1]+"-"+DateTo[0];

        return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Expense+ " WHERE "+ Key_EDate + ">=" + "'"+reformedDateFrom+"'" +
        " AND " + Key_EDate + "<="+ "'"+reformedDateTo+"'" ,null);

        /*
        ArrayList<String> Valid=new ArrayList<String>();

         Log.i("Reformed DateFrom",reformedDateFrom);
        Log.i("Reformed DateTo",reformedDateTo);

        Cursor c= getReadableDatabase().rawQuery("SELECT * " + " FROM " + Table_Expense ,null);
        if(c!=null){

            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

                String date=c.getString(c.getColumnIndex(Key_EDate));

                String tokens[]=date.split("-");
                String reformedDate=tokens[2]+"-"+tokens[1]+"-"+tokens[0];
              //  Log.i("Date from cursor reformed= ",reformedDate);

                if(compareDates(reformedDateFrom,reformedDateTo,reformedDate) ){
                    Valid.add(date);
                 }

            }


        }

        StringBuilder sb=new StringBuilder();
        if(Valid.size()>0) {
            for (int i = 0; i < Valid.size() - 1; i++) {
                sb.append("'" + Valid.get(i) + "'" + ",");
            }
            sb.append("'" + Valid.get(Valid.size() - 1) + "'");
         }

        return getReadableDatabase().rawQuery("SELECT * "+ " FROM " + Table_Expense + " WHERE " + Key_EDate + " IN " +"("+sb.toString()+")"
                ,null );
                */
    }

    public Cursor getExpensesFromNewestToOldest(){

   return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Expense+ " ORDER BY "+Key_EDate+" DESC"
                ,null);
    }

/*
    private boolean compareDates(String from,String to,String date){

        String dateTokens[]=date.split("-");
        String fromTokens[]=from.split("-");
        String toTokens[]=to.split("-");

        int size;
        int dateValues[],fromValues[],toValues[];

        size=dateTokens.length;
        dateValues=new int[size];
        fromValues=new int[size];
        toValues=new int[size];

        for (int i=0; i<size; i++){

            dateValues[i]=Integer.parseInt(dateTokens[i]);
            fromValues[i]=Integer.parseInt(fromTokens[i]);
            toValues[i]=Integer.parseInt(toTokens[i]);

        }

        for(int i=0; i<size; i++){

            if(dateValues[i]<fromValues[i] || dateValues[i]>toValues[i]){
                return false;
            }

        }

        return true;

    }
    */

    public Cursor getCursorIncomes(){
        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income,
                null);
    }

    public Cursor getIncomesBySource(String source){

        return getReadableDatabase().rawQuery("SELECT * FROM "+ Table_Income + " WHERE " + Key_ISource + "=" +"'"+source+"'",null );

    }

    public Cursor getIncomeByDate(String date){

        String dateTokens[]=date.split("-");
        String reformedDate=dateTokens[2]+"-"+dateTokens[1]+"-"+dateTokens[0];

        return getReadableDatabase().rawQuery("SELECT * FROM "+ Table_Income + " WHERE " + Key_IDate + " LIKE " + "'"+reformedDate+"'",
                null);
    }

    public Cursor getIncomeByNewestToOldest(){

        return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Income+ " ORDER BY "+Key_IDate+" DESC"
                ,null);
    }

    public Cursor getIncomeByDateToDate(String date1,String date2){

        String DateFrom[]=date1.split("-");
        String DateTo[]=date2.split("-");
        String reformedDateFrom=DateFrom[2]+"-"+DateFrom[1]+"-"+DateFrom[0];
        String reformedDateTo=DateTo[2]+"-"+DateTo[1]+"-"+DateTo[0];

        return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Income+ " WHERE "+ Key_IDate + ">=" + "'"+reformedDateFrom+"'" +
                " AND " + Key_IDate + "<="+ "'"+reformedDateTo+"'" ,null);
    }


}
