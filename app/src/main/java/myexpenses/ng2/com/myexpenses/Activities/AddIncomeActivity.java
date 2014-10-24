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
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Data.IncomeItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CalendarDialog;

public class AddIncomeActivity extends FragmentActivity {

    private EditText etAmount,etSource;
    private ImageButton ibCalendar;
    private Button bOk,bCancel;
    private MoneyDatabase db;
    private IncomeItem income;
    private CalendarDialog dialog;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        initBasicVariables();
        initUI();
        initListeners();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_income, menu);
        return true;
    }

    private void initBasicVariables(){
        db=new MoneyDatabase(AddIncomeActivity.this);
        try {
            db.openDatabase();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Problem with our database", Toast.LENGTH_SHORT).show();
        }
        Time now=new Time();
        now.setToNow();
        String day=now.monthDay+"",month=now.month+"";

        if(now.monthDay<10){
            day="0"+now.monthDay;
        }
        if(now.month<10){
            month="0"+now.month;
        }
        date=now.year+"-"+month+"-"+day;
    }

    private void initUI(){

       etAmount=(EditText)findViewById(R.id.etiamount);
       etSource=(EditText)findViewById(R.id.etisource);
       ibCalendar =(ImageButton) findViewById(R.id.ibiCalendar);
       bOk=(Button)findViewById(R.id.biOK);
       bCancel=(Button)findViewById(R.id.biCancel);
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

               source=etSource.getText().toString();
           //then we add the income to our database we close it and we finish the activity
               income=new IncomeItem(amount,date,source);
               db.InsertIncome(income);
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

                Calendar c = Calendar.getInstance();
                CalendarDatePickerDialog d = CalendarDatePickerDialog.newInstance(listener ,
                        c.get(Calendar.YEAR) , c.get(Calendar.MONTH) , c.get(Calendar.DAY_OF_MONTH));
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
