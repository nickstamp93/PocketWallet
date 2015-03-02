package myexpenses.ng2.com.myexpenses.Activities;

//this is a comment for github
//this is a comment without being collaborator

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.Data.UserProfile;
import myexpenses.ng2.com.myexpenses.MainActivity;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.DrawerAdapter;
import myexpenses.ng2.com.myexpenses.Utils.PercentView;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;
import myexpenses.ng2.com.myexpenses.Utils.Themer;

public class OverviewActivity extends Activity {

    //request code for the UserDetails sub-Activity
    private static final int USER_DETAILS_SUB_ACTIVITY = 1;
    //SharedPrefsManager object
    private SharedPrefsManager manager;

    //UserProfile object
    private UserProfile profile;

    //View objects for the XML management
    private TextView tvBalance, tvSavings, tvLastIncome, tvLastExpense, tvUsername;
    private PercentView pv;
    private DrawerLayout drawerLayout;
    private ListView drawer;
    private ActionBarDrawerToggle drawerToggle;

    private MoneyDatabase mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //apply theme
        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_overview);

        //init UI elements
        initUI();

        //open money database
        mdb = new MoneyDatabase(OverviewActivity.this);

        //manage the user profile
        checkUserProfile();

        //if profile is on salary , must update its money status
        //this is if the user opened the app after a payment has occurred
//        if (profile instanceof UserProfileSalary) {
//            ((UserProfileSalary) profile).updateMoneyStatus();
//            //and refresh the UI accordingly
//            getDataFromSharedPrefs();
//            refreshUI();
//        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(new Intent(getBaseContext(), OverviewActivity.class));
        OverviewActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get data from the preference file and init the profile object
        getDataFromSharedPrefs();
        //refresh views according to the profile object values
        refreshUI();

//        Intent i = getIntent();
//        overridePendingTransition(0,0);
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        finish();
//        overridePendingTransition(0,0);
//        startActivity(i);

    }

    //checks if a user profile already exists(practically if the app launches for the first time)
    private void checkUserProfile() {

        //open the shared prefs manager
        manager = new SharedPrefsManager(getApplicationContext());

        //store the profile existence in a boolean variable
        boolean isProfile = manager.getPrefsIsProfile();

        //if there isn't a profile already , launch the user details activity to create one
        if (!isProfile) {
            Intent i = new Intent(getApplicationContext(), UserDetailsActivity.class);
            startActivityForResult(i, USER_DETAILS_SUB_ACTIVITY);
        } else {
            //else load the profile data from the shared prefs
            getDataFromSharedPrefs();
            //and refresh the UI elements
            refreshUI();
        }
    }

    //loads the user data from the shared prefs file to some variables
    //then initialize the profile object with these variables
    private void getDataFromSharedPrefs() {
        String username = manager.getPrefsUsername();
        //boolean onSalary = manager.getPrefsOnSalary();
        float savings = manager.getPrefsSavings();
        float balance = manager.getPrefsBalance();
//        String nextPaymentDate = manager.getPrefsNpd();
        String currency = manager.getPrefsCurrency();
        String grouping = manager.getPrefsGrouping();

        Cursor cExpense, cIncome;
        cExpense = mdb.getLastExpense();
        cIncome = mdb.getLastIncome();

        if (cExpense.moveToFirst()) {
            tvLastExpense.setText(cExpense.getString(1) + " " + cExpense.getString(2) + " " + cExpense.getDouble(3));
        }
        if (cIncome.moveToFirst()) {
            tvLastIncome.setText(cIncome.getString(1) + " " + cIncome.getString(2) + " " + cIncome.getString(3));
        }

//        tvLastExpense.setText(cExpense.getString(1) + " " + cExpense.getString(2) + " " + cExpense.getString(3));
//        tvLastIncome.setText(cIncome.getString(1) + " " + cIncome.getString(2) + " " + cIncome.getString(3));

//        if (onSalary) {
//            float salary = manager.getPrefsSalary();
//            String salFreq = manager.getPrefsSalFreq();
//
//            profile = new UserProfileSalary(OverviewActivity.this, username, savings, balance, salary, salFreq, nextPaymentDate, currency);
//            ((UserProfileSalary) profile).show();
//        } else {
//            profile = new UserProfile(username, savings, balance, currency);
//        }
        profile = new UserProfile(username, savings, balance, currency , grouping);
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

    //init the UI Views
    private void initUI() {
        tvBalance = (TextView) findViewById(R.id.tvBalanceAmount);
        tvSavings = (TextView) findViewById(R.id.tvSavings);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvLastExpense = (TextView) findViewById(R.id.tvLastExpense);
        tvLastIncome = (TextView) findViewById(R.id.tvLastIncome);

        pv = (PercentView) findViewById(R.id.percentview);
        pv.setVisibility(View.GONE);

        drawer = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        String activities[] = getResources().getStringArray(R.array.drawer_menu);
        DrawerAdapter adapter = new DrawerAdapter(OverviewActivity.this, R.layout.drawer_item, activities);

        drawer.setAdapter(adapter);
//        drawer.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.drawer_menu)));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setOnItemClickListener(drawerClickListener);


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Drawer Just Opened");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle("Drawer just closed");
                invalidateOptionsMenu();

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        //set a font for the text views
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font_exo2.otf");
        tvBalance.setTypeface(typeface);
        tvSavings.setTypeface(typeface);
        tvUsername.setTypeface(typeface);

    }

    //refresh UI according to profile object
    private void refreshUI() {
        tvUsername.setText(profile.getUsername());
        tvBalance.setText(String.valueOf(profile.getBalance()) + " " + profile.getCurrency());
        tvSavings.setText(String.valueOf(profile.getSavings()));

//        if (profile instanceof UserProfileSalary) {
//            tvDays.setText(String.valueOf(((UserProfileSalary) profile).getDaysToNextPayment()));
//        } else {
//            tvDays.setText("None");
//        }
        double priceOfExpenses;
        double priceOfIncomes;
        if(profile.getGrouping().equalsIgnoreCase("monthly")){
            priceOfExpenses = mdb.getTotalExpensePriceForCurrentMonth();
            priceOfIncomes = mdb.getTotalIncomePriceForCurrentMonth();
        }else{
            priceOfExpenses = mdb.getTotalExpensePriceForCurrentWeek();
            priceOfIncomes = mdb.getTotalIncomePriceForCurrentWeek();
        }

        double total = priceOfExpenses + priceOfIncomes;
        if (total != 0) {
            pv.setVisibility(View.VISIBLE);
            double percentOfExpenses = priceOfExpenses / total;
            pv.setPercentageExpense((float) percentOfExpenses * 100);
        }

//        double priceOfExpenses = mdb.getTotalExpensePriceForCurrentMonth();
//        double priceOfIncomes = mdb.getTotalIncomePriceForCurrentMonth();
//        double total = priceOfExpenses + priceOfIncomes;
        Log.i("Expense", priceOfExpenses + "");
        Log.i("Income", priceOfIncomes + "");

        //TODO the pie is not working right
//        if (total != 0) {
//            pv.setVisibility(View.VISIBLE);
//            double percentOfExpenses = priceOfExpenses / total;
//            pv.setPercentageExpense((float) percentOfExpenses * 100);
//        }
/*
        if(profile.getSavings() > 0){
            float percentBalance = (manager.getPrefsDifference()+manager.getPrefsBalance())/(manager.getPrefsBalance()+manager.getPrefsDifference()+manager.getPrefsSavings());
            float percentExpense = manager.getPrefsDifference()/(manager.getPrefsBalance()+manager.getPrefsDifference()+manager.getPrefsSavings());
            pv.setPercentageBalance(percentBalance*100);
            pv.setPercentageExpense(percentExpense * 100);
        }else{
            pv.setPercentageExpense(100);
        }
*/
    }


    private ListView.OnItemClickListener drawerClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    startActivity(new Intent(OverviewActivity.this, HistoryActivity.class));

                    break;
                case 1:
                    startActivity(new Intent(OverviewActivity.this, AddIncomeActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(OverviewActivity.this, AddExpenseActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(OverviewActivity.this, SettingsActivity2.class));
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
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawer);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
