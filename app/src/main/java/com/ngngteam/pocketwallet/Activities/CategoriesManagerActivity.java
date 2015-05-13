package com.ngngteam.pocketwallet.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.R;

//activity managind the categories
public class CategoriesManagerActivity extends AppCompatActivity {

    //listview
    private ListView lv;
    //cursor
    private Cursor c;
    //database
    private CategoryDatabase db;
    //adapter
    private MyAdapter adapter;

    private boolean expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Themer.setThemeToActivity(this);
        setContentView(R.layout.activity_categories_manager);

        //init variables
        init();

        //init views
        initUI();

        //set up listeners , adapter etc
        setUpUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    private void initUI() {
        //init lv
        lv = (ListView) findViewById(R.id.lvCategories);

    }

    private void init() {

        expense = true;

        db = new CategoryDatabase(CategoriesManagerActivity.this);

        c = db.getAllCategories(expense);
        //init adapter
        adapter = new MyAdapter(CategoriesManagerActivity.this, c);

    }


    private void setUpUI() {
        //set the adapter to the listview
        lv.setAdapter(adapter);

        //and set a OnItemClickListener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                c.moveToPosition(pos);

                Intent updateCat = new Intent(CategoriesManagerActivity.this, CreateCategoryActivity.class);
                //put category attributes to the intent
                updateCat.putExtra("Name", c.getString(1));
                updateCat.putExtra("Color", Integer.parseInt(c.getString(2)));
                updateCat.putExtra("Letter", c.getString(3));
                updateCat.putExtra("Id", Integer.parseInt(c.getString(0)));
                updateCat.putExtra("Expense", expense);
                startActivity(updateCat);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categories_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_addCategory) {
            //on action bar add category , launch add category dialog
            Intent CreateCategory = new Intent(CategoriesManagerActivity.this, CreateCategoryActivity.class);
            CreateCategory.putExtra("Expense", expense);
            startActivity(CreateCategory);

        } else if (id == R.id.action_switcher) {
            //switch between income-expense categories
            if (expense) {
                expense = false;
                item.setTitle(getResources().getString(R.string.action_expense));
                c = db.getAllCategories(expense);
                refreshList(c);
            } else {
                expense = true;
                item.setTitle(getResources().getString(R.string.action_income));
                c = db.getAllCategories(expense);
                refreshList(c);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //refresh list items (categories)
    public void refreshList(Cursor c) {
        adapter.changeCursor(c);
        adapter.notifyDataSetChanged();

    }

    private class MyAdapter extends CursorAdapter {

        //layout inflater
        private LayoutInflater inflater;

        //constructor
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {

            //create a new view from the 'list_item_categories.xml'
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.list_item_categories, parent, false);

            //init UI elements from this particular view
            LetterImageView liv = (LetterImageView) newView.findViewById(R.id.livcat);
            TextView tvName = (TextView) newView.findViewById(R.id.tvName);

            //and fill them with the correct values from the db's cursor
            final String name = cursor.getString(1);
            tvName.setText(name);

            String sletter = cursor.getString(3);
            char cletter = sletter.charAt(0);
            int color = Integer.parseInt(cursor.getString(2));

            liv.setOval(true);
            liv.setmBackgroundPaint(color);
            liv.setLetter(cletter);

            //return the newly created view
            return newView;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            //init the view's UI elements
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livcat);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);

            //and update them with the correct values
            final String name = cursor.getString(1);
            tvName.setText(name);

            String sletter = cursor.getString(3);
            char cletter = sletter.charAt(0);
            int color = Integer.parseInt(cursor.getString(2));

            liv.setOval(true);
            liv.setmBackgroundPaint(color);
            liv.setLetter(cletter);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        c.requery();
        adapter.notifyDataSetChanged();
    }
}
