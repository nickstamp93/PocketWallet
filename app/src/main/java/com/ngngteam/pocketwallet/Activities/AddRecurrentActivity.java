package com.ngngteam.pocketwallet.Activities;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPicker;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.ngngteam.pocketwallet.Adapters.CategorySpinnerAdapter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.ExpenseItem;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;
import com.ngngteam.pocketwallet.Model.SpinnerItem;
import com.ngngteam.pocketwallet.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddRecurrentActivity extends AppCompatActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler
        , CalendarDatePickerDialog.OnDateSetListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    TextView tvAmount;
    EditText etName, etDate, etRepeat;
    Spinner sCategories;
    Button bOk, bCancel;

    MoneyDatabase db;
    CategoryDatabase cdb;
    private String date, recurrentString;

    RecurrencePickerDialog recurrenceDialog;
    CalendarDatePickerDialog dateDialog;
    NumberPickerBuilder amountDialog;

    RecurrentTransaction item;
    boolean update;
    int id;

    private ArrayList<String> allCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recurrent);


        init();

        initUI();

        setUpUI();
//This activity contains 2 modules. Add a new expense in Database or update an exist one.
        //When the user wants to update a expense he just click the history item or click the last expense from overview. From History
        //we start an Intent to open this activity. So we use a ExpenseItem variable to get the
        //expense that the user wants to update.
        item = (RecurrentTransaction) getIntent().getSerializableExtra("item");
        //If variable item is not null it means that AddExpenseActivity is called from HistoryActivity so
        //we need to update the values of the expense that pressed.
        if (item != null) {
            update = true;
            id = item.getId();
            initUiValues();
        } else {
            item = new RecurrentTransaction();
            update = false;
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void init() {
        db = new MoneyDatabase(this);
        cdb = new CategoryDatabase(this);

        allCategories = cdb.getCategories(true);

        amountDialog = new NumberPickerBuilder().setFragmentManager(getSupportFragmentManager())
                .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment);
        Calendar c = Calendar.getInstance();
        dateDialog = CalendarDatePickerDialog.newInstance(AddRecurrentActivity.this,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    private void initUI() {

        tvAmount = (TextView) findViewById(R.id.tvPrice);
        tvAmount.setText("0.00 " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));

        sCategories = (Spinner) findViewById(R.id.sCategories);

        etName = (EditText) findViewById(R.id.etCatName);
        etDate = (EditText) findViewById(R.id.etDate);
        etRepeat = (EditText) findViewById(R.id.etRepeat);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);

    }

    private void setUpUI() {
        //=========================set up the spinner with the categories===========================
        //get from CategoryDatabase all the categories and save them in to an ArrayList
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();
        //Initialize the the categories in UI using LetterImageView for each category
        for (int i = 0; i < allCategories.size(); i++) {
            String name = allCategories.get(i);
            int color = cdb.getColorFromCategory(name, true);
            char letter = cdb.getLetterFromCategory(name, true);
            spinnerItems.add(new SpinnerItem(name, color, letter));
        }
        //Initialize the CategorySpinnerAdapter
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(AddRecurrentActivity.this, R.layout.spinner_item_categories, spinnerItems);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
        sCategories.setAdapter(adapter);
        cdb.close();

        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountDialog.show();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateDialog.show(getSupportFragmentManager(), "tag");
            }
        });
        etRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recurrenceDialog = new RecurrencePickerDialog();
                recurrenceDialog.setOnRecurrenceSetListener(AddRecurrentActivity.this);
                recurrenceDialog.show(getSupportFragmentManager(), "tag");
            }
        });
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                double price = 0;
                String category, notes;
                //get the price of the expense if it has problem a Toast appear and say to correct it
                try {
                    int currencyLength = PreferenceManager.getDefaultSharedPreferences(AddRecurrentActivity.this).getString(getResources().getString(R.string.pref_key_currency), "€").length();
                    price = Double.parseDouble(tvAmount.getText().subSequence(0, tvAmount.getText().length() - currencyLength).toString());
                } catch (NumberFormatException e) {
                    ok = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_number), Toast.LENGTH_LONG).show();
                }
                //if we took the price correctly we continue to retrieve the other information of the expense
                if (ok) {

                    //get the position of the chosen category from spinner
                    int position = sCategories.getSelectedItemPosition();

                    category = allCategories.get(position);

                    item = new RecurrentTransaction(etName.getText().toString(), price, category,
                            etDate.getText().toString(), recurrentString, 1);


                    //After retrieve all values from the UI we want to check if this Expense is new so we just add
                    //it to database or if this expense already exists so we need to update it
                    if (!update) {
                        // we add the expense to our database
                        db.insertRecurrent(item);


                    } else {
                        //update the expense
                        item.setId(id);
                        db.updateRecurrent(item);
                    }
                    //Close the database and finish the activity
                    db.close();

                    finish();
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUiValues() {

        tvAmount.setText(item.getAmount() + " " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));

        etName.setText(item.getName());

        sCategories.setSelection(cdb.getPositionFromValue(item.getCategory(), true));

        String tokens[] = item.getDate().split("-");
        String year = tokens[0];
        String month = tokens[1];
        String day = tokens[2];
        dateDialog = CalendarDatePickerDialog.newInstance(this,
                Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        date = year + "-" + month + "-" + day;

        try {
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            Calendar item_date = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            item_date.setTime(format.parse(date));

            boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
            boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
            if (isToday) {
                etDate.setText(getString(R.string.text_today));
            } else if (isYesterday) {
                etDate.setText(getString(R.string.text_yesterday));
            } else {
                etDate.setText(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fillRepeatText();
    }

    private void fillRepeatText() {
        String strFreq = "";
        if (item.getFreq() == 0) {
            strFreq = " day(s)";
        } else if (item.getFreq() == 1) {
            strFreq = " week(s)";
        } else if (item.getFreq() == 2) {
            strFreq = " month(s)";
        } else if (item.getFreq() == 3) {
            strFreq = " year(s)";
        }
        String strExpire = "";
        if (item.getExpiration() == null) {
            strExpire = " forever";
        } else if (item.getExpiration().split(":")[0].equalsIgnoreCase("count")) {
            strExpire = " for " + item.getExpiration().split(":")[1] + " times";
        } else {
            strExpire = " until " + item.getExpiration().split(":")[1];
        }
        String strDisplay = "Every " + item.getInterval() + strFreq + strExpire;
        etRepeat.setText(strDisplay);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_recurrent, menu);
        if (update) {
            return true;
        }
        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu_delete) {
            db.deleteRecurrent(id);
            finish();
        }
        return true;
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNumber, double fullNumber) {

        String currency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value));
        tvAmount.setText(fullNumber + " " + currency);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int cMonth, int cDay) {
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

        try {
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            Calendar item_date = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            item_date.setTime(format.parse(date));

            boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
            boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
            if (isToday) {
                etDate.setText(getString(R.string.text_today));
            } else if (isYesterday) {
                etDate.setText(getString(R.string.text_yesterday));
            } else {
                etDate.setText(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecurrenceSet(String s) {
        //create
        recurrentString = s;
        if (s != null) {
            item.populateFromDialog(s);
            fillRepeatText();
        }

    }
}
