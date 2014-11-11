package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.IncomeItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CalendarDialog;
import myexpenses.ng2.com.myexpenses.Utils.SpinnerAdapter;

public class AddIncomeActivity extends FragmentActivity {

    private EditText etAmount,etSource;
    private Spinner sCategories;
    private ImageButton ibCalendar;
    private Button bOk,bCancel;
    private MoneyDatabase db;
    private CategoryDatabase cdb;
    private IncomeItem income;
    //private CalendarDialog dialog;
    private String date;
    private boolean update;
    private int id;

    private Calendar c;
    private CalendarDatePickerDialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        initBasicVariables();
        initUI();
        initListeners();

        income=(IncomeItem)getIntent().getSerializableExtra("Income");
        if(income!=null){
            update=true;
            id=income.getId();
            initUiValues();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.add_income, menu);

        if(update){
            MenuItem delete=menu.add("Delete").setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.i("MenuItem","activated");
                    db.deleteIncome(income.getId());
                    db.close();
                    finish();
                    return false;
                }
            });
        }


        return true;
    }
    private void initUiValues() {
        cdb = new CategoryDatabase(AddIncomeActivity.this);
        etAmount.setText(income.getAmount() + "");
        sCategories.setSelection(cdb.getPositionFromValue(income.getSource(),false));
        cdb.close();

        String tokens[]=income.getDate().split("-");
        int day=Integer.parseInt(tokens[2]);
        int month=Integer.parseInt(tokens[1]);
        int year=Integer.parseInt(tokens[0]);

        d= CalendarDatePickerDialog.newInstance(listener ,
                year , month , day);
        date=year+"-"+month+"-"+day;

    }

    private void initBasicVariables(){
        db=new MoneyDatabase(AddIncomeActivity.this);
        try {
            db.openDatabase();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Problem with our database", Toast.LENGTH_SHORT).show();
        }
        cdb=new CategoryDatabase(getApplicationContext());

        update=false;

        c = Calendar.getInstance();
        d = CalendarDatePickerDialog.newInstance(listener ,
                c.get(Calendar.YEAR) , c.get(Calendar.MONTH) , c.get(Calendar.DAY_OF_MONTH));

        String day=c.get(Calendar.DAY_OF_MONTH)+"";
        String month=c.get(Calendar.MONTH)+"";
        if(c.get(Calendar.DAY_OF_MONTH)<10){
            day = "0" +c.get(Calendar.DAY_OF_MONTH) ;
        }
        if(c.get(Calendar.MONTH)<10){
            month = "0" +c.get(Calendar.MONTH) ;
        }
/*
        Time now=new Time();
        now.setToNow();
        String day=now.monthDay+"",month=now.month+"";

        if(now.monthDay<10){
            day="0"+now.monthDay;
        }
        if(now.month<10){
            month="0"+now.month;
        }
        */
        date = c.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    private void initUI(){

       etAmount=(EditText)findViewById(R.id.etiamount);
      // etSource=(EditText)findViewById(R.id.etisource);
       sCategories=(Spinner) findViewById(R.id.sIncomeCategories);
       ibCalendar =(ImageButton) findViewById(R.id.ibiCalendar);
       bOk=(Button)findViewById(R.id.bOK);
       bCancel=(Button)findViewById(R.id.bCancel);

        //get from CategoryDatabase all the categories and save them in to an ArrayList
        ArrayList<String> allCategories = cdb.getExpenseCategories(false);
        //Initialize the SpinnerAdapter
      //  SpinnerAdapter adapter = new SpinnerAdapter(AddIncomeActivity.this, allCategories);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
        //sCategories.setAdapter(adapter);
        cdb.close();
    }


    private void initListeners(){

       bOk.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           boolean ok=true;
           double amount=0;
           String source;
          //get the price of the income if it has problem a Toast appear and say to correct it
           try{
               amount=Double.parseDouble(etAmount.getText().toString());
           }catch (NumberFormatException e){
               ok=false;
               Toast.makeText(getApplicationContext(),"Plz Press a numerical in Price and not a character",Toast.LENGTH_LONG).show();
           }
            //if we took the price correctly we continue to retrieve the other information of the income item
           if(ok){

               source=sCategories.getSelectedItem().toString();

           //then we add the income to our database we close it and we finish the activity
               income=new IncomeItem(amount,date,source);
               if(!update) {
                   db.InsertIncome(income);

               }else{
                    income.setId(id);
                    db.UpdateIncome(income);
               }
               db.close();

               finish();

           }


           }
       });


        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();
            }
        });

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog=new CalendarDialog(false);
//                dialog.show(getFragmentManager(),"Calendar Dialog");


                d.show(getSupportFragmentManager() , "Calendar Dialog");

            }
        });



    }

    private CalendarDatePickerDialog.OnDateSetListener listener = new CalendarDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
            String month,day;
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
            date = i + "-" + month + "-" + day;
        }
    };

    public void setIncomeDate(String date){
        this.date=date;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
