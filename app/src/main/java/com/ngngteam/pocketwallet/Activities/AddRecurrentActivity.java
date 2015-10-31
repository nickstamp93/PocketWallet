package com.ngngteam.pocketwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPicker;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.ngngteam.pocketwallet.Adapters.CategorySpinnerAdapter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;
import com.ngngteam.pocketwallet.Model.SpinnerItem;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.MyDateUtils;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddRecurrentActivity extends AppCompatActivity implements NumberPickerDialogFragment.NumberPickerDialogHandler
        , CalendarDatePickerDialog.OnDateSetListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    TextView tvAmount, tvRepeat;
    EditText etName, etDate;
    Spinner spinnerCategories;
    Button bOk, bCancel;
    ImageButton ibRepeat;

    MoneyDatabase moneyDatabase;
    CategoryDatabase categoryDatabase;

    private String date, currency;
    private boolean isExpense, isValid;
    SimpleDateFormat dateFormat;
    Calendar calendar;

    RecurrencePickerDialog recurrenceDialog;
    CalendarDatePickerDialog dateDialog;
    NumberPickerBuilder amountDialog;

    RecurrentTransaction itemToUpdate;
    boolean updateMode;

    private String recurrenceRule;
    private int freq, interval, day;
    private String expiration;

    private ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_recurrent);

        //init variables
        init();

        //init UI elements
        initUI();

        setUpUI();

        //get a serializable item from the intent
        itemToUpdate = (RecurrentTransaction) getIntent().getSerializableExtra("itemToUpdate");

        //If variable item is not null it means that this activity has been launched from
        // RecurrentTransactionsAcivity so we are in updateMode mode for the item passed
        if (itemToUpdate != null) {
            updateMode = true;
            isExpense = itemToUpdate.getIsExpense() == 1 ? true : false;
            initUiValues();

            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(getString(R.string.pref_key_show_again), true)) {
                showAlertDialog();
            }

            //else we are in creation mode , so init a
        } else {
            itemToUpdate = new RecurrentTransaction();
            updateMode = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void init() {
        moneyDatabase = new MoneyDatabase(this);
        categoryDatabase = new CategoryDatabase(this);

        currency = PreferenceManager.getDefaultSharedPreferences(AddRecurrentActivity.this).getString(getResources().getString(R.string.pref_key_currency), "€");

        isExpense = true;
        isValid = true;


        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(Calendar.getInstance().getTime());


        amountDialog = new NumberPickerBuilder().setFragmentManager(getSupportFragmentManager())
                .setPlusMinusVisibility(NumberPicker.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment);

        calendar = Calendar.getInstance();
        dateDialog = CalendarDatePickerDialog.newInstance(AddRecurrentActivity.this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initUI() {

        tvAmount = (TextView) findViewById(R.id.tvPrice);
        tvAmount.setText("0.00 " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value)));

        spinnerCategories = (Spinner) findViewById(R.id.sCategories);

        etName = (EditText) findViewById(R.id.etCatName);
        etDate = (EditText) findViewById(R.id.etDate);

        tvRepeat = (TextView) findViewById(R.id.tvRepeat);
        ibRepeat = (ImageButton) findViewById(R.id.ibRepeat);


        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);

    }

    private void setUpUI() {

        fillDateText();

        populateSpinnerCategories();

        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountDialog.show();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show(AddRecurrentActivity.this.getSupportFragmentManager(), "Date dialog");
            }
        });

        ibRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recurrenceDialog = new RecurrencePickerDialog();
                if (recurrenceRule == null) {
                    recurrenceRule = "FREQ=DAILY;WKST=SU";
                }
                if (recurrenceRule != null && recurrenceRule.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(RecurrencePickerDialog.BUNDLE_RRULE, recurrenceRule);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, -1);
                    bundle.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, c.getTimeInMillis());
                    recurrenceDialog.setArguments(bundle);
                }
                recurrenceDialog.setOnRecurrenceSetListener(AddRecurrentActivity.this);

                recurrenceDialog.show(AddRecurrentActivity.this.getSupportFragmentManager(), "Recurrence dialog");
            }
        });
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category;

                //if some vital info is missing from the form , alert the user
                if (!isFormComplete()) {
                    return;
                }

                //get the pure amount text , without the currency text
                int currencyLength = currency.length();
                double amount = Double.parseDouble(tvAmount.getText().subSequence(0, tvAmount.getText().length() - currencyLength).toString());

                //get the name
                String name = etName.getText().toString();

                //get the position of the chosen category from spinner
                int position = spinnerCategories.getSelectedItemPosition();
                category = categories.get(position);

                //create a new transaction item
                RecurrentTransaction item = new RecurrentTransaction(name,
                        amount,
                        category,
                        date,
                        freq,
                        interval,
                        day,
                        expiration,
                        isExpense ? 1 : 0,
                        isValid ? 1 : 0);


                //if it is create mode
                if (!updateMode) {
                    //if date is today , must add a transaction for today and calculate next date
                    if (MyDateUtils.isToday(date)) {
                        item.addOneTransaction(AddRecurrentActivity.this);
                    }
                    moneyDatabase.insertRecurrent(item);
                    //else we are in updateMode mode
                } else {

                    //set the id of the newly created  item as the item passed in the intent
                    //in order to update its values
                    item.setId(itemToUpdate.getId());
                    item.setIsValid(1);

                    //if recurrent string has not been changed , copy the old values
                    //from the object that is passed through the intent
                    if (recurrenceRule == null) {
                        item.setFreq(itemToUpdate.getFreq());
                        item.setInterval(itemToUpdate.getInterval());
                        item.setDay(itemToUpdate.getDay());
                        item.setExpiration(itemToUpdate.getExpiration());
                    }
                    //either way , here the repeat values are set , either update or
                    //are the same as before

                    //if the date is today , it is supposed that the transaction is done for today
                    //so must calculate the next date for the transaction to take place
                    if (MyDateUtils.isToday(date))
                        item.calculateNextDate(item.getDate());
                    moneyDatabase.updateRecurrent(item);
                }
                //Close the database and finish the activity
                moneyDatabase.close();
                AddRecurrentActivity.this.finish();

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRecurrentActivity.this.finish();
            }
        });
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecurrentActivity.this);
        LayoutInflater adbInflater = LayoutInflater.from(AddRecurrentActivity.this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        final CheckBox dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        builder.setView(eulaLayout);
        builder.setMessage("If you change the date or the repeat only future transactions will be affected")
                .setTitle("Attention");
        builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddRecurrentActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.pref_key_show_again), !dontShowAgain.isChecked());
                // Commit the edits!
                editor.commit();
                return;

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isFormComplete() {

        if (etName.getText().toString().trim().length() == 0) {
            Toast.makeText(AddRecurrentActivity.this, "Please enter a name for the transaction", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvRepeat.getText().toString().equals("not set")) {
            Toast.makeText(AddRecurrentActivity.this, "Please set a recurrency mode", Toast.LENGTH_SHORT).show();
            return false;
        }
        int currencyLength = currency.length();
        double amount = Double.parseDouble(tvAmount.getText().subSequence(0, tvAmount.getText().length() - currencyLength).toString());
        if (amount <= 0) {
            Toast.makeText(AddRecurrentActivity.this, "Please enter a non zero amount for the transaction", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void populateSpinnerCategories() {

        categories = categoryDatabase.getCategories(isExpense);
        //=========================set up the spinner with the categories===========================
        //get from CategoryDatabase all the categories and save them in to an ArrayList
        ArrayList<SpinnerItem> spinnerItems = new ArrayList<SpinnerItem>();
        //Initialize the the categories in UI using LetterImageView for each category
        for (int i = 0; i < categories.size(); i++) {
            String name = categories.get(i);
            int color = categoryDatabase.getColorFromCategory(name, isExpense);
            char letter = categoryDatabase.getLetterFromCategory(name, isExpense);
            spinnerItems.add(new SpinnerItem(name, color, letter));
        }
        //Initialize the CategorySpinnerAdapter
        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(AddRecurrentActivity.this, R.layout.spinner_item_categories, spinnerItems);
        //Set the adapter of spinner item to be all the categories from CategoryDatabase
        spinnerCategories.setAdapter(adapter);

    }

    private void initUiValues() {

        tvAmount.setText(itemToUpdate.getAmount() + " " + currency);

        etName.setText(itemToUpdate.getName());

        recurrenceRule = null;

        isExpense = itemToUpdate.getIsExpense() == 1 ? true : false;
        isValid = itemToUpdate.getIsValid() == 1 ? true : false;

        populateSpinnerCategories();
        spinnerCategories.setSelection(categoryDatabase.getPositionFromValue(itemToUpdate.getCategory(), isExpense));

        String tokens[] = itemToUpdate.getDate().split("-");
        String year = tokens[0];
        String month = tokens[1];
        String day = tokens[2];

        dateDialog = CalendarDatePickerDialog.newInstance(this,
                Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

        date = year + "-" + month + "-" + day;

        fillDateText();

        fillRepeatText();
    }

    //fill the repeat edittext with a user friendly text informing about the repeat mode selected
    private void fillRepeatText() {
        String strFreq = "";
        if (itemToUpdate.getFreq() == 0) {
            strFreq = getResources().getString(R.string.day);
        } else if (itemToUpdate.getFreq() == 1) {
            strFreq = getResources().getString(R.string.week);
        } else if (itemToUpdate.getFreq() == 2) {
            strFreq = getResources().getString(R.string.month);
        } else if (itemToUpdate.getFreq() == 3) {
            strFreq = getResources().getString(R.string.year);
        }
        String strExpire;
        if (itemToUpdate.getExpiration() == null) {
            strExpire = getResources().getString(R.string.forever);
        } else if (itemToUpdate.getExpiration().split(":")[0].equalsIgnoreCase("count")) {
            strExpire = " "+getResources().getString(R.string.for_) + " " + itemToUpdate.getExpiration().split(":")[1].split("/")[1] + " "+ getResources().getString(R.string.times);
        } else {
            strExpire = " " + getResources().getString(R.string.until) +" " + itemToUpdate.getExpiration().split(":")[1];
        }
        String strDisplay = getResources().getString(R.string.every)+" " + itemToUpdate.getInterval() + strFreq + strExpire;
        tvRepeat.setText(strDisplay);
    }

    //fill the date edittext with a user friendly text informing about the date selected
    private void fillDateText() {

        if (MyDateUtils.isToday(date)) {

            etDate.setText(getString(R.string.text_today));
        } else if (MyDateUtils.isYesterday(date)) {
            etDate.setText(getString(R.string.text_yesterday));

        } else if (MyDateUtils.isTomorrow(date)) {
            etDate.setText(getString(R.string.text_tomorrow));

        } else {
            etDate.setText(date);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_recurrent, menu);
        if (updateMode) {
            MenuItem delete = menu.add(getString(R.string.action_delete)).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    moneyDatabase.deleteRecurrent(itemToUpdate.getId());
                    finish();
                    return false;
                }
            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.action_menu_toggle) {
            if (isExpense)
                item.setTitle(getString(R.string.action_expense));
            else
                item.setTitle(getString(R.string.action_income));

            isExpense = !isExpense;
            populateSpinnerCategories();
        }
        return true;
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNumber, double fullNumber) {
        tvAmount.setText(fullNumber + " " + currency);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int cMonth, int cDay) {

        calendar = Calendar.getInstance();

        //new date based on date selected
        Calendar cNewDate = Calendar.getInstance();
        cNewDate.set(year, cMonth, cDay);

        //because i compare with day of year , if the year changes it starts over
        //so we must add 365 to the result to fix that
        int adj = 0;
        if (cNewDate.get(Calendar.YEAR) > calendar.get(Calendar.YEAR))
            adj = 365;
        //if day of year selected is former than the today , set date to today and inform the user
        if (cNewDate.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR) + adj) {
            Toast.makeText(AddRecurrentActivity.this, "You can't select a past date for a recurrent transaction"
                    , Toast.LENGTH_LONG).show();
            date = dateFormat.format(Calendar.getInstance().getTime());
        } else {
            date = dateFormat.format(cNewDate.getTime());
        }

        fillDateText();
    }

    @Override
    public void onRecurrenceSet(String s) {

        recurrenceRule = s;

        if (recurrenceRule != null && recurrenceRule.length() > 0) {

            Log.i("nikos", recurrenceRule);
            EventRecurrence recurrenceEvent = new EventRecurrence();
            recurrenceEvent.setStartDate(new Time("" + Calendar.getInstance().getTimeInMillis()));
            recurrenceEvent.parse(s);

            freq = recurrenceEvent.freq - 4;
            interval = recurrenceEvent.interval;
            if (recurrenceEvent.byday != null)
                day = recurrenceEvent.day2TimeDay(recurrenceEvent.byday[0]);
            else
                day = -1;
            //if expires by date , set date
            if (recurrenceEvent.count == 0) {
                if (recurrenceEvent.until == null)
                    expiration = null;
                else
                    try {
                        //format the date until ,to our date format
                        expiration = "date:" + dateFormat.format(new SimpleDateFormat("yyyyMMdd").parse(recurrenceEvent.until.substring(0, 8)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

            } else {
                //or set count:0/total
                expiration = "count:0/" + recurrenceEvent.count;
            }

            String srt = EventRecurrenceFormatter.getRepeatString(AddRecurrentActivity.this, getResources(), recurrenceEvent, true);
            tvRepeat.setText(srt);
        } else {
            tvRepeat.setText("not set");
        }


    }
}
