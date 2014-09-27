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

    private static final String TABLE_CATEGORIES = "CATEGORIES";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";

    private static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT NOT NULL)";


    public CategoryDatabase(Context context){

        super(context , DATABASE_NAME , null , DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);

        //insert the initial categories
        db.execSQL("insert into " + TABLE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_NAME + ") values(1,'Food')");
        db.execSQL("insert into " + TABLE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_NAME + ") values(2,'Drinks')");
        db.execSQL("insert into " + TABLE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_NAME + ") values(3,'Personal')");
        db.execSQL("insert into " + TABLE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_NAME + ") values(4,'Clothing')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing for now
    }

    //insert a category given its name
    public void insertCategory(String name){
        ContentValues values=new ContentValues();

        values.put(COLUMN_NAME , name);

        getWritableDatabase().insert(TABLE_CATEGORIES, null, values);

    }

    //delete a category by name
    public void deleteCategory(String name)
    {
        // Define 'where' part of query.
        String selection = COLUMN_NAME + " LIKE ?";
        // Specify arguments
        String[] selectionArgs = { name };
        // execute
        getWritableDatabase().delete(TABLE_CATEGORIES, selection, selectionArgs);
    }

    //return a cursor with all categories
    public Cursor getAllCategories(){
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_CATEGORIES , null);
    }

    //Get all the categories of database returned in a ArrayList
    public ArrayList<String> getCategories(){
        Cursor c=getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_CATEGORIES , null);
        ArrayList<String> categories=new ArrayList<String>();

        int Catrow=c.getColumnIndex(COLUMN_NAME);

        if(c!=null){

            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

                String category=c.getString(Catrow);
                categories.add(category);

            }

        }
        return categories;
    }

    public void closeDB(){
        getReadableDatabase().close();
    }

}
