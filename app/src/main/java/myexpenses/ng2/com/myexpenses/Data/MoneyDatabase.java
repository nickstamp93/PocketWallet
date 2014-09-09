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

    public void InsertExpense(ExpenseItem expense){

        ContentValues values=new ContentValues();
        values.put(Key_ECategory,expense.getCategories());
        values.put(Key_EDate,expense.getDate());
        values.put(Key_EPrice,expense.getPrice());
        values.put(Key_ENotes,expense.getNotes());
        values.put(Key_EReceive, expense.getReceive());
        //Need to check this
        /*
        if(photo) {
            values.put(Key_EReceive, expense.getReceive());
                  }*/
        mydb.insert(Table_Expense,null,values);

    }

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

       return getReadableDatabase().rawQuery("SELECT * FROM "+ Table_Expense + " WHERE " + Key_EDate + " LIKE " + "'"+date+"'",
               null);
    }

 //return a cursor which contains the tuples of table expense between of parameter date1 and parameter date2
    public Cursor getExpensesByDateToDate(String date1,String date2){

        return getReadableDatabase().rawQuery("SELECT * FROM "+Table_Expense + " WHERE " + Key_EDate + " >= " + "'"+date1 +"'"+ " AND "+ Key_EDate +
                        " <= "+ "'"+date2+"'",
                null);
    }


}
