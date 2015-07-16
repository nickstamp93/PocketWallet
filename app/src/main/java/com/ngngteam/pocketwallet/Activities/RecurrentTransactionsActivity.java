package com.ngngteam.pocketwallet.Activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

public class RecurrentTransactionsActivity extends ActionBarActivity {

    ListView listView;
    FloatingActionButton fab;

    MoneyDatabase db;
    CustomCursorAdapter adapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recurrent_transactions);


        init();

        initUI();

    }

    private void init() {
        db = new MoneyDatabase(this);
        cursor = db.getAllIncomes();

        adapter = new CustomCursorAdapter(this, cursor);


    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.lvRecurrentTransactions);
        listView.setAdapter(adapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(listView);
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
        public void bindView(View view, Context context, Cursor cursor) {

            final Holder holder = (Holder) view.getTag();


            holder.tvName.setText("Bank debt");
            holder.tvCategory.setText("Other");
            holder.tvAmount.setText("200 $");
            holder.tvDays.setText("Due in 4 days");
            holder.tvNotes.setText("these are some notes");
            holder.ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));

        }

        //when adding a new item in the adapter , this method is called and creates a new row as in the row.xml
        @Override
        public View newView(Context context, final Cursor c, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item_recurrent_transaction, parent, false);

            final Holder holder = new Holder();

            holder.tvName = (TextView) row.findViewById(R.id.tvName);
            holder.tvCategory = (TextView) row.findViewById(R.id.tvCategory);
            holder.tvNotes = (TextView) row.findViewById(R.id.tvNotes);
            holder.tvAmount = (TextView) row.findViewById(R.id.tvPrice);
            holder.tvDays = (TextView) row.findViewById(R.id.tvDate);
            holder.ivIcon = (LetterImageView) row.findViewById(R.id.livhistory);


            holder.tvName.setText("Bank debt");
            holder.tvCategory.setText("Other");
            holder.tvAmount.setText("200 $");
            holder.tvDays.setText("Due in 4 days");
            holder.tvNotes.setText("these are some notes");
            holder.ivIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));


            row.setTag(holder);
            return row;
        }

        class Holder {
            String id;
            TextView tvName, tvCategory, tvDays, tvNotes, tvAmount;
            ImageView ivIcon;

        }
    }

}
