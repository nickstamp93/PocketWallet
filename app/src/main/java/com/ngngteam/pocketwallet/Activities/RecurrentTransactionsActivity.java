package com.ngngteam.pocketwallet.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import me.grantland.widget.AutofitTextView;

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

        //on list item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                cursor.moveToPosition(pos);

                //launch the AddRecurrentActivity and pass the item clicked through the intent
                //to fill its values to the form
                Intent updateIntent = new Intent(RecurrentTransactionsActivity.this, AddRecurrentActivity.class);

                //create a copy of the selected transaction
                RecurrentTransaction item = new RecurrentTransaction(cursor);

                item.setId(cursor.getInt(0));
                updateIntent.putExtra("itemToUpdate", item);
                startActivity(updateIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //on resume , refresh the list for any changes made
        cursor.requery();
        adapter.notifyDataSetChanged();
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

            //get the item's views that are saved by the holder
            final Holder holder = (Holder) view.getTag();

            //create the item by cursor
            RecurrentTransaction item = new RecurrentTransaction(c);

            //fill views with its values
            holder.tvName.setText(item.getName());
            holder.tvCategory.setText(item.getCategory());
            holder.tvDays.setText(daysToEvent(item.getNextDate(), item.getExpiration(), item.getIsValid()));

            holder.tvAmount.setText(item.getAmount() + " "
                    + PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_key_currency), "€"));

            boolean isExpense = item.getIsExpense() == 1 ? true : false;
            holder.ivIcon.setLetter(cdb.getLetterFromCategory(item.getCategory(), isExpense));
            holder.ivIcon.setmBackgroundPaint(cdb.getColorFromCategory(item.getCategory(), isExpense));

        }

        //when adding a new item in the adapter , this method is called and creates a new row as in the row.xml
        @Override
        public View newView(Context context, final Cursor c, ViewGroup parent) {

            //create a new view for the item
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item_recurrent_transaction, parent, false);

            //and create the holder
            final Holder holder = new Holder();

            //init holder views
            holder.tvName = (AutofitTextView) row.findViewById(R.id.tvName);
            holder.tvCategory = (AutofitTextView) row.findViewById(R.id.tvCategory);
            holder.tvAmount = (TextView) row.findViewById(R.id.tvPrice);
            holder.tvDays = (AutofitTextView) row.findViewById(R.id.tvDaysLeft);
            holder.ivIcon = (LetterImageView) row.findViewById(R.id.livhistory);


            //create the item by cursor
            RecurrentTransaction item = new RecurrentTransaction(c);

            //fill views with item's values
            holder.tvName.setText(item.getName());
            holder.tvCategory.setText(item.getCategory());

            holder.tvDays.setText(daysToEvent(item.getNextDate(), item.getExpiration(), item.getIsValid()));
            holder.tvAmount.setText(item.getAmount() + " "
                    + PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_key_currency), "€"));

            boolean isExpense = item.getIsExpense() == 1 ? true : false;
            holder.ivIcon.setLetter(cdb.getLetterFromCategory(item.getCategory(), isExpense));
            holder.ivIcon.setmBackgroundPaint(cdb.getColorFromCategory(item.getCategory(), isExpense));

            row.setTag(holder);
            return row;
        }

        //holder class for the adapter
        class Holder {
            String id;
            AutofitTextView tvName, tvCategory, tvDays;
            TextView tvAmount;
            LetterImageView ivIcon;

        }
    }

    //method to return a string based on the days left between today and the next date of each item
    private String daysToEvent(String nextDate, String expiration, int isValid) {

        try {
            Date nDate = new SimpleDateFormat("yyyy-MM-dd").parse(nextDate);
            Date today = new Date(Calendar.getInstance().getTimeInMillis());

            int elapsedDays;

            Calendar cToday = Calendar.getInstance();
            Calendar cNextDate = Calendar.getInstance();
            cToday.setTime(today);
            cNextDate.setTime(nDate);


            int adj = 0;
            if (cNextDate.get(Calendar.YEAR) > cToday.get(Calendar.YEAR))
                adj = 365;
            elapsedDays = cNextDate.get(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR) + adj;

            String returnString = "";
            if (expiration != null) {
                if (expiration.split(":")[0].equalsIgnoreCase("count")) {
                    int event = Integer.valueOf(expiration.split(":")[1].split("/")[0]);
                    int total = Integer.valueOf(expiration.split(":")[1].split("/")[1]);
                    if (event == total) {
                        return "Completed " + event + "/" + total;
                    }
                    returnString = "Event " + (event + 1) + "/" + total + "\n";
                } else if (expiration.split(":")[0].equalsIgnoreCase("date")) {
                    if (isValid == 0) {
                        return "Completed";
                    }
                }
            }

            if (elapsedDays == 0) {
                return returnString + "Today";
            }
            if (elapsedDays == 1) {
                return returnString + "Tomorrow";
            }
            if (nDate.before(today)) {
                return -elapsedDays + " days ago";
            }

            return returnString + " in " + elapsedDays + " days";


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Not valid";
    }

}
