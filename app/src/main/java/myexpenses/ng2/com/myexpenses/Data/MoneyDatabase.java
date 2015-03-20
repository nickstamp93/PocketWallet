package myexpenses.ng2.com.myexpenses.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikos on 7/26/2014.
 */
public class MoneyDatabase extends SQLiteOpenHelper {

    private static final int Database_Version = 1;
    private static final String Database_Name = "MoneyDatabase";

    private static final String Table_Expense = "Expense";
    private static final String Table_Income = "Income";

    //Table Expense Columns
    private static final String Key_EId = "_id";
    private static final String Key_ECategory = "category";
    private static final String Key_EDate = "date";
    private static final String Key_EPrice = "price";
    private static final String Key_ENotes = "notes";
    private static final String Key_EReceive = "receive";

    //Table Income Columns
    private static final String Key_Iid = "_id";
    private static final String Key_IAmount = "amount";
    private static final String Key_ISource = "source";
    private static final String Key_IDate = "date";

    private SQLiteDatabase mydb;

    private static final String Create_Expense_Table = "CREATE TABLE " + Table_Expense + "(" + Key_EId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Key_ECategory + " TEXT NOT NULL," + Key_EDate + " TEXT NOT NULL," + Key_EPrice + " DOUBLE," + Key_ENotes + " TEXT," + Key_EReceive + " BLOB" + ")";

    private static final String Create_Income_Table = "CREATE TABLE " + Table_Income + "(" + Key_Iid + " INTEGER PRIMARY KEY AUTOINCREMENT," +
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Table_Expense);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Table_Income);

        onCreate(sqLiteDatabase);
    }

    public void openDatabase() throws SQLException {
        mydb = this.getWritableDatabase();
    }

    //we take from parameter expense all the attributes we create a tuple in table Expense
    public void InsertExpense(ExpenseItem expense) {

        ContentValues values = new ContentValues();
        values.put(Key_ECategory, expense.getCategories());
        values.put(Key_EDate, expense.getDate());
        values.put(Key_EPrice, expense.getPrice());
        values.put(Key_ENotes, expense.getNotes());
        values.put(Key_EReceive, expense.getReceive());

        mydb.insert(Table_Expense, null, values);

    }

    //we take from parameter income all the attributes we create a tuple in table Income
    public void InsertIncome(IncomeItem income) {

        ContentValues values = new ContentValues();
        values.put(Key_IAmount, income.getAmount());
        values.put(Key_ISource, income.getSource());
        values.put(Key_IDate, income.getDate());
        mydb.insert(Table_Income, null, values);

    }


    //return a cursor which contains the whole table expense (select *)
    public Cursor getCursorExpense() {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense,
                null);
    }

    //return a cursor which contains the tuples with Category equal to the parameter category of table Expenses
    public Cursor getExpensesByCategory(String category) {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " WHERE " + Key_ECategory + " LIKE " + "'" + category + "'",
                null);
    }

    //return a cursor which contains the table expense order by price depended on variable asc (ASC-DESC)
    public Cursor getExpensesByPriceOrder(boolean asc) {
        String order = " ASC";
        if (!asc) {
            order = " DESC";
        }
        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " ORDER BY " + Key_EPrice + order, null);

    }

    //return a cursor which contains the tuples of table expense with Date equal to parameter date
    public Cursor getExpensesByDate(String date) {

        String dateTokens[] = date.split("-");
        String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " WHERE " + Key_EDate + " LIKE " + "'" + reformedDate + "'",
                null);
    }

    //return a cursor which contains the tuples of table expense with date between of parameter date1 and parameter date2
    public Cursor getExpensesByDateToDate(String date1, String date2) {

        String DateFrom[] = date1.split("-");
        String DateTo[] = date2.split("-");
        String reformedDateFrom = DateFrom[2] + "-" + DateFrom[1] + "-" + DateFrom[0];
        String reformedDateTo = DateTo[2] + "-" + DateTo[1] + "-" + DateTo[0];

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " WHERE " + Key_EDate + ">=" + "'" + reformedDateFrom + "'" +
                " AND " + Key_EDate + "<=" + "'" + reformedDateTo + "'", null);


    }

    //return a cursor which contains the tuples of table expense order by the date
    public Cursor getExpensesFromNewestToOldest() {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " ORDER BY " + Key_EDate + " DESC"
                , null);
    }


    //return a cursor which contains all the tuples of table income
    public Cursor getCursorIncomes() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income,
                null);
    }

    //return a cursor which contains all the tuples of table income with Source = to parameter source
    public Cursor getIncomesBySource(String source) {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " WHERE " + Key_ISource + "=" + "'" + source + "'", null);

    }

    //return a cursor which contains the tuples of table income with Date equal to parameter date
    public Cursor getIncomeByDate(String date) {

        String dateTokens[] = date.split("-");
        String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " WHERE " + Key_IDate + " LIKE " + "'" + reformedDate + "'",
                null);
    }

    //return a cursor which contains the tuples of table income order by the date
    public Cursor getIncomeByNewestToOldest() {

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " ORDER BY " + Key_IDate + " DESC"
                , null);
    }

    public Cursor getIncomeByAmountOrder(boolean asc) {
        String order = " ASC";
        if (!asc) {
            order = " DESC";
        }
        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " ORDER BY " + Key_IAmount + order, null);

    }


    //return a cursor which contains the tuples of table income with date between of parameter date1 and parameter date2
    public Cursor getIncomeByDateToDate(String date1, String date2) {

        String DateFrom[] = date1.split("-");
        String DateTo[] = date2.split("-");
        String reformedDateFrom = DateFrom[2] + "-" + DateFrom[1] + "-" + DateFrom[0];
        String reformedDateTo = DateTo[2] + "-" + DateTo[1] + "-" + DateTo[0];

        return getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " WHERE " + Key_IDate + ">=" + "'" + reformedDateFrom + "'" +
                " AND " + Key_IDate + "<=" + "'" + reformedDateTo + "'", null);
    }


    public void UpdateExpense(ExpenseItem expense) {

        ContentValues values = new ContentValues();
        values.put(Key_ECategory, expense.getCategories());
        values.put(Key_EDate, expense.getDate());
        values.put(Key_EPrice, expense.getPrice());
        values.put(Key_ENotes, expense.getNotes());

        Log.i("Database Enter", expense.getId() + "");

        getReadableDatabase().update(Table_Expense, values, Key_EId + " = " + expense.getId(), null);

    }

    public void UpdateIncome(IncomeItem income) {
        ContentValues values = new ContentValues();
        values.put(Key_ISource, income.getSource());
        values.put(Key_IAmount, income.getAmount());
        values.put(Key_IDate, income.getDate());

        getReadableDatabase().update(Table_Income, values, Key_Iid + " = " + income.getId(), null);

    }

    public void deleteExpense(int id) {

        getReadableDatabase().delete(Table_Expense, Key_EId + "=" + id, null);

    }

    public void deleteIncome(int id) {

        getReadableDatabase().delete(Table_Income, Key_Iid + "=" + id, null);

    }

    public void deleteAllIncome() {
        getWritableDatabase().delete(Table_Income, null, null);
    }

    public void deleteAllExpense() {
        getWritableDatabase().delete(Table_Expense, null, null);
    }


    public void deleteTuplesDependedOnCategory(String category, boolean expense) {

        if (expense)
            getReadableDatabase().delete(Table_Expense, Key_ECategory + "=" + "'" + category + "'", null);
        else
            getReadableDatabase().delete(Table_Income, Key_ISource + "=" + "'" + category + "'", null);

    }

    public void updateTuplesDependedOnCategory(String category, boolean expense, String newCategory) {

        ContentValues values = new ContentValues();
        if (expense) {
            values.put(Key_ECategory, newCategory);
            getReadableDatabase().update(Table_Expense, values, Key_ECategory + "=" + "'" + category + "'", null);
        } else {
            values.put(Key_ISource, newCategory);
            getReadableDatabase().update(Table_Income, values, Key_ISource + "=" + "'" + category + "'", null);
        }


    }

    public boolean CategoryHasItems(String category, boolean expense) {

        Cursor c;

        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " WHERE " + Key_ECategory + "=" + "'" + category + "'", null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " WHERE " + Key_ISource + "=" + "'" + category + "'", null);
        }

        if (c.getCount() == 0) {
            return false;
        }

        return true;

    }

    public double getTotalExpensePriceForCurrentMonth() {

        double total = 0;
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;

        String month = currentMonth + "";
        if (currentMonth < 10) {
            month = "0" + currentMonth;
        }

        String firstOfMonth = "01" + "-" + month + "-" + c.get(Calendar.YEAR);
        String lastOfMonth = "31" + "-" + month + "-" + c.get(Calendar.YEAR);
        Log.i("firstOfMonth", firstOfMonth);
        Log.i("lastOfMonth", lastOfMonth);

        Cursor cursor = this.getExpensesByDateToDate(firstOfMonth, lastOfMonth);

        if (cursor.getCount() != 0) {
            Log.i("Database", "Bike");
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(3)) + total;
            }

        }

        return total;
    }

    public double getTotalIncomePriceForCurrentMonth() {

        double total = 0;
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        String month = currentMonth + "";
        if (currentMonth < 10) {
            month = "0" + currentMonth;
        }
        String firstOfMonth = "01" + "-" + month + "-" + c.get(Calendar.YEAR);
        String lastOfMonth = "31" + "-" + month + "-" + c.get(Calendar.YEAR);

        Cursor cursor = this.getIncomeByDateToDate(firstOfMonth, lastOfMonth);

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(1)) + total;
            }

        }

        return total;
    }

    public double getTotalExpenses() {

        double total = 0;

        Cursor cursor = this.getCursorExpense();

        if (cursor.getCount() != 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(3)) + total;
            }

        }

        return total;
    }

    public double getTotalIncome() {

        double total = 0;

        Cursor cursor = this.getCursorIncomes();

        if (cursor.getCount() != 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(1)) + total;
            }

        }

        return total;
    }

    public Cursor getLastExpense() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + Table_Expense + " ORDER BY " + Key_EDate + " DESC"
                , null);
        return c;

    }

    public Cursor getLastIncome() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + Table_Income + " ORDER BY " + Key_IDate + " DESC"
                , null);
        return c;
    }

    public double getTotalExpensePriceForCurrentWeek() {

        double total = 0;

        Calendar c = Calendar.getInstance();

        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int endDay = Calendar.SUNDAY;

        while (currentDay != endDay) {
            c.add(Calendar.DATE, 1);
            currentDay = c.get(Calendar.DAY_OF_WEEK);
        }
        Date endDate = c.getTime();

        c.add(Calendar.DAY_OF_YEAR, -6);
        Date startDate = c.getTime();


        c.setTime(startDate);

        String day = c.get(Calendar.DAY_OF_MONTH) + "";
        String month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        String firstOfWeek = day + "-" + month + "-" + c.get(Calendar.YEAR);
        c.setTime(endDate);

        day = c.get(Calendar.DAY_OF_MONTH) + "";
        month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        String lastOfWeek = day + "-" + month + "-" + c.get(Calendar.YEAR);

        Cursor cursor = this.getExpensesByDateToDate(firstOfWeek, lastOfWeek);

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(3)) + total;
            }

        }

        return total;

    }

    public double getTotalIncomePriceForCurrentWeek() {

        double total = 0;

        Calendar c = Calendar.getInstance();

        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int endDay = Calendar.SUNDAY;

        while (currentDay != endDay) {
            c.add(Calendar.DATE, 1);
            currentDay = c.get(Calendar.DAY_OF_WEEK);
        }
        Date endDate = c.getTime();

        c.add(Calendar.DAY_OF_YEAR, -6);
        Date startDate = c.getTime();


        c.setTime(startDate);

        String day = c.get(Calendar.DAY_OF_MONTH) + "";
        String month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        String firstOfWeek = day + "-" + month + "-" + c.get(Calendar.YEAR);
        c.setTime(endDate);

        day = c.get(Calendar.DAY_OF_MONTH) + "";
        month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        String lastOfWeek = day + "-" + month + "-" + c.get(Calendar.YEAR);

        Cursor cursor = this.getIncomeByDateToDate(firstOfWeek, lastOfWeek);

        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total = Double.parseDouble(cursor.getString(1)) + total;
            }

        }

        return total;
    }
}
