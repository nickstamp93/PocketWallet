package com.ngngteam.pocketwallet.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

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

    PieChart pieChart;
    Switch switchIsExpense;

    private boolean isExpense;

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
                R.array.pie_period_values, R.layout.support_simple_spinner_dropdown_item);

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

    }

    private void initUI() {
        pieChart = (PieChart) findViewById(R.id.pieDistChart);
        customizePieChart();

        switchIsExpense = (Switch) findViewById(R.id.switchIsExpense);
    }

    private void setUpUI() {

        switchIsExpense.setOnCheckedChangeListener(switchListener);

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

        pieChart.getLegend().setEnabled(false);
        pieChart.setDescriptionColor(primaryTextColor);
        pieChart.setDescriptionTextSize(25);
    }

    private void initPieCurrentMonth() {
        totalAmount = 0;

        allCategories = categoryDatabase.getCategories(isExpense);
        int count = allCategories.size();

        values = new ArrayList<>();
        finalCategories = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double amount = moneyDatabase.getMonthTotalForCategory(allCategories.get(i), isExpense);
            //if this category has positive amount
            if (amount > 0) {
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
        String sMonth = OverviewActivity.getMonthForInt(month);

        //and set it to the center text
        pieChart.setCenterTextSize(40f);
        centerText = sMonth;
        String mode = isExpense ? "Expense" : "Income";

        setDataToPie(values, finalCategories, colors, mode);
    }

    private void initPieCurrentWeek() {
        totalAmount = 0;

        allCategories = categoryDatabase.getCategories(isExpense);
        int count = allCategories.size();

        values = new ArrayList<>();
        finalCategories = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double amount = moneyDatabase.getWeekTotalForCategory(allCategories.get(i), isExpense);
            //if this category has positive amount
            if (amount > 0) {
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
        int endDay = Calendar.MONDAY;

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

                return value + " " +
                        PreferenceManager.getDefaultSharedPreferences(PieDistributionActivity.this)
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

        pieChart.setDescription(isExpense ? "Expense" : "Income");
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
            onNavigationItemSelected(selectedDropDownPos, 0);
        }
    };

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        selectedDropDownPos = itemPosition;
        if (itemPosition == 0) {
            initPieCurrentMonth();

        } else if (itemPosition == 1) {

            initPieCurrentWeek();

        } else if (itemPosition == 2) {

        }
        return false;
    }
}
