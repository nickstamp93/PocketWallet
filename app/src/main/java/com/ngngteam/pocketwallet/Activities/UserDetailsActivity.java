package com.ngngteam.pocketwallet.Activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

public class UserDetailsActivity extends AppCompatActivity {

    //SharedPrefsManager object
    SharedPrefsManager manager;

    Cursor cursorExpense, cursorIncome;
    MoneyDatabase db;

    EditText etSavings, etUsername;
    TextView tvDayStart;
    Button bOk, bCancel;
    RadioGroup radioGroup;
    Spinner spinnerDayStart;
    SpinnerAdapter spinnerAdapter;

    TableRow rowDayStart;

    //variables set by user
    float savings;
    String username, grouping, dayStart;
    int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_details);

        init();

        //init the UI
        initUI();

        //init the Listeners
        setUpUI();

        //init values
        fillUIValues();


    }

    private void init() {

        db = new MoneyDatabase(this);
        cursorExpense = db.getAllExpenses();
        cursorIncome = db.getAllIncomes();

    }

    //init the UI Views
    private void initUI() {

        etUsername = (EditText) findViewById(R.id.etUsername);

        etSavings = (EditText) findViewById(R.id.etSavings);
        if (cursorExpense.moveToFirst() || cursorIncome.moveToFirst()) {
            Toast.makeText(this, getString(R.string.toast_savings), Toast.LENGTH_LONG).show();
            etSavings.setEnabled(false);
            etSavings.setTextColor(Color.GRAY);

        }

        radioGroup = (RadioGroup) findViewById(R.id.rgGrouping);

        spinnerDayStart = (Spinner) findViewById(R.id.spinnerDayStart);

        tvDayStart = (TextView) findViewById(R.id.tvDayStart);

        rowDayStart = (TableRow) findViewById(R.id.rowDayStart);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);

    }

    //init form values according to preference file
    private void fillUIValues() {

        manager = new SharedPrefsManager(getApplicationContext());

        etUsername.setText(manager.getPrefsUsername());
        etSavings.setText(manager.getPrefsSavings() + "");

        if (manager.getPrefsGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_monthly))) {
            radioGroup.check(R.id.rbMonthly);
            spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.days_month, R.layout.spinner_item_simple);

            tvDayStart.setText(getString(R.string.textview_headline_month_start));
            rowDayStart.setVisibility(View.VISIBLE);
        } else if (manager.getPrefsGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_weekly))) {
            radioGroup.check(R.id.rbWeekly);
            spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.days_week, R.layout.spinner_item_simple);
            tvDayStart.setText(getString(R.string.textview_headline_week_start));
            rowDayStart.setVisibility(View.VISIBLE);
        } else if (manager.getPrefsGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_daily))) {
            radioGroup.check(R.id.rbDaily);
            rowDayStart.setVisibility(View.GONE);
        } else {
            radioGroup.check(R.id.rbNoGrouping);
            rowDayStart.setVisibility(View.GONE);
        }

        spinnerDayStart.setAdapter(spinnerAdapter);
        spinnerDayStart.setSelection(manager.getPrefsDayStart());

    }

    //init the Listeners
    private void setUpUI() {
        radioGroup.setOnCheckedChangeListener(radioListener);

        bOk.setOnClickListener(buttonListener);
        bCancel.setOnClickListener(buttonListener);


    }

    private RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rbWeekly) {
                tvDayStart.setText(getString(R.string.textview_headline_week_start));
                spinnerAdapter = ArrayAdapter.createFromResource(UserDetailsActivity.this,
                        R.array.days_week, R.layout.spinner_item_simple);
                spinnerDayStart.setAdapter(spinnerAdapter);
                rowDayStart.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbMonthly) {
                tvDayStart.setText(getString(R.string.textview_headline_month_start));
                spinnerAdapter = ArrayAdapter.createFromResource(UserDetailsActivity.this,
                        R.array.days_month, R.layout.spinner_item_simple);
                spinnerDayStart.setAdapter(spinnerAdapter);
                rowDayStart.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rbDaily) {
                rowDayStart.setVisibility(View.GONE);
            } else {
                Toast.makeText(UserDetailsActivity.this, getString(R.string.text_no_grouping_toast), Toast.LENGTH_LONG).show();
                rowDayStart.setVisibility(View.GONE);
            }
        }
    };

    //the buttons listener
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bOK:
                    //if form is not complete, alert the user
                    if (!isFormComplete()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_form), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //if form is complete , store these data to the shared prefs file
                    manager.startEditing();

                    getDataFromXml();

                    manager.setPrefsIsProfile(true);
                    manager.setPrefsUsername(username);
                    manager.setPrefsSavings(savings);
                    manager.setPrefsGrouping(grouping);
                    manager.setPrefsDayStart(spinnerDayStart.getSelectedItemPosition());

                    manager.commit();

                    //set the activity result to an OK state
                    setResult(RESULT_OK);

                    //terminate the activity
                    finish();
                case R.id.bCancel:
                    //if the user clicked the Cancel button , just terminate the activity without storing anything
                    setResult(RESULT_CANCELED);
                    finish();
            }
        }
    };

    //checks if the form is complete
    private boolean isFormComplete() {
        //if any of UI editTexts are empty , form is not complete
        if (etUsername.getText().toString().trim().length() < 1) {
            return false;
        }
        if (etSavings.getText().toString().trim().length() < 1) {
            return false;
        }
        return true;
    }

    //get user data from the XML and store them in the variables
    private void getDataFromXml() {
        username = etUsername.getText().toString();
        savings = Float.parseFloat(etSavings.getText().toString());
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rbWeekly:
                grouping = getResources().getString(R.string.pref_grouping_weekly);
                break;
            case R.id.rbMonthly:
                grouping = getResources().getString(R.string.pref_grouping_monthly);
                break;
            case R.id.rbDaily:
                grouping = getResources().getString(R.string.pref_grouping_daily);
                break;
            case R.id.rbNoGrouping:
                grouping = getResources().getString(R.string.pref_grouping_none);
                break;
        }

    }

}
