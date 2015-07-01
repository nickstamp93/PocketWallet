package com.ngngteam.pocketwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BarsDistributionActivity extends AppCompatActivity implements ActionBar.OnNavigationListener {

    MoneyDatabase moneyDatabase;
    CategoryDatabase categoryDatabase;

    ArrayList<String> allCategories;
    ArrayList<String> selectedCategoriesIncome, selectedCategoriesExpense, selectedCategories, backup;
    boolean isExpense;

    ArrayList<BarEntry> expenseValues;
    ArrayList<BarEntry> incomeValues;
    BarDataSet set1, set2;
    ArrayList<BarDataSet> dataSets;

    BarChart barChart;

    CardView cardCustomDate;

    int primaryTextColor;
    int selectedDropDownPos;

    String startDate, endDate;
    EditText etStart, etEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bars_distribution);

        init();

        initUI();

        setUpUI();

        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.bars_period_values, R.layout.action_dropdown_spinner_item);

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

        selectedCategoriesIncome = categoryDatabase.getCategories(false);
        selectedCategoriesExpense = categoryDatabase.getCategories(true);

        endDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(Calendar.getInstance().getTimeInMillis()));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -10);
        startDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(c.getTimeInMillis()));

    }

    private void initUI() {
        barChart = (BarChart) findViewById(R.id.barDistChart);

        cardCustomDate = (CardView) findViewById(R.id.cardview_bars_custom_date);

        etStart = (EditText) findViewById(R.id.etDateFrom);
        etEnd = (EditText) findViewById(R.id.etDateTo);

    }

    private void setUpUI() {

        customizeBarChart();


        etStart.setText(startDate);
        etEnd.setText(endDate);

        etStart.setOnClickListener(etDatesListener);
        etEnd.setOnClickListener(etDatesListener);
    }

    private void customizeBarChart() {

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.setDescription("");

        // if more than 12 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(10);

        // draw shadows for each bar that show the maximum value
        // barChart.setDrawBarShadow(true);

        // barChart.setDrawXLabels(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);

//        barChart.setPinchZoom(true);
        barChart.setScaleEnabled(true);
        barChart.setHighlightEnabled(false);


        barChart.setDrawHighlightArrow(false);

        // barChart.setDrawLegend(false);
//        if (barChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
//            Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
//                    Toast.LENGTH_SHORT).show();
//        } else
//            Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                    .show();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setTextColor(primaryTextColor);

        ValueFormatter custom = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "€";
            }
        };

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setTextColor(primaryTextColor);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextColor(primaryTextColor);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(primaryTextColor);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void initBarsMonths(int monthCount) {
        ArrayList<String> months = new ArrayList<>();
        expenseValues = new ArrayList<>();
        incomeValues = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        for (int i = 0; i < monthCount; i++) {
            int month = c.get(Calendar.MONTH);
            String sMonth = OverviewActivity.getMonthForInt(month);
            months.add(sMonth);
            double expenseAmount = moneyDatabase.getMonthTotal(c.get(Calendar.YEAR), month, true, selectedCategoriesExpense);
            double incomeAmount = moneyDatabase.getMonthTotal(c.get(Calendar.YEAR), month, false, selectedCategoriesIncome);
            expenseValues.add(new BarEntry((float) expenseAmount, i));
            incomeValues.add(new BarEntry((float) incomeAmount, i));
            if (month == 0) {
                c.add(Calendar.YEAR, -1);
                c.set(Calendar.MONTH, 11);
            } else {
                c.add(Calendar.MONTH, -1);
            }
        }

        setDataToBarChart(months, expenseValues, incomeValues);


    }

    private void initBarsWeeks(int weeksCount) {
        ArrayList<String> weeks = new ArrayList<>();
        expenseValues = new ArrayList<>();
        incomeValues = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        for (int i = 0; i < weeksCount; i++) {
            weeks.add("Week " + i);
            double expenseAmount = moneyDatabase.getWeekTotal(c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_YEAR), true, selectedCategoriesExpense);
            double incomeAmount = moneyDatabase.getWeekTotal(c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_YEAR), false, selectedCategoriesIncome);
            expenseValues.add(new BarEntry((float) expenseAmount, i));
            incomeValues.add(new BarEntry((float) incomeAmount, i));
            c.add(Calendar.DAY_OF_YEAR, -7);
        }

        setDataToBarChart(weeks, expenseValues, incomeValues);
    }

    private void initBarsTotal() {
        ArrayList<String> months = new ArrayList<>();
        expenseValues = new ArrayList<>();
        incomeValues = new ArrayList<>();

        double expenseAmount = moneyDatabase.getTotal(true, selectedCategoriesExpense);
        double incomeAmount = moneyDatabase.getTotal(false, selectedCategoriesIncome);

        months.add("Total");
        expenseValues.add(new BarEntry((float) expenseAmount, 0));
        incomeValues.add(new BarEntry((float) incomeAmount, 0));

        setDataToBarChart(months, expenseValues, incomeValues);
    }

    private void initBarsCustomPeriod() {
        ArrayList<String> months = new ArrayList<>();
        expenseValues = new ArrayList<>();
        incomeValues = new ArrayList<>();

        try {
            String sDate = new SimpleDateFormat("d MMMM yy").format(new SimpleDateFormat("dd-MM-yyyy").parse(startDate));
            String eDate = new SimpleDateFormat("d MMMM yy").format(new SimpleDateFormat("dd-MM-yyyy").parse(endDate));
            months.add(sDate + " - " + eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        double expenseAmount = moneyDatabase.getTotalCustomDate(startDate, endDate, true, selectedCategoriesExpense);
        double incomeAmount = moneyDatabase.getTotalCustomDate(startDate, endDate, false, selectedCategoriesIncome);
        expenseValues.add(new BarEntry((float) expenseAmount, 0));
        incomeValues.add(new BarEntry((float) incomeAmount, 0));


        setDataToBarChart(months, expenseValues, incomeValues);
    }

    private void setDataToBarChart(ArrayList<String> tags, ArrayList<BarEntry> expenseValues, ArrayList<BarEntry> incomeValues) {
        set1 = new BarDataSet(expenseValues, "Expenses");
        set2 = new BarDataSet(incomeValues, "Income");
        set1.setBarSpacePercent(20f);
        set1.setColor(getResources().getColor(R.color.bpRed));
        set2.setBarSpacePercent(20f);
        set2.setColor(getResources().getColor(R.color.YellowGreen));

        dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(tags, dataSets);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "€";
            }
        });
        data.setValueTextColor(primaryTextColor);
        data.setValueTextSize(10f);

        barChart.setData(data);

        barChart.animateXY(3000, 3000);
        barChart.invalidate();
    }


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
            initBarsCustomPeriod();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bars_distribution, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isExpense = true;
        if (item.getItemId() == R.id.action_expense_categories || item.getItemId() == R.id.action_income_categories) {
            if (item.getItemId() == R.id.action_expense_categories) {
                isExpense = true;
                selectedCategories = new ArrayList<>(selectedCategoriesExpense);
            } else if (item.getItemId() == R.id.action_income_categories) {
                isExpense = false;
                selectedCategories = new ArrayList<>(selectedCategoriesIncome);
            }
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
                            if (isExpense) {
                                selectedCategoriesExpense = new ArrayList<>(selectedCategories);
                            } else {
                                selectedCategoriesIncome = new ArrayList<>(selectedCategories);
                            }
                            onNavigationItemSelected(selectedDropDownPos, 0);
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

//            Log.i("nikos", "new dialog");
            builder.create().show();
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        selectedDropDownPos = itemPosition;
        if (itemPosition == 5) {

            cardCustomDate.setVisibility(View.VISIBLE);
            initBarsCustomPeriod();
        } else {

            cardCustomDate.setVisibility(View.GONE);
            if (itemPosition == 0) {
                initBarsMonths(3);
            } else if (itemPosition == 1) {
                initBarsWeeks(6);
            } else if (itemPosition == 2) {
                initBarsMonths(6);
            } else if (itemPosition == 3) {
                initBarsWeeks(12);
            } else if (itemPosition == 4) {
                initBarsTotal();
            }
        }


        return true;
    }
}
