package com.ngngteam.pocketwallet.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ngngteam.pocketwallet.Model.RecurrentTransaction;

import java.net.IDN;

/**
 * Created by nickstamp on 7/17/2015.
 */
public class RecurrentDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RecurrentDatabase";

    private static final String TABLE_RECURRENT = "recurrent_transactions";

    //Table Recurrent Columns
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DATE = "date";
    private static final String KEY_FREQ = "frequency";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_DAY = "day";
    private static final String KEY_EXPIRATION = "expiration";
    private static final String KEY_ISEXPENSE = "isExpense";

    private SQLiteDatabase db;

    private static final String Create_Expense_Table = "CREATE TABLE IF NOT EXISTS " + TABLE_RECURRENT
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + " TEXT NOT NULL," + KEY_AMOUNT + " DOUBLE NOT NULL," + KEY_CATEGORY + " TEXT NOT NULL,"
            + KEY_DATE + " TEXT NOT NULL," + KEY_FREQ + " INTEGER NOT NULL," + KEY_INTERVAL + " TEXT NOT NULL,"
            + KEY_DAY + " TEXT," + KEY_EXPIRATION + " TEXT," + KEY_ISEXPENSE + " INTEGER);";

    private Context context;

    public RecurrentDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Create_Expense_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public void insert(RecurrentTransaction item) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_AMOUNT, item.getAmount());
        values.put(KEY_CATEGORY, item.getCategory());
        values.put(KEY_DATE, item.getDate());
        values.put(KEY_FREQ, item.getFreq());
        values.put(KEY_INTERVAL, item.getInterval());
        values.put(KEY_DAY, item.getDay());
        values.put(KEY_EXPIRATION, item.getExpiration());
        values.put(KEY_ISEXPENSE, item.getIsExpense());

        getWritableDatabase().insert(TABLE_RECURRENT, null, values);
    }
}
