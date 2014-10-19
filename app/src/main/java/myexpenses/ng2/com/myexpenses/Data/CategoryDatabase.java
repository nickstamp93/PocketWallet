package myexpenses.ng2.com.myexpenses.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by Nikos on 9/23/2014.
 */
//database containing categories
public class CategoryDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "categories";

    private static final String TABLE_EXPENSE_CATEGORIES = "ECATEGORIES";
    private static final String TABLE_INCOME_CATEGORIES ="ICATEGORIES";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ENAME = "ename";
    private static final String COLUMN_INAME= "iname";

    private static final String CREATE_EXPENSE_CATEGORIES = "CREATE TABLE " + TABLE_EXPENSE_CATEGORIES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ENAME + " TEXT NOT NULL)";

    private static final String CREATE_INCOME_CATEGORIES =  "CREATE TABLE " + TABLE_INCOME_CATEGORIES + " (" +
    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_INAME + " TEXT NOT NULL)";


    public CategoryDatabase(Context context){

        super(context , DATABASE_NAME , null , DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXPENSE_CATEGORIES);
        db.execSQL(CREATE_INCOME_CATEGORIES);

        //insert the initial expense categories
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(1,'Food')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(2,'Drinks')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(3,'Personal')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(4,'Clothing')");
        //insert the initial income categories
        db.execSQL("insert into " + TABLE_INCOME_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_INAME + ") values(1,'Company')");
        db.execSQL("insert into " + TABLE_INCOME_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_INAME + ") values(2,'Other')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing for now
    }

    //insert a expense category given its name
    public void insertExpenseCategory(String name){
        ContentValues values=new ContentValues();

        values.put(COLUMN_ENAME , name);

        getWritableDatabase().insert(TABLE_EXPENSE_CATEGORIES, null, values);

    }

    //insert a income category given its name
    public void insertIncomeCategory(String name){
        ContentValues values=new ContentValues();

        values.put(COLUMN_INAME , name);

        getWritableDatabase().insert(TABLE_INCOME_CATEGORIES, null, values);

    }



    //delete a expense category by name
    public void deleteCategory(String name,boolean expense)
    {
        if(expense) {
            // Define 'where' part of query.
            String selection = COLUMN_ENAME + " LIKE ?";
            // Specify arguments
            String[] selectionArgs = {name};
            // execute
            getWritableDatabase().delete(TABLE_EXPENSE_CATEGORIES, selection, selectionArgs);
        }else{
            // Define 'where' part of query.
            String selection = COLUMN_INAME + " LIKE ?";
            // Specify arguments
            String[] selectionArgs = { name };
            // execute
            getWritableDatabase().delete(TABLE_INCOME_CATEGORIES, selection, selectionArgs);
        }
    }

    //return a cursor with all categories depended on variable expense
    public Cursor getAllCategories(boolean expense){
        if(expense) return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES , null);
          return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES , null);
    }

    //Get all the categories of database returned in a ArrayList
    public ArrayList<String> getExpenseCategories(){
        Cursor c=getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES , null);
        ArrayList<String> categories=new ArrayList<String>();

        int Catrow=c.getColumnIndex(COLUMN_ENAME);

        if(c!=null){

            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

                String category=c.getString(Catrow);
                categories.add(category);

            }

        }
        return categories;
    }

    public int getPositionFromValue(String category){

        int pos=0;
        Cursor c=getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_EXPENSE_CATEGORIES,null);
        if(c!=null){

            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                String cat=c.getString(1);
                if(cat.equals(category)){
                    break;
                }
                pos++;
            }
            c.close();

        }

        return pos;


    }

    public ArrayList<String> getAllCategoriesExceptOne(String category,boolean expense){

        ArrayList<String > categories=new ArrayList<String>();
        Cursor c;
        if(expense) {
             c = getReadableDatabase().rawQuery("SELECT * FROM "+TABLE_EXPENSE_CATEGORIES,null);
        }else{
            c = getReadableDatabase().rawQuery("SELECT * FROM "+TABLE_INCOME_CATEGORIES,null);

        }
        if(c!=null){
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                String cat=c.getString(1);
                if(!cat.equals(category)) {
                    categories.add(cat);
                }

            }
            c.close();
        }
        return categories;
    }

    public void closeDB(){
        getReadableDatabase().close();
    }

}
