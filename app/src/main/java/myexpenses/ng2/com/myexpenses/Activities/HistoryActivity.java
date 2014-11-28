package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Data.ExpenseItem;
import myexpenses.ng2.com.myexpenses.Data.IncomeItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.FiltersDateToDateActivity;
import myexpenses.ng2.com.myexpenses.Utils.HistoryListViewAdapter;


public class HistoryActivity extends FragmentActivity {

    private HistoryListViewAdapter adapter;
    private Cursor c;
    private MoneyDatabase db;
    private ListView lv;
    private AlertDialog dialog=null;
    private boolean switcher =true,update=false;
    private Menu menu;
    private CalendarDatePickerDialog Cdialog;

    private static int result=1;
    private Calendar calendar;




    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return super.startInstrumentation(className, profileFile, arguments);
    }

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



        setContentView(R.layout.activity_history);



        lv = (ListView) findViewById(R.id.lvHistory);

        db = new MoneyDatabase(getApplicationContext());
        c = db.getExpensesFromNewestToOldest();

        startManagingCursor(c);
        adapter = new HistoryListViewAdapter(getApplicationContext() , c);
        adapter.setTheView(true);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                c.moveToPosition(position);
                update=true;
                stopManagingCursor(c);

                if(switcher) {
                    Intent processExpense=new Intent(HistoryActivity.this,AddExpenseActivity.class);
                    ExpenseItem expense=new ExpenseItem(c.getString(1),c.getString(4),Double.parseDouble(c.getString(3)),c.getString(2));
                    expense.setId(Integer.parseInt(c.getString(0)));
                    processExpense.putExtra("Expense",expense);
                    startActivity(processExpense);
                }else{
                    Intent processIncome=new Intent(HistoryActivity.this,AddIncomeActivity.class);
                    IncomeItem income=new IncomeItem(Double.parseDouble(c.getString(1)),c.getString(3),c.getString(2));
                    income.setId(Integer.parseInt(c.getString(0)));
                    processIncome.putExtra("Income",income);
                    startActivity(processIncome);

                }



                Log.i("Listener",c.getString(1)+"-"+c.getString(2)+"-"+c.getString(3));



            }
        });

    }
// Refresh the view of HistoryActivity using different cursor
    public void refreshList(Cursor cursor){
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

//This method is called by FiltersDateDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
//the expenses in specific date(parameter)
 public void saveExpenseFiltersDate(String date){
     c=db.getExpensesByDate(date);
     refreshList(c);
 }


 //This method is called by FiltersDateDialog\ when the dialog is about to close and set the cursor of HistoryActivity to be all
//the incomes in specific date(parameter)
 public void saveIncomeFiltersDate(String date){
     c=db.getIncomeByDate(date);
     refreshList(c);
 }

//This method is called by FiltersDateToDateDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
//the expenses with date between parameters from and to
 public void saveFiltersDateToDate(String from,String to){
     c=db.getExpensesByDateToDate(from,to);
     refreshList(c);
 }

//This method is called by FiltersDateToDateDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
//the incomes with date between parameters from and to
 public void saveIncomeFiltersDateToDate(String from,String to){
     c=db.getIncomeByDateToDate(from,to);
     refreshList(c);
 }
//Dismiss the field Dialog dialog
    public void dismissDialog(){
        dialog.dismiss();

    }
//We keep in a variable the actionBar Menu so we can process it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.history_expense, this.menu);

        return true;
    }


//All the menu items with their listeners. More specific the drop down menu for filtering expenses and incomes and also
//the toggle button to change view in HistoryActivity between the expenses and incomes.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        switch (id){

            case R.id.action_settings:
                return true;



            case R.id.FiltersCategory:
                final String categories[]=getResources().getStringArray(R.array.expenses_category);
                CharSequence[] items=new CharSequence[categories.length];
                for (int i=0; i<categories.length; i++){
                    items[i]=categories[i];
                }
                builder.setTitle("Choose a Category");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {


                        String chosenCategory = categories[item];
                        c = db.getExpensesByCategory(chosenCategory);
                        refreshList(c);
                        dialogInterface.dismiss();
                        dismissDialog();


                    }
                });
                dialog=builder.create();
                dialog.show();
                break;

            case R.id.OrderPrice:
                final CharSequence[] orderItems={"Ascending","Declining"};
                builder.setTitle("Order of items depended on price");
                builder.setSingleChoiceItems(orderItems,-1,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc=false;
                        if(pos==0){
                            asc=true;
                        }
                        c=db.getExpensesByPriceOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                dialog=builder.create();
                dialog.show();
                break;

            case R.id.FiltersDate:
               calendar = Calendar.getInstance();
               Cdialog = CalendarDatePickerDialog.newInstance(dListener ,
                        calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(),"Date Filter");
               // FiltersDateDialog DDialog=new FiltersDateDialog(true,true);
                //DDialog.show(getFragmentManager(),"FiltersDate Dialog");
                break;

            case R.id.FiltersDTD:
               // FiltersDateToDateDialog DTDDialog=new FiltersDateToDateDialog(true);
                //DTDDialog.show(getFragmentManager(),"FiltersDateToDaTE Dialog");

                stopManagingCursor(c);
                Intent ExpenseFilterDTD=new Intent(HistoryActivity.this,FiltersDateToDateActivity.class);
                startActivityForResult(ExpenseFilterDTD,result);
                break;

            case R.id.FiltersNTO:
                c=db.getExpensesFromNewestToOldest();
                refreshList(c);
                break;


            case R.id.IncomeSource:

                final String sources[]={"Company","External Source"};
                CharSequence [] Csources=new CharSequence[sources.length];
                for(int i=0; i<sources.length; i++){
                    Csources[i]=sources[i];
                }
                builder.setTitle("Choose a Source");

                builder.setItems(Csources,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String chosenCategory= sources[i];
                        c=db.getIncomesBySource(chosenCategory);
                        refreshList(c);
                        dialogInterface.dismiss();
                    }
                });

                dialog=builder.create();
                dialog.show();
                break;
                /*
                final EditText source=new EditText(this);
                source.setHint("Source");
                source.setSingleLine(true);

                LinearLayout ll=new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                ll.addView(source);
                builder.setTitle("Write the source of the income");
                builder.setView(ll);
                builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String incomeSource;
                        incomeSource = source.getText().toString();
                        c = db.getIncomesBySource(incomeSource);
                        refreshList(c);
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialog=builder.create();
                dialog.show();
                break;*/

            case R.id.IncomeDate:
              //  FiltersDateDialog dDialog=new FiltersDateDialog(true,false);
               // dDialog.show(getFragmentManager(),"Date Income Filters Dialog");
                calendar = Calendar.getInstance();
                Cdialog = CalendarDatePickerDialog.newInstance(dListener ,
                      calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(),"Date Filter");

                break;

            case R.id.IncomeDTD:
                stopManagingCursor(c);
                Intent IncomeFilterDTD=new Intent(HistoryActivity.this,FiltersDateToDateActivity.class);
                startActivityForResult(IncomeFilterDTD,result);
              //  FiltersDateToDateDialog dtdDialog=new FiltersDateToDateDialog(false);
                //dtdDialog.show(getFragmentManager(),"Date to Date Income Filters Dialog");
                break;

            case R.id.IncomeNTO:
                c=db.getIncomeByNewestToOldest();
                refreshList(c);
                break;

            case R.id.IncomeAmount:
                final CharSequence[] Items={"Ascending","Declining"};
                builder.setTitle("Order of items depended on price");
                builder.setSingleChoiceItems(Items,-1,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc=false;
                        if(pos==0){
                            asc=true;
                        }
                        c=db.getIncomeByAmountOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                dialog=builder.create();
                dialog.show();

                break;

           //When the toggleButton is pressed we check the boolean variable switcher.If it is true it means that we are in
           //Expenses so we change the variable switcher we clear the menu and add the Income Menu and the standar cursor
           //for the income items. To do that we need to create new adapter and set the ListView to have this adapter. The
           //same thing happens when we are in Incomes. Finally we use 2 menu xml history_expense - history_income to change
           //the menus between Expenses and Incomes, in both menu there is a item with the same id (toggleButton)
            case R.id.toggleButton:

                if(switcher){
                    switcher=false;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_income, menu);
                    c=db.getIncomeByNewestToOldest();
                    adapter.closeCDB();
                    adapter=new HistoryListViewAdapter(getApplicationContext(),c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);

                }else{
                    switcher=true;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_expense,menu);
                    c=db.getExpensesFromNewestToOldest();
                    adapter.closeCDB();
                    adapter=new HistoryListViewAdapter(getApplicationContext(),c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);

                }



        }


        return super.onOptionsItemSelected(item);
    }

     private CalendarDatePickerDialog.OnDateSetListener dListener=new CalendarDatePickerDialog.OnDateSetListener() {
         @Override
         public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
             String month,day,date;
             if(i2<10){
                 month = "0" + i2;
             }else{
                 month = String.valueOf(i2);
             }
             if(i3<10){
                 day = "0" + i3;
             }else{
                 day = String.valueOf(i3);
             }
             date = day+"-"+month+"-"+i;
             if(switcher){
                 saveExpenseFiltersDate(date);

             }else{
               saveIncomeFiltersDate(date);
             }

         }
     };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==result){
            if(resultCode==Activity.RESULT_OK){

                String from=data.getStringExtra("From");
                String to=data.getStringExtra("To");

                if(switcher){

                  saveFiltersDateToDate(from,to);
                }else {
                    saveIncomeFiltersDateToDate(from,to);
                }


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(update){
            Log.i("Resume","requery");
            update=false;
            c.requery();
        }
    }

}
