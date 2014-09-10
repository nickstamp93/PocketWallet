package myexpenses.ng2.com.myexpenses.Activities;

//this is a comment for github
//this is a comment without being collaborator

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.Data.UserProfile;
import myexpenses.ng2.com.myexpenses.Data.UserProfileSalary;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class OverviewActivity extends Activity {

    //request code for the UserDetails sub-Activity
    private static final int USER_DETAILS_SUB_ACTIVITY = 1;
    //SharedPrefsManager object
    private SharedPrefsManager manager;

    //UserProfile object
    UserProfile profile;

    //View objects for the XML management
    TextView tvBalance, tvSavings, tvDays, tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        //init UI elements
        initUI();

        //manage the user profile
        checkUserProfile();

        //if profile is on salary , must update its money status
        //this is if the user opened the app after a payment has occured
        if (profile instanceof UserProfileSalary) {
            ((UserProfileSalary) profile).updateMoneyStatus();
            //and refresh the UI accordingly
            refreshUI();
        }

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
        boolean onSalary = manager.getPrefsOnSalary();
        float savings = manager.getPrefsSavings();
        float balance = manager.getPrefsBalance();
        String nextPaymentDate = manager.getPrefsNpd();
        String currency = manager.getPrefsCurrency();

        if (onSalary) {
            float salary = manager.getPrefsSalary();
            boolean bonus = manager.getPrefsBonus();
            String salFreq = manager.getPrefsSalFreq();

            profile = new UserProfileSalary(username, savings, balance, bonus, salary, salFreq, nextPaymentDate , currency);
            ((UserProfileSalary) profile).show();
        } else {
            profile = new UserProfile(username, savings, balance , currency);
        }
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
                    //and refres the UI
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
        tvDays = (TextView) findViewById(R.id.tvDays);
        tvUsername = (TextView) findViewById(R.id.tvUsername);

        Typeface typeface = Typeface.createFromAsset(getAssets() , "fonts/font_exo2.otf");
        tvDays.setTypeface(typeface);
        tvBalance.setTypeface(typeface);
        tvSavings.setTypeface(typeface);
        tvUsername.setTypeface(typeface);

    }

    //fill the xml elements attributes with the user details
    private void refreshUI() {
        tvUsername.setText(profile.getUsername());
        tvBalance.setText(String.valueOf(profile.getBalance()) + " " + profile.getCurrency());
        tvSavings.setText(String.valueOf(profile.getSavings()));

        if (profile instanceof UserProfileSalary) {
            tvDays.setText(String.valueOf(((UserProfileSalary) profile).getDaysToNextPayment()));
        } else {
            tvDays.setText("You are not currently working anywhere with a standard salary");
        }
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
