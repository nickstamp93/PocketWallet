package com.ngngteam.pocketwallet.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Adapters.DrawerAdapter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.Extra.MagnificentChart;
import com.ngngteam.pocketwallet.Extra.MagnificentChartItem;
import com.ngngteam.pocketwallet.Model.ExpenseItem;
import com.ngngteam.pocketwallet.Model.IncomeItem;
import com.ngngteam.pocketwallet.Model.UserProfile;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewOverviewActivity extends AppCompatActivity {

    //request code for the UserDetails sub-Activity
    private static final int USER_DETAILS_SUB_ACTIVITY = 1;
    //SharedPrefsManager object
    private SharedPrefsManager manager;

    //UserProfile object
    private UserProfile profile;

    //View objects for the XML management
    private TextView tvBalance, tvSavings, tvLastIncomeValue, tvLastExpenseValue, tvLastExpenseDate,
            tvLastIncomeDate, tvUsername, tvPieHeading, tvLegendTotalExpense, tvLegendTotalIncome;
    private LinearLayout llBalance , llLastExpense, llLastIncome;
    private CardView card_message , card_pie , card_last_transactions;
    private LetterImageView livLastExpense, livLastIncome, livLegendIncome, livLegendExpense;
    private DrawerLayout drawerLayout;
    private ListView drawer;
    private ActionBarDrawerToggle drawerToggle;

    private MagnificentChart mcPie;

    private MoneyDatabase mdb;

    private Cursor cursorLastExpense, cursorLastIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_new_overview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        init();

        //init UI elements
        initUI();

        //set up UI elements
        setUpUI();

        //manage the user profile
        checkUserProfile();

        initListeners();

    }

    private void setUpUI() {

        //==================================Navigation Drawer=======================================
        //get the string array with the Navigation drawer items
        String drawerItems[] = getResources().getStringArray(R.array.drawer_menu);

        //init the adapter and connect with the View
        DrawerAdapter adapter = new DrawerAdapter(NewOverviewActivity.this, R.layout.drawer_item, drawerItems);
        drawer.setAdapter(adapter);

        //set on drawer item click listener
        drawer.setOnItemClickListener(drawerClickListener);

        //set shadow for the navigation drawer
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();

            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (manager.getPrefsThemeChanged()) {
            startActivity(new Intent(getBaseContext(), NewOverviewActivity.class));
            NewOverviewActivity.this.finish();

            manager = new SharedPrefsManager(NewOverviewActivity.this);
            manager.startEditing();
            manager.setPrefsThemeChanged(false);
            manager.commit();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //get data from the preference file and init the profile object
        getDataFromSharedPrefs();
        //refresh views according to the profile object values
        refreshUI();

    }

    //checks if a user profile already exists(practically if the app launches for the first time)
    private void checkUserProfile() {

        //store the profile existence in a boolean variable
        boolean isProfile = manager.getPrefsIsProfile();

        //if there isn't a profile already , launch the user details activity to create one
        if (!isProfile) {
            startActivityForResult(new Intent(getApplicationContext(), UserDetailsActivity.class), USER_DETAILS_SUB_ACTIVITY);
        } else {
            //else load the profile data from the shared prefs
            getDataFromSharedPrefs();
            //and refresh the UI elements
            refreshUI();
        }
    }

    //loads the user data from the shared prefs file
    //then initialize the profile object with these variables
    private void getDataFromSharedPrefs() {

        String username = manager.getPrefsUsername();
        float savings = manager.getPrefsSavings();
        float balance = manager.getPrefsBalance();
        String currency = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_key_currency), getResources().getString(R.string.pref_currency_default_value));
        String grouping = manager.getPrefsGrouping();

        profile = new UserProfile(username, savings, balance, currency, grouping);
    }

    //is called when a sub-Activity with the result code returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //each sub-Activity has a different request code
        //here we have only one sub-Activity
        switch (requestCode) {
            case USER_DETAILS_SUB_ACTIVITY:
                //if the user confirmed the creation of the profile
                //get these data from the shared prefs file
                if (resultCode == RESULT_OK) {
                    getDataFromSharedPrefs();
                    //and refresh the UI
                    refreshUI();
                } else {
                    //else close the app(OverviewActivity is the main Activity)
                    finish();
                    break;
                }
        }
    }

    //init some basic variables
    private void init() {

        //shared preferences
        manager = new SharedPrefsManager(this);

        //open money database
        mdb = new MoneyDatabase(NewOverviewActivity.this);

    }

    //init the UI Views
    private void initUI() {

        //name
        tvUsername = (TextView) findViewById(R.id.tvUsername);

        tvBalance = (TextView) findViewById(R.id.tvOverviewBalance);
        tvSavings = (TextView) findViewById(R.id.tvOverviewSavings);

        //=================Message section====================================================
        card_message = (CardView) findViewById(R.id.cardview_message);

        //=================Second section , Pie - Pie Heading - Pie Legends===================
        card_pie = (CardView) findViewById(R.id.cardview_pie);
        card_pie.setVisibility(View.GONE);

        mcPie = (MagnificentChart) findViewById(R.id.mcPie);

        tvPieHeading = (TextView) findViewById(R.id.tvPieHeading);

        livLegendExpense = (LetterImageView) findViewById(R.id.livLegendExpense);
        livLegendIncome = (LetterImageView) findViewById(R.id.livLegendIncome);
        livLegendExpense.setmBackgroundPaint(getResources().getColor(R.color.red));
        livLegendIncome.setmBackgroundPaint(getResources().getColor(R.color.green));
        livLegendExpense.setOval(true);
        livLegendIncome.setOval(true);

        tvLegendTotalExpense = (TextView) findViewById(R.id.tvLegendExpenseText);
        tvLegendTotalIncome = (TextView) findViewById(R.id.tvLegendIncomeText);

        //==================Third Section , Last Transactions=================================
        card_last_transactions = (CardView) findViewById(R.id.cardview_last_transactions);
        card_last_transactions.setVisibility(View.GONE);

        llLastExpense = (LinearLayout) findViewById(R.id.llOverviewLastExpense);
        llLastIncome = (LinearLayout) findViewById(R.id.llOverviewLastIncome);

        tvLastExpenseValue = (TextView) findViewById(R.id.tvLastExpenseValue);
        tvLastIncomeValue = (TextView) findViewById(R.id.tvLastIncomeValue);

        tvLastExpenseDate = (TextView) findViewById(R.id.tvLastExpenseDate);
        tvLastIncomeDate = (TextView) findViewById(R.id.tvLastIncomeDate);

        livLastExpense = (LetterImageView) findViewById(R.id.livLastExpense);
        livLastIncome = (LetterImageView) findViewById(R.id.livLastIncome);

        //==================Navigation Drawer=================================================
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer = (ListView) findViewById(R.id.nav_drawer);
//        Themer.setDrawerBackground(this, drawer);


    }

    String getMonthForInt(int num) {
        String month = "January";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    //refresh UI according to profile object
    private void refreshUI() {

        tvUsername.setText(profile.getUsername());

        double totalExpenses;
        double totalIncomes;
        if (profile.getGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_monthly))) {
            totalExpenses = mdb.getTotalExpensePriceForCurrentMonth();
            totalIncomes = mdb.getTotalIncomePriceForCurrentMonth();
        } else {
            totalExpenses = mdb.getTotalExpensePriceForCurrentWeek();
            totalIncomes = mdb.getTotalIncomePriceForCurrentWeek();
        }

        totalExpenses = Math.round(totalExpenses * 100) / 100.0;
        totalIncomes = Math.round(totalIncomes * 100) / 100.0;

        double balance = totalIncomes - totalExpenses;
        balance = Math.round(balance * 100) / 100.0;
        //set the balance to the user profile object
        profile.setBalance((float) balance);

        //set balance to UI
        tvBalance.setText(balance + " " + profile.getCurrency());
        //set legends' text
        tvLegendTotalExpense.setText(getResources().getString(R.string.action_expense) + "\n(" + (totalExpenses + " " + profile.getCurrency()) + ")");
        tvLegendTotalIncome.setText(getResources().getString(R.string.action_income) + "\n(" + (totalIncomes + " " + profile.getCurrency()) + ")");

        //savings are : total income - total expense - balance + savings user defined at the creation of the profile
        double savings = mdb.getTotalIncome() - mdb.getTotalExpenses() - balance + profile.getSavings();
        profile.setSavings((float) savings);
        savings = Math.round(savings * 100) / 100.0;
        tvSavings.setText(savings + " " + profile.getCurrency());

        //set up the PieChart
        List<MagnificentChartItem> chartItemsList = new ArrayList<MagnificentChartItem>();

        double total = totalExpenses + totalIncomes;
        //if there is income or expense for the current period (week or month)
        if (total != 0) {

            mcPie.setMaxValue(total);

            //expense part of the pie chart
            MagnificentChartItem item = new MagnificentChartItem("Expense", totalExpenses, getResources().getColor(R.color.red));
            chartItemsList.add(item);

            //income part of the pie chart
            item = new MagnificentChartItem("Income", totalIncomes, getResources().getColor(R.color.green));
            chartItemsList.add(item);

            //set the parts to the pie chart
            mcPie.setChartItemsList(chartItemsList);

            mcPie.setAnimationSpeed(MagnificentChart.ANIMATION_SPEED_FAST);

            //show the pie after set up
            card_pie.setVisibility(View.VISIBLE);
            card_message.setVisibility(View.GONE);

        } else {
            //else the pie section should be invisible
            card_pie.setVisibility(View.GONE);
            card_message.setVisibility(View.VISIBLE);
        }

        //set the heading of the PieChart according to the preferred grouping
        if (manager.getPrefsGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_weekly))) {

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
                tvPieHeading.setText(getString(R.string.text_pie_heading) + "\n("
                        + new SimpleDateFormat("dd").format(startDate) + "-" +
                        new SimpleDateFormat("dd MMM").format(endDate) + ")");
            } else {
                tvPieHeading.setText(getString(R.string.text_pie_heading) + "\n("
                        + new SimpleDateFormat("dd MMM").format(startDate) + "-" +
                        new SimpleDateFormat("dd MMM").format(endDate) + ")");
            }

            //tvPieHeading.setText(getResources().getString(R.string.text_pie_heading));
            tvPieHeading.setTextSize(getResources().getInteger(R.integer.text_size_pie_heading_small));
        } else {
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);

            //get month name
            String sMonth = getMonthForInt(month);

            tvPieHeading.setText(sMonth);
            tvPieHeading.setTextSize(getResources().getInteger(R.integer.text_size_pie_heading));
        }

        //  Cursor cursorLastExpense, cursorLastIncome;
        cursorLastExpense = mdb.getExpensesFromNewestToOldest();
        cursorLastIncome = mdb.getIncomeByNewestToOldest();

        //if there isn't currently an expense
        if (cursorLastExpense.moveToFirst()) {
            card_last_transactions.setVisibility(View.VISIBLE);
            llLastExpense.setVisibility(View.VISIBLE);

            //get last expense's date
            String date = cursorLastExpense.getString(2);
            String tokens[] = date.split("-");
            date = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                Date item_date = format.parse(date);
                item_calendar.setTime(item_date);

                boolean isToday = today.get(Calendar.YEAR) == item_calendar.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_calendar.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_calendar.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_calendar.get(Calendar.DAY_OF_YEAR);
                if (isToday) {
                    tvLastExpenseDate.setText(getString(R.string.text_today));
                } else if (isYesterday) {
                    tvLastExpenseDate.setText(getString(R.string.text_yesterday));
                } else {
                    tvLastExpenseDate.setText(new SimpleDateFormat("dd MMMM").format(item_date));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //and fill the views with the values
            tvLastExpenseValue.setText(cursorLastExpense.getDouble(3) + " " + profile.getCurrency());

            //open category database
            CategoryDatabase cdb = new CategoryDatabase(this);

            //and get the right color and letter according to the last expense category
            int color = cdb.getColorFromCategory(cursorLastExpense.getString(1), true);
            char letter = cdb.getLetterFromCategory(cursorLastExpense.getString(1), true);
            cdb.close();

            //set the letter image views accordingly
            livLastExpense.setLetter(letter);
            livLastExpense.setmBackgroundPaint(color);


        } else {
            //else disappear the last expense module
            llLastExpense.setVisibility(View.GONE);
        }
        if (cursorLastIncome.moveToFirst()) {
            card_last_transactions.setVisibility(View.VISIBLE);
            llLastIncome.setVisibility(View.VISIBLE);

            //get last income's date
            String date = cursorLastIncome.getString(3);
            String tokens[] = date.split("-");
            date = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                Date item_date = format.parse(date);
                item_calendar.setTime(item_date);

                boolean isToday = today.get(Calendar.YEAR) == item_calendar.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_calendar.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_calendar.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_calendar.get(Calendar.DAY_OF_YEAR);
                if (isToday) {
                    tvLastIncomeDate.setText(getString(R.string.text_today));
                } else if (isYesterday) {
                    tvLastIncomeDate.setText(getString(R.string.text_yesterday));
                } else {
                    tvLastIncomeDate.setText(new SimpleDateFormat("dd MMMM").format(item_date));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //and fill the views with the values
            tvLastIncomeValue.setText(cursorLastIncome.getDouble(1) + " " + profile.getCurrency());

            //open category database
            CategoryDatabase cdb = new CategoryDatabase(this);

            //and get the right color and letter according to the last expense category
            int color = cdb.getColorFromCategory(cursorLastIncome.getString(2), false);
            char letter = cdb.getLetterFromCategory(cursorLastIncome.getString(2), false);

            livLastIncome.setLetter(letter);
            livLastIncome.setmBackgroundPaint(color);

            cdb.close();

        } else {
            //else disappear the last income module
            llLastIncome.setVisibility(View.GONE);
        }
        //if neither expense or income exists , disappear the last transactions section
        if (!cursorLastIncome.moveToFirst() && !cursorLastExpense.moveToFirst()) {
            card_last_transactions.setVisibility(View.GONE);
        }

    }


    private void initListeners() {

        llLastExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursorLastExpense.moveToFirst();
                Intent processExpense = new Intent(NewOverviewActivity.this, AddExpenseActivity.class);
                ExpenseItem expense = new ExpenseItem(cursorLastExpense.getString(1), cursorLastExpense.getString(4), Double.parseDouble(cursorLastExpense.getString(3)), cursorLastExpense.getString(2));
                expense.setId(Integer.parseInt(cursorLastExpense.getString(0)));
                processExpense.putExtra("Expense", expense);
                startActivity(processExpense);
            }
        });

        llLastIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursorLastIncome.moveToFirst();
                Intent processIncome = new Intent(NewOverviewActivity.this, AddIncomeActivity.class);
                IncomeItem income = new IncomeItem(Double.parseDouble(cursorLastIncome.getString(1)), cursorLastIncome.getString(3), cursorLastIncome.getString(2));
                income.setId(Integer.parseInt(cursorLastIncome.getString(0)));
                processIncome.putExtra("Income", income);
                startActivity(processIncome);
            }
        });


    }


    private ListView.OnItemClickListener drawerClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    startActivity(new Intent(NewOverviewActivity.this, HistoryActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(NewOverviewActivity.this, AddIncomeActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(NewOverviewActivity.this, AddExpenseActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(NewOverviewActivity.this, SettingsActivity.class));
                    break;
                case 4:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        boolean drawerOpen = drawerLayout.isDrawerOpen(drawer);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_addExpense) {
            startActivity(new Intent(getApplicationContext(), AddExpenseActivity.class));
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //if drawer is open , close it
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        //else , default action
        else {
            super.onBackPressed();
        }
    }
}
