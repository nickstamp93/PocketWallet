package com.ngngteam.pocketwallet.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

public class BarsDistributionActivity extends AppCompatActivity implements ActionBar.OnNavigationListener {

    MoneyDatabase moneyDatabase;
    CategoryDatabase categoryDatabase;

    BarChart barChart;

    int primaryTextColor;
    int selectedDropDownPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bars_distribution);

        init();

        initUI();

        setUpUI();

        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.bars_period_values, R.layout.support_simple_spinner_dropdown_item);

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

    }

    private void initUI() {
        barChart = (BarChart) findViewById(R.id.barDistChart);

    }

    private void setUpUI() {

        customizeBarChart();

    }

    private void customizeBarChart() {

    }

    private void initPieMonths(int months) {

    }


    private void initPieWeeks(int weeks) {

    }

    private void setDataToBarChart() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bars_distribution, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_expense_categories) {

        } else if (item.getItemId() == R.id.action_income_categories) {

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        selectedDropDownPos = itemPosition;
        if (itemPosition == 0) {

        } else if (itemPosition == 1) {

        } else if (itemPosition == 2) {

        } else if (itemPosition == 3) {

        }

        return true;
    }
}
