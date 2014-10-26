package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

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
TO DO LIST
1)Create a table for income categories DONE
2)Switch categories in CategoriesManagerActivity DONE but need to fix some methods
3)method to delete all expenses and all incomes to MoneyDatabase
4)Custom dialog to handle categories and tuples when you delete a category DONE
5)Create addCategoryActivity (EditText namecat button to choose color for category,ll to change the starting letter for category and preview
for the result. DONE
6)Change categoryDatabase, add to tables color and letter DONE
7) Process categories add functions to change the categories DONE
8)Add a filter feature amount in income
9) Fix some features in income (history List View Adapter and also update an income)
10)Fix the dialog for filters date and date-to-date
 */
public class AddExpenseActivity extends FragmentActivity {

    private Button bOk, bCancel;
    private ImageButton ibCalendar, ibCamera;

    private EditText etNotes, etPrice;
    private ImageView ivPhoto;
    private Spinner sCategories;
    private MoneyDatabase mydb;
    private CategoryDatabase cdb;
    private ExpenseItem item;

    private boolean image;
  //  private CalendarDialog dialog;

    private final int REQUEST_CODE = 1;
    private Bitmap bm;
    private String date;
    private int id;
    private boolean update;

    private Calendar c;
    private  CalendarDatePickerDialog d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);


        initBasicVariables();
        initUi();
        initListeners();

        item = (ExpenseItem) getIntent().getSerializableExtra("Expense");
        if (item != null) {
            update = true;
            id=item.getId();
            initUiValues();
            Log.i("ExpenseActivity", "Called from History");
            Log.i("Values", item.getCategories() + "-" + item.getDate() + '-' + item.getNotes() + item.getPrice() + "-" + item.getId());
        } else {
            Log.i("ExpenseActivity", "Called from Expense");
        }


    }

    private void initBasicVariables() {
        cdb = new CategoryDatabase(AddExpenseActivity.this);
        mydb = new MoneyDatabase(AddExpenseActivity.this);

        try {
            mydb.openDatabase();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Problem with our database", Toast.LENGTH_SHORT).show();
        }
        image = false;
        update = false;


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
        Time now = new Time();
        now.setToNow();
        String day = now.monthDay + "", month = now.month + "";
        //Check if the day and month is <10 to add a leading zero in front of them
        if (now.monthDay < 10) {
            day = "0" + now.monthDay;
        }
        if (now.month < 10) {
            month = "0" + now.month;
        }*/

        date = c.get(Calendar.YEAR) + "-" + month + "-" + day;

    }

    private void initUiValues() {
        cdb = new CategoryDatabase(AddExpenseActivity.this);
        etPrice.setText(item.getPrice() + "");
        etNotes.setText(item.getNotes());
        sCategories.setSelection(cdb.getPositionFromValue(item.getCategories(),true));
        cdb.close();

        String tokens[]=item.getDate().split("-");
        int day=Integer.parseInt(tokens[2]);
        int month=Integer.parseInt(tokens[1]);
        int year=Integer.parseInt(tokens[0]);

        d= CalendarDatePickerDialog.newInstance(listener ,
                year , month , day);
        date=year+"-"+month+"-"+day;

    }


    private void initUi() {

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
        ibCalendar = (ImageButton) findViewById(R.id.ibCalendar);
        ibCamera = (ImageButton) findViewById(R.id.ibCamera);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etNotes = (EditText) findViewById(R.id.etNotes);
        ivPhoto = (ImageView) findViewById(R.id.ivReceive);
        sCategories = (Spinner) findViewById(R.id.sCategories);

        //get from CategoryDatabase all the categories and save them in to an ArrayList
        ArrayList<String> allCategories = cdb.getExpenseCategories(true);
        //Initialize the SpinnerAdapter
        SpinnerAdapter adapter = new SpinnerAdapter(AddExpenseActivity.this, allCategories);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
        sCategories.setAdapter(adapter);
        cdb.closeDB();

    }

    private void initListeners() {
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    boolean ok = true;
                    double price = 0;
                    String category, notes;
                    //get the price of the expense if it has problem a Toast appear and say to correct it
                    try {
                        price = Double.parseDouble(String.valueOf(etPrice.getText()));
                    } catch (NumberFormatException e) {
                        ok = false;
                        Toast.makeText(getApplicationContext(), "Plz Press a numerical in Price and not a character", Toast.LENGTH_LONG).show();
                    }
                    //if we took the price correctly we continue to retrieve the other information of the expense
                    if (ok) {

                        category = sCategories.getSelectedItem().toString();
                        notes = etNotes.getText().toString();
                        item = new ExpenseItem(category, notes, price, date);
                        //if we took a picture using the image button camera we set to the ExpenseItem expense the byte array
                        if (image) {
                            item.setReceive(bm);
                        }
                        if(!update){
                            //then we add the expense to our database we close it and we finish the activity
                            mydb.InsertExpense(item);

                        }else{
                            item.setId(id);
                            mydb.UpdateExpense(item);
                        }
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

//                dialog = new CalendarDialog(true);
//                dialog.show(getFragmentManager(), "Calendar Dialog");


                d.show(getSupportFragmentManager() , "Calendar Dialog");


            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when we press the image button ibCamera we have access to the camera of the phone and we save the picture
                //to the ImageView ivPhoto
                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                image = true;
                bm = (Bitmap) data.getExtras().get("data");
                ivPhoto.setImageBitmap(bm);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.add_expense, menu);
        if(update){

         MenuItem delete=menu.add("Delete").setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
         delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.i("MenuItem","activated");
                    mydb.deleteExpense(item.getId());
                    mydb.close();
                    finish();
                    return false;
                }
            });
        }
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


    public void setDate(String date) {
        this.date = date;
    }


}
