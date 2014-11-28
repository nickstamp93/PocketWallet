package myexpenses.ng2.com.myexpenses.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPicker;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.ExpenseItem;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;
import myexpenses.ng2.com.myexpenses.Utils.SpinnerAdapter;
import myexpenses.ng2.com.myexpenses.Utils.SpinnerItem;

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
public class AddExpenseActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    private Button bOk, bCancel;
    private ImageButton ibCalendar, ibCamera;

    private EditText etNotes, etDate;
    private TextView tvPrice;
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
    private CalendarDatePickerDialog d;
    private NumberPickerBuilder npb;

    private ArrayList<String> allCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("pref_key_theme" , "black").equals("fuchsia")){
            setTheme(R.style.AppThemeFuchsia);
        }else if(prefs.getString("pref_key_theme" , "black").equals("black")) {
            setTheme(R.style.AppThemeBlack);
        }
        setContentView(R.layout.activity_add_expense);


        initBasicVariables();
        initUi();
        initListeners();

        item = (ExpenseItem) getIntent().getSerializableExtra("Expense");
        if (item != null) {
            update = true;
            id = item.getId();
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
        d = CalendarDatePickerDialog.newInstance(listener,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        String day = c.get(Calendar.DAY_OF_MONTH) + "";
        String month = (c.get(Calendar.MONTH)+1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH)+1 < 10) {
            month = "0" + (c.get(Calendar.MONTH)+1);
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


        npb = new NumberPickerBuilder().setFragmentManager(getSupportFragmentManager())
                .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment);

    }

    private void initUiValues() {
        cdb = new CategoryDatabase(AddExpenseActivity.this);

        tvPrice.setText(item.getPrice() + " " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_key_currency", "€"));
        etNotes.setText(item.getNotes());
        sCategories.setSelection(cdb.getPositionFromValue(item.getCategories(), true));
        cdb.close();

        String tokens[] = item.getDate().split("-");
        int day = Integer.parseInt(tokens[2]);
        int month = Integer.parseInt(tokens[1]);
        int year = Integer.parseInt(tokens[0]);

        d = CalendarDatePickerDialog.newInstance(listener,
                year, month, day);
        date = year + "-" + month + "-" + day;
        etDate.setText(reverseDate());

    }


    private void initUi() {

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
        ibCalendar = (ImageButton) findViewById(R.id.ibCalendar);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        etDate = (EditText) findViewById(R.id.etDate);
        etNotes = (EditText) findViewById(R.id.etNotes);
        sCategories = (Spinner) findViewById(R.id.sCategories);

        etDate.setText(reverseDate());

        //get from CategoryDatabase all the categories and save them in to an ArrayList
        allCategories = cdb.getCategories(true);
              ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();

        for (int i = 0; i < allCategories.size(); i++) {
            String name = allCategories.get(i);
            int color = cdb.getColorFromCategory(name, true);
            char letter = cdb.getLetterFromCategory(name, true);
            spinnerItems.add(new SpinnerItem(name, color, letter));

        }

        //Initialize the SpinnerAdapter
        SpinnerAdapter adapter = new SpinnerAdapter(AddExpenseActivity.this, R.layout.spinner_item, spinnerItems);
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
                    price = Double.parseDouble(tvPrice.getText().subSequence(0,tvPrice.getText().length()-1).toString());
                } catch (NumberFormatException e) {
                    ok = false;
                    Toast.makeText(getApplicationContext(), "Plz Press a numerical in Price and not a character", Toast.LENGTH_LONG).show();
                }
                //if we took the price correctly we continue to retrieve the other information of the expense
                if (ok) {

                    //category = sCategories.getSelectedItem().toString();
                    int position = sCategories.getSelectedItemPosition();

                    category = allCategories.get(position);
                    notes = etNotes.getText().toString();
                    item = new ExpenseItem(category, notes, price, date);
                    //if we took a picture using the image button camera we set to the ExpenseItem expense the byte array

                    if (!update) {
                        //then we add the expense to our database we close it and we finish the activity
                        mydb.InsertExpense(item);
                        SharedPrefsManager manager = new SharedPrefsManager(AddExpenseActivity.this);
                        manager.startEditing();
                        float difference = manager.getPrefsBalance() - (float)item.getPrice();
                        manager.setPrefsBalance(difference);
                        manager.setPrefsDifference((float)item.getPrice() + manager.getPrefsDifference());
                        manager.commit();
                        if(!manager.getPrefsOnSalary() && manager.getPrefsBalance() < 0){
                            manager.setPrefsSavings(manager.getPrefsSavings() + manager.getPrefsBalance());
                            manager.setPrefsBalance(0);
                            manager.commit();
                        }
                    } else {
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


                d.show(getSupportFragmentManager(), "Calendar Dialog");


            }
        });


        tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                npb.show();

            }
        });


    }

    private CalendarDatePickerDialog.OnDateSetListener listener = new CalendarDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
            String month, day;
            if (i2 < 10) {
                month = "0" + i2;
            } else {
                month = String.valueOf(i2);
            }
            if (i3 < 10) {
                day = "0" + i3;
            } else {
                day = String.valueOf(i3);
            }
            date = i + "-" + month + "-" + day;
            etDate.setText(reverseDate());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.add_expense, menu);
        if (update) {

            MenuItem delete = menu.add("Delete").setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.i("MenuItem", "activated");
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

    public void setPrice(String price) {
        tvPrice.setText(price);
    }

    private String reverseDate() {

        String tokens[] = date.split("-");
        return tokens[2] + "-" + tokens[1] + "-" + tokens[0];


    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNumber, double fullNumber) {
        String currency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_key_currency", "€");
        tvPrice.setText(fullNumber + " " + currency);
    }
}
