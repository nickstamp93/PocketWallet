package com.ngngteam.pocketwallet.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecurrentTransactionsActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton fab;

    MoneyDatabase db;
    CategoryDatabase cdb;
    CustomCursorAdapter adapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recurrent_transactions);


        init();

        initUI();

        setUpUI();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void init() {
        db = new MoneyDatabase(this);
        cdb = new CategoryDatabase(this);
        cursor = db.getAllRecurrents();

        adapter = new CustomCursorAdapter(this, cursor);


    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.lvRecurrentTransactions);
        listView.setAdapter(adapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(listView);
    }

    private void setUpUI() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecurrentTransactionsActivity.this, AddRecurrentActivity.class));
            }
        });

        //and set a OnItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                cursor.moveToPosition(pos);

                Intent updateIntent = new Intent(RecurrentTransactionsActivity.this, AddRecurrentActivity.class);
                //put category attributes to the intent
                RecurrentTransaction item = new RecurrentTransaction(cursor.getString(1), cursor.getDouble(2)
                        , cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6)
                        , cursor.getString(7), cursor.getString(8), cursor.getInt(9));

                item.setId(cursor.getInt(0));
                updateIntent.putExtra("item", item);
                startActivity(updateIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor.requery();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recurrent_transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //inner class , implementation of our custom cursor adapter
    class CustomCursorAdapter extends CursorAdapter {

        //constructor
        public CustomCursorAdapter(Context context, Cursor c) {
            super(context, c);

        }

        //this method is called mainly when a row is changing position on the screen (e.g scrolling)
        //because when you're scrolling a view that wasn't on the screen and it is now , has to show its data
        @Override
        public void bindView(View view, Context context, Cursor c) {

            final Holder holder = (Holder) view.getTag();

            RecurrentTransaction item = new RecurrentTransaction(c);

            holder.tvName.setText(item.getName());
            holder.tvCategory.setText(item.getCategory());
            holder.tvDays.setText(daysToEvent(item.getFreq(), item.getInterval(), item.getDate(), item.getExpiration(), item.getDay()));
            holder.tvAmount.setText(item.getAmount() + " "
                    + PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_key_currency), "€"));

            boolean isExpense = item.getIsExpense() == 1 ? true : false;
            holder.ivIcon.setLetter(cdb.getLetterFromCategory(item.getCategory(), isExpense));
            holder.ivIcon.setmBackgroundPaint(cdb.getColorFromCategory(item.getCategory(), isExpense));

        }

        //when adding a new item in the adapter , this method is called and creates a new row as in the row.xml
        @Override
        public View newView(Context context, final Cursor c, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item_recurrent_transaction, parent, false);

            final Holder holder = new Holder();

            holder.tvName = (TextView) row.findViewById(R.id.tvName);
            holder.tvCategory = (TextView) row.findViewById(R.id.tvCategory);
            holder.tvAmount = (TextView) row.findViewById(R.id.tvPrice);
            holder.tvDays = (TextView) row.findViewById(R.id.tvDaysLeft);
            holder.ivIcon = (LetterImageView) row.findViewById(R.id.livhistory);


            RecurrentTransaction item = new RecurrentTransaction(c);

            holder.tvName.setText(item.getName());
            holder.tvCategory.setText(item.getCategory());

            holder.tvDays.setText(daysToEvent(item.getFreq(), item.getInterval(), item.getDate(), item.getExpiration(), item.getDay()));
            holder.tvAmount.setText(item.getAmount() + " "
                    + PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_key_currency), "€"));

            boolean isExpense = item.getIsExpense() == 1 ? true : false;
            holder.ivIcon.setLetter(cdb.getLetterFromCategory(item.getCategory(), isExpense));
            holder.ivIcon.setmBackgroundPaint(cdb.getColorFromCategory(item.getCategory(), isExpense));

            row.setTag(holder);
            return row;
        }

        class Holder {
            String id;
            TextView tvName, tvCategory, tvDays, tvAmount;
            LetterImageView ivIcon;

        }
    }


    private String daysToEvent(int freq, int interval, String date, String expiration, String day) {
        int freqVar = Calendar.MONTH;
        switch (freq) {
            //daily
            case 0:
                freqVar = Calendar.DAY_OF_YEAR;
                break;
            //weekly
            case 1:
                freqVar = Calendar.WEEK_OF_YEAR;
                break;
            //monthly
            case 2:
                freqVar = Calendar.MONTH;
                break;
            //yearly
            case 3:
                freqVar = Calendar.YEAR;
                break;
        }
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date today = new Date(Calendar.getInstance().getTimeInMillis());

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);

            while (startDate.before(today)) {
                c.add(freqVar, interval);
                startDate = c.getTime();
            }

            //milliseconds
            long different = startDate.getTime() - today.getTime();


            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;

            if (elapsedDays == 0)
                return "Today";
            if (elapsedDays == 1)
                return "Tomorrow";
            return "due to " + elapsedDays + " days";


        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i("kwstas", "end with freq " + freq);
        Log.i("kwstas", "end with freqvar " + freqVar);
        return "0";
    }

}
