package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.ExpenseItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CalendarDialog;
import myexpenses.ng2.com.myexpenses.Utils.SpinnerAdapter;

/*
Future Modules
1)Credit or Cash on Expenses
2)Diagrams-Pites probably library
3)Repeat some of the expenses
4)SubCategories
5)User add a Category DONE
6)Pattern custom DONE
7)Switch on History Activity Income-Expense with filters (Toggle Button) DONE
8)Filters change in DropDown Menu in ActionBar DONE
 */
public class AddExpenseActivity extends Activity {

  private Button bOk,bCancel;
  private ImageButton ibCalendar,ibCamera;

  private EditText etNotes,etPrice;
  private ImageView ivPhoto;
  private Spinner sCategories;
  private MoneyDatabase mydb;
  private CategoryDatabase cdb;
  private ExpenseItem item;

  private boolean image;
  private  CalendarDialog dialog;

  private final int REQUEST_CODE=1;
  private Bitmap bm;
  private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initBasicVariables();
        initUi();
        initListeners();


    }

    private void initBasicVariables(){
        cdb=new CategoryDatabase(AddExpenseActivity.this);
        mydb=new MoneyDatabase(AddExpenseActivity.this);

        try {
            mydb.openDatabase();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(),"Problem with our database",Toast.LENGTH_SHORT).show();
        }
        image=false;


        Time now=new Time();
        now.setToNow();
        String day=now.monthDay+"",month=now.month+"";
        //Check if the day and month is <10 to add a leading zero in front of them
        if(now.monthDay<10){
            day="0"+now.monthDay;
        }
        if(now.month<10){
            month="0"+now.month;
        }
        date=now.year+"-"+month+"-"+day;



    }

    private void initUi(){

      bOk=(Button) findViewById(R.id.bOK);
      bCancel=(Button) findViewById(R.id.bCancel);
      ibCalendar=(ImageButton) findViewById(R.id.ibCalendar);
      ibCamera=(ImageButton) findViewById(R.id.ibCamera);
      etPrice=(EditText) findViewById(R.id.etPrice);
      etNotes=(EditText) findViewById(R.id.etNotes);
      ivPhoto=(ImageView) findViewById(R.id.ivReceive);
      sCategories=(Spinner) findViewById(R.id.sCategories);

        //get from CategoryDatabase all the categories and save them in to an ArrayList
         ArrayList<String>   allCategories= cdb.getCategories();
        //Initialize the SpinnerAdapter
         SpinnerAdapter adapter=new SpinnerAdapter(AddExpenseActivity.this,allCategories);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
         sCategories.setAdapter(adapter);
         cdb.closeDB();

    }

    private void initListeners(){
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok=true;
                double price=0;
                String category,notes;
                //get the price of the expense if it has problem a Toast appear and say to correct it
                try {
                    price = Double.parseDouble(String.valueOf(etPrice.getText()));
                }catch (NumberFormatException e){
                    ok=false;
                    Toast.makeText(getApplicationContext(),"Plz Press a numerical in Price and not a character",Toast.LENGTH_LONG).show();
                }
                //if we took the price correctly we continue to retrieve the other information of the expense
              if(ok){

               category=sCategories.getSelectedItem().toString();
               notes=etNotes.getText().toString();
               item=new ExpenseItem(category,notes,price,date);
                //if we took a picture using the image button camera we set to the ExpenseItem expense the byte array
               if(image){
                   item.setReceive(bm);
               }
                //then we add the expense to our database we close it and we finish the activity
               mydb.InsertExpense(item);
               mydb.close();
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

                dialog = new CalendarDialog(true);
                dialog.show(getFragmentManager(),"Calendar Dialog");


            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //when we press the image button ibCamera we have access to the camera of the phone and we save the picture
                //to the ImageView ivPhoto
                Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            if(data!=null){
                image=true;
                bm=(Bitmap)data.getExtras().get("data");
                ivPhoto.setImageBitmap(bm);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_expense, menu);
        return true;
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


    public void setDate(String date){
        this.date=date;
    }


}
