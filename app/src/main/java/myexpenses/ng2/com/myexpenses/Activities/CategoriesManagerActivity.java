package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.MainActivity;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.AddCategoryDialog;

//activity managind the categories
public class CategoriesManagerActivity extends Activity {

    //listview
    ListView lv;
    //cursor
    Cursor c;
    //database
    CategoryDatabase db;
    //adapter
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_manager);

        //init db
        db = new CategoryDatabase(getApplicationContext());

        //init cursor
        c = db.getAllCategories();

        //init lv
        lv = (ListView) findViewById(R.id.lvCategories);

        //init adapter
        adapter = new MyAdapter(getApplicationContext() , c);

        //set the adapter to the listview
        lv.setAdapter(adapter);

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
            new AddCategoryDialog().show(getFragmentManager() , "Add Category");
        }

        return super.onOptionsItemSelected(item);
    }

    //refresh listview
    public void refreshList(){
        c.requery();
        adapter.notifyDataSetChanged();

    }

    private class MyAdapter extends CursorAdapter{

        //layout inflater
        LayoutInflater inflater;

        //constructor
        public MyAdapter(Context context , Cursor c){
            super(context , c);
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {
            //create a new view from the 'list_item_categories.xml'
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.list_item_categories , parent , false);

            //init UI elements from this particular view
            ImageView ivIcon = (ImageView) newView.findViewById(R.id.ivIcon);
            TextView tvName = (TextView) newView.findViewById(R.id.tvName);
            ImageButton ibDelete = (ImageButton) newView.findViewById(R.id.ibDelete);

            //and fill them with the correct values from the db's cursor
            final String name = cursor.getString(1);
            tvName.setText(name);

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteCategory(name);
                    cursor.requery();
                    notifyDataSetChanged();
                }
            });

            //set the correct icon for each category
            if(name.equals("Food")){
                ivIcon.setImageResource(R.drawable.food);
            }else if(name.equals("Drinks")){
                ivIcon.setImageResource(R.drawable.drinks);
            }else if(name.equals("Personal")){
                ivIcon.setImageResource(R.drawable.personal);
            }else if(name.equals("Clothing")){
                ivIcon.setImageResource(R.drawable.clothing);
            }

            //return the newly created view
            return newView;
        }

        @Override
        public void bindView(View view, Context context,final Cursor cursor) {
            //init the view's UI elements
            ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            ImageButton ibDelete = (ImageButton) view.findViewById(R.id.ibDelete);

            //and update them with the correct values
            final String name = cursor.getString(1);
            tvName.setText(name);

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteCategory(name);
                    cursor.requery();
                    notifyDataSetChanged();
                }
            });

            //and the right icon
            if(name.equals("Food")){
                ivIcon.setImageResource(R.drawable.food);
            }else if(name.equals("Drinks")){
                ivIcon.setImageResource(R.drawable.drinks);
            }else if(name.equals("Personal")){
                ivIcon.setImageResource(R.drawable.personal);
            }else if(name.equals("Clothing")){
                ivIcon.setImageResource(R.drawable.clothing);
            }
        }
    }

}
