package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.AddCategoryDialog;
import myexpenses.ng2.com.myexpenses.Utils.DeleteCategoryDialog;
import myexpenses.ng2.com.myexpenses.Utils.LetterImageView;

//activity managind the categories
public class CategoriesManagerActivity extends Activity {

    //listview
    private ListView lv;
    //cursor
    private Cursor c;
    //database
    private CategoryDatabase db;
    //adapter
    private MyAdapter adapter;

    private boolean expense;
    private boolean delete;//there was a problem when you delete a category because on Listener we move the cursor but the variable c is on
    //the old cursor so we need it to refresh him (Difficult problem Solved)
    private DeleteCategoryDialog deleteCatDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getInt("pref_key_theme" , getResources().getColor(R.color.black))==getResources().getColor(R.color.Fuchsia)){
            setTheme(R.style.AppThemeFuchsia);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.black))) {
            setTheme(R.style.AppThemeBlack);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.green))) {
            setTheme(R.style.AppThemeGreen);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.Orange))) {
            setTheme(R.style.AppThemeOrange);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.teal))) {
            setTheme(R.style.AppThemeTeal);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.white))) {
            setTheme(R.style.AppThemeWhite);
        }
        setContentView(R.layout.activity_categories_manager);

        //init db
        db = new CategoryDatabase(getApplicationContext());

        expense=true;
       // delete=false;

        //init cursor
        c = db.getAllCategories(expense);


        //init lv
        lv = (ListView) findViewById(R.id.lvCategories);

        //init adapter
        adapter = new MyAdapter(getApplicationContext() , c,true);

        //set the adapter to the listview
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*
                if(delete){
                    refreshList(c=db.getAllCategories(expense));
                }*/
                   c.moveToPosition(i);

                    Intent updateCat=new Intent(CategoriesManagerActivity.this,CreateCategoryActivity.class);
                    updateCat.putExtra("Name",c.getString(1));
                    updateCat.putExtra("Color",Integer.parseInt(c.getString(2)));
                    updateCat.putExtra("Letter",c.getString(3));
                    updateCat.putExtra("Id",Integer.parseInt(c.getString(0)));
                    updateCat.putExtra("Expense",expense);
                    startActivity(updateCat);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  this.menu=menu;
        getMenuInflater().inflate(R.menu.categories_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       //TODO need to work the dialog in difference case between income and expense categories
        if (id == R.id.action_addCategory) {
            //on action bar add category , launch add category dialog
          //  new AddCategoryDialog().show(getFragmentManager() , "Add Category");
            Intent CreateCategory=new Intent(CategoriesManagerActivity.this,CreateCategoryActivity.class);
            CreateCategory.putExtra("Expense",expense);
            startActivity(CreateCategory);

        }else if(id==R.id.action_switcher){
            if(expense){
                expense=false;
                item.setTitle("EXPENSE");
                adapter.setExpense(expense);
                c=db.getAllCategories(expense);
                refreshList(c);
            }else{
                expense=true;
                item.setTitle("INCOME");
                adapter.setExpense(expense);
                c=db.getAllCategories(expense);
                refreshList(c);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //refresh listview
    public void refreshList(Cursor c){
        adapter.changeCursor(c);
        adapter.notifyDataSetChanged();

    }
/*
    public void deleteHappen(){
        delete=true;
    }
*/
    private class MyAdapter extends CursorAdapter{

        //layout inflater
         private LayoutInflater inflater;
         private boolean expense;

        //constructor
        public MyAdapter(Context context , Cursor c,boolean expense){
            super(context , c);
            this.expense=expense;
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {
            //create a new view from the 'list_item_categories.xml'
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.list_item_categories , parent , false);

            //init UI elements from this particular view
           // ImageView ivIcon = (ImageView) newView.findViewById(R.id.ivIcon);
            LetterImageView liv=(LetterImageView) newView.findViewById(R.id.livcat);
            TextView tvName = (TextView) newView.findViewById(R.id.tvName);
          //  ImageButton ibDelete = (ImageButton) newView.findViewById(R.id.ibDelete);

            //and fill them with the correct values from the db's cursor
            final String name = cursor.getString(1);
            tvName.setText(name);

            String sletter=cursor.getString(3);
            char cletter=sletter.charAt(0);
            int color=Integer.parseInt(cursor.getString(2));

            liv.setOval(true);
            liv.setmBackgroundPaint(color);
            liv.setLetter(cletter);


/*
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                   deleteCatDialog=new DeleteCategoryDialog(name,expense);
                   deleteCatDialog.show(getFragmentManager(),"Delete");

                    cursor.requery();
                    notifyDataSetChanged();
                }
            });
            */
/*
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
            */

            //return the newly created view
            return newView;
        }

        @Override
        public void bindView(View view, Context context,final Cursor cursor) {
            //init the view's UI elements
          // ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            LetterImageView liv=(LetterImageView) view.findViewById(R.id.livcat);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            //ImageButton ibDelete = (ImageButton) view.findViewById(R.id.ibDelete);


            //and update them with the correct values
            final String name = cursor.getString(1);
            tvName.setText(name);


            String sletter=cursor.getString(3);
            char cletter=sletter.charAt(0);
            int color=Integer.parseInt(cursor.getString(2));

            liv.setOval(true);
            liv.setmBackgroundPaint(color);
            liv.setLetter(cletter);

/*
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteCatDialog=new DeleteCategoryDialog(name,expense);
                    deleteCatDialog.show(getFragmentManager(),"Delete");

                    cursor.requery();
                    notifyDataSetChanged();
                }
            });
            */
/*
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
            */
        }
        public void setExpense(boolean expense){
            this.expense=expense;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        c.requery();
        adapter.notifyDataSetChanged();
    }
}
