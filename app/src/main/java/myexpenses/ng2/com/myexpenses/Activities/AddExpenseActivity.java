package myexpenses.ng2.com.myexpenses.Activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import myexpenses.ng2.com.myexpenses.Adapters.SpinnerAdapter;
import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.Model.ExpenseItem;
import myexpenses.ng2.com.myexpenses.Model.SpinnerItem;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.Themer;

/*
Future Modules
1)Credit or Cash on Expenses
2)Diagrams-Pites probably Done
3)Repeat some of the expenses
4)SubCategories
5)User add a Category DONE
6)Pattern custom DONE
7)Switch on History Activity Income-Expense with filters (Toggle Button) DONE
8)Filters change in DropDown Menu in ActionBar DONE
TO DO LIST
1) Icons for whole app OK
2) translate the strings in greek
3)fix the dimensions
4)cleaning and debugging
5)Notification fixed
6)Styles for dialogs OK
7)Default Categories OK
 */
public class AddExpenseActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    private Button bOk, bCancel;
    private ImageButton ibCalendar;

    private EditText etNotes, etDate;
    private TextView tvPrice;
    private Spinner sCategories;
    private MoneyDatabase mydb;
    private CategoryDatabase cdb;
    private ExpenseItem item;

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
        Themer.setThemeToActivity(this);
        setContentView(R.layout.activity_add_expense);


        init();
        initUi();
        setUpUI();

        //This activity contains 2 modules. Add a new expense in Database or update an exist one.
        //When the user wants to update a expense he just click the history item. From History
        //we start an Intent to open this activity. So we use a ExpenseItem variable to get the
        //expense that the user wants to update.
        item = (ExpenseItem) getIntent().getSerializableExtra("Expense");
        //If variable item is not null it means that AddExpenseActivity is called from HistoryActivity so
        //we need to update the values of the expense that pressed.
        if (item != null) {
            update = true;
            id = item.getId();
            initUiValues();
        }

    }


    private void init() {
        cdb = new CategoryDatabase(AddExpenseActivity.this);
        mydb = new MoneyDatabase(AddExpenseActivity.this);
        //Open the database
        try {
            mydb.openDatabase();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_database), Toast.LENGTH_SHORT).show();
        }

        update = false;

        //Get current date and save it to variable date with format e.t.c 2014-09-12
        c = Calendar.getInstance();
        d = CalendarDatePickerDialog.newInstance(listener,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        String day = c.get(Calendar.DAY_OF_MONTH) + "";
        String month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        date = c.get(Calendar.YEAR) + "-" + month + "-" + day;


        npb = new NumberPickerBuilder().setFragmentManager(getSupportFragmentManager())
                .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment);

    }

    private void initUiValues() {
        cdb = new CategoryDatabase(AddExpenseActivity.this);

        tvPrice.setText(item.getPrice() + " " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));
        etNotes.setText(item.getNotes());
        sCategories.setSelection(cdb.getPositionFromValue(item.getCategory(), true));
        cdb.close();

        String tokens[] = item.getDate().split("-");
        String year = tokens[0];
        String month = tokens[1];
        String day = tokens[2];
        d = CalendarDatePickerDialog.newInstance(listener,
                Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        date = year + "-" + month + "-" + day;
        etDate.setText(reverseDate());

    }


    private void initUi() {

        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText("0.00 " +
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));

        sCategories = (Spinner) findViewById(R.id.sCategories);

        etDate = (EditText) findViewById(R.id.etDate);
        etDate.setText(reverseDate());
        ibCalendar = (ImageButton) findViewById(R.id.ibCalendar);

        etNotes = (EditText) findViewById(R.id.etNotes);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
        Themer.setBackgroundColor(this, bOk, false);
        Themer.setBackgroundColor(this, bCancel, true);


    }

    private void setUpUI() {

        //=========================set up the spinner with the categories===========================
        //get from CategoryDatabase all the categories and save them in to an ArrayList
        allCategories = cdb.getCategories(true);
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();
        //Initialize the the categories in UI using LetterImageView for each category
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
        cdb.close();

        //=============================date picker listener=========================================
        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.show(getSupportFragmentManager(), "Calendar Dialog");
            }
        });

        //=============================price textview listener=======================================
        tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                npb.show();

            }
        });

        //=============================ok button listener===========================================
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean ok = true;
                double price = 0;
                String category, notes;
                //get the price of the expense if it has problem a Toast appear and say to correct it
                try {
                    int currencyLength = PreferenceManager.getDefaultSharedPreferences(AddExpenseActivity.this).getString(getResources().getString(R.string.pref_key_currency), "€").length();
                    price = Double.parseDouble(tvPrice.getText().subSequence(0, tvPrice.getText().length() - currencyLength).toString());
                } catch (NumberFormatException e) {
                    ok = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_number), Toast.LENGTH_LONG).show();
                }
                //if we took the price correctly we continue to retrieve the other information of the expense
                if (ok) {

                    //get the position of the chosen category from spinner
                    int position = sCategories.getSelectedItemPosition();

                    category = allCategories.get(position);
                    notes = etNotes.getText().toString();
                    item = new ExpenseItem(category, notes, price, date);

                    //After retrieve all values from the UI we want to check if this Expense is new so we just add
                    //it to database or if this expense already exists so we need to update it
                    if (!update) {
                        // we add the expense to our database
                        mydb.InsertExpense(item);


                    } else {
                        //update the expense
                        item.setId(id);
                        mydb.UpdateExpense(item);
                    }
                    //Close the database and finish the activity
                    mydb.close();

                    finish();
                }


            }

        });

        //===========================cancel button listener=========================================
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private CalendarDatePickerDialog.OnDateSetListener listener = new CalendarDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int cMonth, int cDay) {
            //This listener used when we change the date. We just increase the value of month because in android
            //the months start to count from 0. After that we set up the proper format for the date and show
            //in EditText etDate

            String month, day;
            cMonth++;
            if (cMonth < 10) {
                month = "0" + cMonth;
            } else {
                month = String.valueOf(cMonth);
            }
            if (cDay < 10) {
                day = "0" + cDay;
            } else {
                day = String.valueOf(cDay);
            }
            date = year + "-" + month + "-" + day;
            etDate.setText(reverseDate());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //When the user want to update a expense we display at the top right of menu a menu item that
        //is used for deletion of this expense.
        if (update) {

            MenuItem delete = menu.add(getResources().getString(R.string.action_delete)).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mydb.deleteExpense(item.getId());
                    mydb.close();
                    finish();
                    return false;
                }
            });
        }
        return true;
    }


    public void setDate(String date) {
        this.date = date;
    }

    private String reverseDate() {
        String tokens[] = date.split("-");
        return tokens[2] + "-" + tokens[1] + "-" + tokens[0];
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNumber, double fullNumber) {
        String currency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value));
        tvPrice.setText(fullNumber + " " + currency);
    }
}
