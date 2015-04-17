package com.ngngteam.pocketwallet.Activities;

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
import com.ngngteam.pocketwallet.Adapters.SpinnerAdapter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.IncomeItem;
import com.ngngteam.pocketwallet.Model.SpinnerItem;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddIncomeActivity extends FragmentActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    private EditText etDate;
    private TextView tvAmount;
    private Spinner sCategories;
    private ImageButton ibCalendar;
    private Button bOk, bCancel;
    private MoneyDatabase db;
    private CategoryDatabase cdb;
    private IncomeItem income;
    private String date;
    private boolean update;
    private int id;
    private ArrayList<String> allCategories;

    private Calendar c;
    private CalendarDatePickerDialog d;
    private NumberPickerBuilder npb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_add_income);

        init();
        initUI();
        setUpUI();

        //if income not null , means that activity launched from history for editing,load data
        income = (IncomeItem) getIntent().getSerializableExtra("Income");
        if (income != null) {
            update = true;
            id = income.getId();
            initUiValues();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (update) {
            MenuItem delete = menu.add(getResources().getString(R.string.action_delete)).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
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
        tvAmount.setText(income.getAmount() + " " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));
        sCategories.setSelection(cdb.getPositionFromValue(income.getSource(), false));
        cdb.close();

        String tokens[] = income.getDate().split("-");
        String year = tokens[0];
        String month = tokens[1];
        String day = tokens[2];
        d = CalendarDatePickerDialog.newInstance(listener,
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

    }

    private void init() {
        db = new MoneyDatabase(AddIncomeActivity.this);
        try {
            db.openDatabase();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_database), Toast.LENGTH_SHORT).show();
        }

        cdb = new CategoryDatabase(getApplicationContext());

        update = false;

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

    private void initUI() {

        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvAmount.setText("0.00 " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));

        sCategories = (Spinner) findViewById(R.id.sIncomeCategories);

        etDate = (EditText) findViewById(R.id.etIncomeDate);
        etDate.setText(getString(R.string.text_today));
        ibCalendar = (ImageButton) findViewById(R.id.ibIncomeCalendar);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);
        Themer.setBackgroundColor(this, bOk, false);
        Themer.setBackgroundColor(this, bCancel, true);

    }


    private void setUpUI() {

        //======================spinner init with categories========================================
        //get from CategoryDatabase all the categories and save them in to an ArrayList
        allCategories = cdb.getCategories(false);

        ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();

        for (int i = 0; i < allCategories.size(); i++) {
            String name = allCategories.get(i);
            int color = cdb.getColorFromCategory(name, false);
            char letter = cdb.getLetterFromCategory(name, false);
            spinnerItems.add(new SpinnerItem(name, color, letter));

        }


        //Initialize the SpinnerAdapter
        SpinnerAdapter adapter = new SpinnerAdapter(AddIncomeActivity.this, R.layout.spinner_item, spinnerItems);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
        sCategories.setAdapter(adapter);
        cdb.close();

        //=========================ok button listener===============================================
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok = true;
                double amount = 0;
                String source;
                //get the price of the income if it has problem a Toast appear and say to correct it
                try {
                    int currencyLength = PreferenceManager.getDefaultSharedPreferences(AddIncomeActivity.this).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)).length();
                    amount = Double.parseDouble(tvAmount.getText().subSequence(0, tvAmount.getText().length() - currencyLength).toString());
                } catch (NumberFormatException e) {
                    ok = false;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_number), Toast.LENGTH_LONG).show();
                }

                //if we took the price correctly we continue to retrieve the other information of the income item
                if (ok) {

                    //source=sCategories.getSelectedItem().toString();
                    int position = sCategories.getSelectedItemPosition();
                    source = allCategories.get(position);

                    //then we add the income to our database we close it and we finish the activity
                    income = new IncomeItem(amount, date, source);
                    if (!update) {
                        db.InsertIncome(income);

                    } else {
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
                d.show(getSupportFragmentManager(), "Calendar Dialog");

            }
        });

        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                npb.show();

            }
        });

    }

    private CalendarDatePickerDialog.OnDateSetListener listener = new CalendarDatePickerDialog.OnDateSetListener() {
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
    };


    private String reverseDate() {

        String tokens[] = date.split("-");
        return tokens[2] + "-" + tokens[1] + "-" + tokens[0];

    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNumber, double fullNumber) {
        String currency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value));
        tvAmount.setText(fullNumber + " " + currency);
    }
}
