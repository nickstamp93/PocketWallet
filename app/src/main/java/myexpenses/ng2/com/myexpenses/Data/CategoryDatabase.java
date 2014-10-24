package myexpenses.ng2.com.myexpenses.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import java.util.ArrayList;


/**
 * Created by Nikos on 9/23/2014.
 */
//database containing categories
public class CategoryDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "categories";

    private static final String TABLE_EXPENSE_CATEGORIES = "ECATEGORIES";
    private static final String TABLE_INCOME_CATEGORIES = "ICATEGORIES";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ENAME = "ename";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_LETTER = "letter";
    private static final String COLUMN_INAME = "iname";
    private static final String COLUMN_ICOLOR = "icolor";
    private static final String COLUMN_ILETTER = "letter";

    private static final String CREATE_EXPENSE_CATEGORIES = "CREATE TABLE " + TABLE_EXPENSE_CATEGORIES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ENAME + " TEXT NOT NULL," + COLUMN_COLOR + " INTEGER," + COLUMN_LETTER + " TEXT NOT NULL)";

    private static final String CREATE_INCOME_CATEGORIES = "CREATE TABLE " + TABLE_INCOME_CATEGORIES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_INAME + " TEXT NOT NULL," + COLUMN_ICOLOR + " INTEGER," + COLUMN_ILETTER + " TEXT NOT NULL)";

    public CategoryDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXPENSE_CATEGORIES);
        db.execSQL(CREATE_INCOME_CATEGORIES);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENAME, "Food");
        cv.put(COLUMN_COLOR, Color.RED);
        cv.put(COLUMN_LETTER, "F");
        db.insert(TABLE_EXPENSE_CATEGORIES, null, cv);

        cv.put(COLUMN_ENAME, "Drinks");
        cv.put(COLUMN_COLOR, Color.BLUE);
        cv.put(COLUMN_LETTER, "D");
        db.insert(TABLE_EXPENSE_CATEGORIES, null, cv);

        cv.put(COLUMN_ENAME, "Personal");
        cv.put(COLUMN_COLOR, Color.BLACK);
        cv.put(COLUMN_LETTER, "P");
        db.insert(TABLE_EXPENSE_CATEGORIES, null, cv);

        cv.put(COLUMN_ENAME, "Clothing");
        cv.put(COLUMN_COLOR, Color.YELLOW);
        cv.put(COLUMN_LETTER, "C");
        db.insert(TABLE_EXPENSE_CATEGORIES, null, cv);

        cv = new ContentValues();

        cv.put(COLUMN_INAME, "Company");
        cv.put(COLUMN_ICOLOR, Color.BLACK);
        cv.put(COLUMN_ILETTER, "C");
        db.insert(TABLE_INCOME_CATEGORIES, null, cv);

        cv.put(COLUMN_INAME, "External Source");
        cv.put(COLUMN_ICOLOR, Color.YELLOW);
        cv.put(COLUMN_ILETTER, "E");
        db.insert(TABLE_INCOME_CATEGORIES, null, cv);


        //insert the initial expense categories
      /*  db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME +COLUMN_COLOR+","+COLUMN_LETTER + ") values(1,'Food',"+red+",'F')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME +","+COLUMN_COLOR+","+COLUMN_LETTER +") values(2,'Drinks',Color.GREEN,'D')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(3,'Personal')");
        db.execSQL("insert into " + TABLE_EXPENSE_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_ENAME + ") values(4,'Clothing')");
        //insert the initial income categories
        db.execSQL("insert into " + TABLE_INCOME_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_INAME + ") values(1,'Company')");
        db.execSQL("insert into " + TABLE_INCOME_CATEGORIES + "(" + COLUMN_ID + ","
                + COLUMN_INAME + ") values(2,'Other')");
                */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing for now
    }

    //insert a expense category given its name
    public void insertCategory(String name, String letter, int color, boolean expense) {
        ContentValues values = new ContentValues();
        if (expense) {
            values.put(COLUMN_ENAME, name);
            values.put(COLUMN_LETTER, letter);
            values.put(COLUMN_COLOR, color);
            getWritableDatabase().insert(TABLE_EXPENSE_CATEGORIES, null, values);

        } else {
            values.put(COLUMN_INAME, name);
            values.put(COLUMN_ILETTER, letter);
            values.put(COLUMN_ICOLOR, color);
            getWritableDatabase().insert(TABLE_INCOME_CATEGORIES, null, values);

        }

    }


    //delete a expense category by name
    public void deleteCategory(String name, boolean expense) {
        if (expense) {
            // Define 'where' part of query.
            String selection = COLUMN_ENAME + " LIKE ?";
            // Specify arguments
            String[] selectionArgs = {name};
            // execute
            getWritableDatabase().delete(TABLE_EXPENSE_CATEGORIES, selection, selectionArgs);
        } else {
            // Define 'where' part of query.
            String selection = COLUMN_INAME + " LIKE ?";
            // Specify arguments
            String[] selectionArgs = {name};
            // execute
            getWritableDatabase().delete(TABLE_INCOME_CATEGORIES, selection, selectionArgs);
        }
    }

    //return a cursor with all categories depended on variable expense
    public Cursor getAllCategories(boolean expense) {
        if (expense)
            return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);
    }

    //Get all the categories of database returned in a ArrayList
    public ArrayList<String> getExpenseCategories() {
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        ArrayList<String> categories = new ArrayList<String>();

        int Catrow = c.getColumnIndex(COLUMN_ENAME);

        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                String category = c.getString(Catrow);
                categories.add(category);

            }

        }
        return categories;
    }

    public int getPositionFromValue(String category) {

        int pos = 0;
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String cat = c.getString(1);
                if (cat.equals(category)) {
                    break;
                }
                pos++;
            }
            c.close();

        }

        return pos;


    }

    public ArrayList<String> getAllCategoriesExceptOne(String category, boolean expense) {

        ArrayList<String> categories = new ArrayList<String>();
        Cursor c;
        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);

        }
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String cat = c.getString(1);
                if (!cat.equals(category)) {
                    categories.add(cat);
                }

            }
            c.close();
        }
        return categories;
    }

    public boolean checkIfNameExists(String name, boolean expense) {
        Cursor c;
        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);
        }

        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String catname = c.getString(1);
                if (catname.equals(name)) {
                    return true;
                }
            }
            c.close();

        }
        return false;
    }

    public boolean checkIfLetterAndColorExists(String letter, int color, boolean expense) {
        Cursor c;
        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);
        }

        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                String sletter = c.getString(3);
                int Catcolor = Integer.parseInt(c.getString(2));
                if (sletter.equals(letter) && Catcolor == color) {
                    return true;
                }
            }
            c.close();

        }
        return false;

    }

    public void updateCategory(int id, String name, String letter, int color, boolean expense) {

        ContentValues cv = new ContentValues();
        if (expense) {
            cv.put(COLUMN_ENAME, name);
            cv.put(COLUMN_COLOR, color);
            cv.put(COLUMN_LETTER, letter);
            getReadableDatabase().update(TABLE_EXPENSE_CATEGORIES, cv, COLUMN_ID + "=" + id, null);
        } else {
            cv.put(COLUMN_INAME, name);
            cv.put(COLUMN_ICOLOR, color);
            cv.put(COLUMN_ILETTER, letter);
            getReadableDatabase().update(TABLE_INCOME_CATEGORIES, cv, COLUMN_ID + "=" + id, null);
        }


    }

    public int getColorFromCategory(String name, boolean expense) {
        Cursor c;
        int color = -1;

        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);
        }

        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                int catColor = Integer.parseInt(c.getString(2));
                String catName = c.getString(1);
                if (catName.equals(name)) {
                    color = catColor;
                    break;
                }

            }
            c.close();
        }

        return color;

    }

    public char getLetterFromCategory(String name, boolean expense) {

        Cursor c;
        char letter = '0';

        if (expense) {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EXPENSE_CATEGORIES, null);
        } else {
            c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_INCOME_CATEGORIES, null);
        }

        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                String catLetter = c.getString(3);
                String catName = c.getString(1);
                if (catName.equals(name)) {
                    letter = catLetter.charAt(0);
                    break;
                }

            }
            c.close();
        }


        return letter;

    }


    public void closeDB() {
        getReadableDatabase().close();
    }

}
