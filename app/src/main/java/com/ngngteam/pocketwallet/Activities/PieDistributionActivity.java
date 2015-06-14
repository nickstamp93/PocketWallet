package com.ngngteam.pocketwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PieDistributionActivity extends AppCompatActivity implements ActionBar.OnNavigationListener {

    MoneyDatabase moneyDatabase;
    CategoryDatabase categoryDatabase;

    ArrayList<String> allCategories;
    ArrayList<String> finalCategories;
    ArrayList<Entry> values;
    ArrayList<Integer> colors;
    double totalAmount;
    String centerText;

    ArrayList<String> selectedCategories, backup;

    PieChart pieChart;
    Switch switchIsExpense;
    EditText etStart, etEnd;
    ImageButton bGo;
    CardView cardCustomDate;

    private boolean isExpense;

    private String startDate, endDate;

    int primaryTextColor;
    int selectedDropDownPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pie_distribution);

        init();

        initUI();

        setUpUI();

        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.pie_period_values, R.layout.action_dropdown_spinner_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setListNavigationCallbacks(adapter, this);


    }

    private void init() {

        moneyDatabase = new MoneyDatabase(this);
        categoryDatabase = new CategoryDatabase(this);

        //get attribute "textCustomColor" value
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.textCustomColor, typedValue, true);
        primaryTextColor = typedValue.data;

        isExpense = true;

        allCategories = categoryDatabase.getCategories(isExpense);
        selectedCategories = allCategories;


        startDate = endDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(Calendar.getInstance().getTimeInMillis()));


    }

    private void initUI() {
        pieChart = (PieChart) findViewById(R.id.pieDistChart);

        switchIsExpense = (Switch) findViewById(R.id.switchIsExpense);

        cardCustomDate = (CardView) findViewById(R.id.cardview_pie_custom_date);
        etStart = (EditText) findViewById(R.id.etDateFrom);
        etEnd = (EditText) findViewById(R.id.etDateTo);

        bGo = (ImageButton) findViewById(R.id.bPieGo);
    }

    private void setUpUI() {

        customizePieChart();

        switchIsExpense.setOnCheckedChangeListener(switchListener);

        etStart.setText(startDate);
        etEnd.setText(endDate);

        etStart.setOnClickListener(etDatesListener);
        etEnd.setOnClickListener(etDatesListener);

        bGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPieCustomPeriod();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pie_distribution, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_filters_pie) {
            allCategories = categoryDatabase.getCategories(isExpense);
            boolean[] checked = new boolean[allCategories.size()];
            //check the items that are previously checked
            for (int i = 0; i < checked.length; i++) {
                if (selectedCategories.contains(allCategories.get(i))) {
                    checked[i] = true;
                }
            }
            backup = new ArrayList<>(selectedCategories);
            CharSequence[] cs = allCategories.toArray(new CharSequence[allCategories.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the dialog title
            builder.setTitle("Choose categories")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(cs, checked,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        selectedCategories.add(allCategories.get(which));
                                    } else if (selectedCategories.contains(allCategories.get(which))) {
                                        // Else, if the item is already in the array, remove it
                                        selectedCategories.remove(allCategories.get(which));
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog
                            onNavigationItemSelected(selectedDropDownPos, 0);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedCategories = backup;
                        }
                    });

            builder.create().show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void customizePieChart() {
        pieChart.setHoleColorTransparent(true);

        //activate hole and define the size
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);

        //draw text in the center
        pieChart.setDrawCenterText(true);
        //define the size of the text in the center of the pie

        //center text color set to primary
        pieChart.setCenterTextColor(primaryTextColor);

        pieChart.setDescription("");

        pieChart.getLegend().setEnabled(false);
    }

    private void initPieCurrentMonth() {
        totalAmount = 0;

        int days[] = {1, 5, 10, 15, 20, 25};
        int firstDay = days[new SharedPrefsManager(this).getPrefsDayStart()];

        values = new ArrayList<>();
        finalCategories = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i < allCategories.size(); i++) {
            double amount = moneyDatabase.getMonthTotalForCategory(allCategories.get(i), isExpense);
            //if this category has positive amount
            if (amount > 0 && selectedCategories.contains(allCategories.get(i))) {
                totalAmount += amount;
                //add it in the pie
                Entry e = new Entry(
                        (float) amount, i);
                values.add(e);
                finalCategories.add(allCategories.get(i));
                colors.add(categoryDatabase.getColorFromCategory(allCategories.get(i), isExpense));
            }
        }
        totalAmount = Math.round(totalAmount * 100) / 100.0;

        //get the current month's name
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);

        //if first day of Month is 1 , then show month name
        if (firstDay == 1) {

            //get month name
            String sMonth = OverviewActivity.getMonthForInt(month);

            centerText = sMonth;
            //else show the period
        } else {


            Date startDate, endDate;

            c.set(Calendar.DAY_OF_MONTH, firstDay);
            startDate = c.getTime();

            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DAY_OF_MONTH, -1);
            endDate = c.getTime();

            centerText = getString(R.string.text_period) + "\n("
                    + new SimpleDateFormat("dd MMM").format(startDate) + "-" +
                    new SimpleDateFormat("dd MMM").format(endDate) + ")";

        }

        //and set it to the center text
        pieChart.setCenterTextSize(20f);
//        centerText = sMonth;
        String mode = isExpense ? "Expense" : "Income";

        setDataToPie(values, finalCategories, colors, mode);
    }

    private void initPieCurrentWeek() {
        totalAmount = 0;

        int days[] = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
                Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
        int firstDay = days[new SharedPrefsManager(this).getPrefsDayStart()];

        values = new ArrayList<>();
        finalCategories = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i < allCategories.size(); i++) {
            double amount = moneyDatabase.getWeekTotalForCategory(firstDay, allCategories.get(i), isExpense);
            //if this category has positive amount
            if (amount > 0 && selectedCategories.contains(allCategories.get(i))) {
                totalAmount += amount;
                //add it in the pie
                Entry e = new Entry(
                        (float) amount, i);
                values.add(e);
                finalCategories.add(allCategories.get(i));
                colors.add(categoryDatabase.getColorFromCategory(allCategories.get(i), isExpense));
            }
        }
        totalAmount = Math.round(totalAmount * 100) / 100.0;

        Calendar c = Calendar.getInstance();

        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int endDay = firstDay;

        Date startDate, endDate;

        if (endDay == currentDay) {
            startDate = c.getTime();
            c.add(Calendar.DAY_OF_YEAR, 6);
            endDate = c.getTime();
        } else {
            while (currentDay != endDay) {
                c.add(Calendar.DATE, 1);
                currentDay = c.get(Calendar.DAY_OF_WEEK);
            }
            c.add(Calendar.DAY_OF_YEAR, -1);
            endDate = c.getTime();
            c.add(Calendar.DAY_OF_YEAR, -6);
            startDate = c.getTime();
        }
        if (startDate.getMonth() == endDate.getMonth()) {
            centerText = new SimpleDateFormat("dd").format(startDate)
                    + "-" + new SimpleDateFormat("dd MMM").format(endDate);
        } else {
            centerText = new SimpleDateFormat("dd MMM").format(startDate)
                    + "-" + new SimpleDateFormat("dd MMM").format(endDate);
        }

        pieChart.setCenterTextSize(25f);
        pieChart.setCenterText(centerText);
        String mode = isExpense ? "Expense" : "Income";

        setDataToPie(values, finalCategories, colors, mode);
    }

    private void initPieCustomPeriod() {
        //TODO find a way to let user define start and end date of period
        totalAmount = 0;

        int count = allCategories.size();

        values = new ArrayList<>();
        finalCategories = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double amount = moneyDatabase.getTotalForCategoryCustomDate(startDate, endDate, allCategories.get(i), isExpense);
            //if this category has positive amount
            if (amount > 0 && selectedCategories.contains(allCategories.get(i))) {
                totalAmount += amount;
                //add it in the pie
                Entry e = new Entry(
                        (float) amount, i);
                values.add(e);
                finalCategories.add(allCategories.get(i));
                colors.add(categoryDatabase.getColorFromCategory(allCategories.get(i), isExpense));
            }
        }
        totalAmount = Math.round(totalAmount * 100) / 100.0;

        String mode = isExpense ? "Expense" : "Income";

        try {
            Date sDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
            Date eDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);
            if (startDate.split("-")[1].equals(endDate.split("-")[1])) {

                centerText = new SimpleDateFormat("dd").format(sDate) +
                        "-" + new SimpleDateFormat("dd MMM").format(eDate);
            } else {

                centerText = new SimpleDateFormat("dd").format(sDate) +
                        "-" + new SimpleDateFormat("dd MMM").format(eDate);
            }

            pieChart.setCenterTextSize(20f);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        setDataToPie(values, finalCategories, colors, mode);
    }

    private void setDataToPie(ArrayList values, ArrayList finalCategories, ArrayList colors, String mode) {
        PieDataSet dataSet = new PieDataSet(values, mode + " Distribution");

        //set slice width
        dataSet.setSliceSpace(2f);
        //set the colors in the pie
        dataSet.setColors(colors);

        //set the categories
        PieData data = new PieData(finalCategories, dataSet);
        //set the formatter either a percent or currency
//        data.setValueFormatter(new PercentFormatter());
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return value +
                        " " + PreferenceManager.getDefaultSharedPreferences(PieDistributionActivity.this)
                        .getString(getString(R.string.pref_key_currency), "€");
            }
        });

        //set the color of the values
        data.setValueTextColor(primaryTextColor);
        //set text size of values
        data.setValueTextSize(15f);
        //set data to the pie
        pieChart.setData(data);

        if (finalCategories.size() == 0) {
            pieChart.setDrawCenterText(false);
        } else {
            pieChart.setDrawCenterText(true);
            if (totalAmount == (int) totalAmount) {
                pieChart.setCenterText(centerText + "\n" + (int) totalAmount +
                        PreferenceManager.getDefaultSharedPreferences(PieDistributionActivity.this)
                                .getString(getString(R.string.pref_key_currency), "€"));
            } else {
                pieChart.setCenterText(centerText + "\n" + totalAmount +
                        PreferenceManager.getDefaultSharedPreferences(PieDistributionActivity.this)
                                .getString(getString(R.string.pref_key_currency), "€"));
            }
        }

        // undo all highlights
        pieChart.highlightValues(null);

        //animate pie createion
        pieChart.animateXY(1000, 1000);
        //invalidate
//        barChart.animateXY(1500 , 1500);
        pieChart.invalidate();
    }

    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isExpense = !isChecked;
            allCategories = categoryDatabase.getCategories(isExpense);
            selectedCategories = allCategories;
            onNavigationItemSelected(selectedDropDownPos, 0);
        }
    };

    private View.OnClickListener etDatesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            if (v.getId() == R.id.etDateFrom) {
                bundle.putBoolean("isStartDate", true);
            } else {
                bundle.putBoolean("isStartDate", false);
            }
            CalendarDatePickerDialog dialog = new CalendarDatePickerDialog();
            dialog.setOnDateSetListener(dateSetListener);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "Date");
        }
    };

    private CalendarDatePickerDialog.OnDateSetListener dateSetListener = new CalendarDatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            Date d = new Date(c.getTimeInMillis());
            String date = new SimpleDateFormat("dd-MM-yyyy").format(d);
            if (calendarDatePickerDialog.getArguments().getBoolean("isStartDate")) {
                startDate = date;
                etStart.setText(startDate);
            } else {
                endDate = date;
                etEnd.setText(endDate);
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        selectedDropDownPos = itemPosition;
        if (itemPosition == 2) {
            cardCustomDate.setVisibility(View.VISIBLE);
        } else {
            cardCustomDate.setVisibility(View.INVISIBLE);

            if (itemPosition == 0) {
                initPieCurrentMonth();

            } else if (itemPosition == 1) {

                initPieCurrentWeek();

            }
        }
        return true;
    }
}
