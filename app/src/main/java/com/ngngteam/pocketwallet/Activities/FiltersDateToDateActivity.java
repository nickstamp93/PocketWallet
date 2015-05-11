package com.ngngteam.pocketwallet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.util.Calendar;

/**
 * Created by Vromia on 15/9/2014.
 * FiltersDateToDateDialog class that extends DialogFragment is used when we want to find expenses or incomes with date between two
 * dates. HistoryActivity calls this class more specific in filters.
 */
public class FiltersDateToDateActivity extends AppCompatActivity {

    private Button bOk, bCancel;
    private ImageButton ibFrom, ibTo;
    private EditText etFrom, etTo;
    private String from, to;
    //variable flag is used because we want to know which button the user pressed so we initialise the right date
    //and variable expense is to check if this fragment is called for expenses or incomes
    private boolean flag;

    private CalendarDatePickerDialog Cdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_filters_datetodate);

        initUI();
        setUpUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    public void initUI() {

        etFrom = (EditText) findViewById(R.id.etDateFrom);
        ibFrom = (ImageButton) findViewById(R.id.ibFrom);

        etTo = (EditText) findViewById(R.id.etDateTo);
        ibTo = (ImageButton) findViewById(R.id.ibTo);

        Calendar c = Calendar.getInstance();
        String day = c.get(Calendar.DAY_OF_MONTH) + "";
        String month = (c.get(Calendar.MONTH) + 1) + "";
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        if (c.get(Calendar.MONTH) + 1 < 10) {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        String date = day + "-" + month + "-" + c.get(Calendar.YEAR);
        etFrom.setText(date);
        etTo.setText(date);

        bOk = (Button) findViewById(R.id.bOK);
        bCancel = (Button) findViewById(R.id.bCancel);

        Themer.setBackgroundColor(this, bOk, false);
        Themer.setBackgroundColor(this, bCancel, true);

    }

    public void setUpUI() {

        //from button listener
        ibFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                Calendar c = Calendar.getInstance();
                Cdialog = CalendarDatePickerDialog.newInstance(dtdListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(), "Calendar Dialog");

            }
        });

        //to button listener
        ibTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                Calendar c = Calendar.getInstance();
                Cdialog = CalendarDatePickerDialog.newInstance(dtdListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(), "Calendar Dialog");

            }
        });

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = getIntent();
                resultIntent.putExtra("From", etFrom.getText().toString());
                resultIntent.putExtra("To", etTo.getText().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();


            }
        });
        //cancel button listener
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

    private CalendarDatePickerDialog.OnDateSetListener dtdListener = new CalendarDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int cYear, int cMonth, int cDay) {

            String month, day, date;
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
            date = day + "-" + month + "-" + cYear;

            if (flag) {
                from = date;
                etFrom.setText(from);

            } else {
                to = date;
                etTo.setText(to);
            }
        }
    };

}
