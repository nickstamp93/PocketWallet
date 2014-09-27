package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CurrencyDialog;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class SettingsActivity extends Activity {

    //Shared Preferences Manager
    SharedPrefsManager manager;

    //UI elements
    TextView tvCurrency, tvCategories, tvRateApp, tvAbout, tvReminder, tvPassword;
    LinearLayout llReminder, llCurrency, llCategories, llPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //init manager
        manager = new SharedPrefsManager(getApplicationContext());

        //init UI
        initUI();

        //init listeners
        initListeners();
    }

    //on restarting the activity , the Reminder option might have changed
    @Override
    protected void onRestart() {
        super.onRestart();
        if (manager.getPrefsReminder()) {
            tvReminder.setText(manager.getPrefsReminderTime());
        } else {
            tvReminder.setText("Disabled");
        }
    }

    //init listeners
    private void initListeners() {
        llReminder.setOnClickListener(clickListener);
        llCurrency.setOnClickListener(clickListener);
        llCategories.setOnClickListener(clickListener);
        llPassword.setOnClickListener(clickListener);

        tvAbout.setOnClickListener(clickListener);

    }


    //init UI
    private void initUI() {
        tvReminder = (TextView) findViewById(R.id.tvReminder);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvRateApp = (TextView) findViewById(R.id.tvRateApp);
        tvAbout = (TextView) findViewById(R.id.tvAbout);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        llCurrency = (LinearLayout) findViewById(R.id.llCurrency);
        llCategories = (LinearLayout) findViewById(R.id.llCategories);
        llReminder = (LinearLayout) findViewById(R.id.llReminder);
        llPassword = (LinearLayout) findViewById(R.id.llPassword);

        //initial values according to prefs file
        tvCurrency.setText(manager.getPrefsCurrency());
        if (manager.getPrefsReminder()) {
            tvReminder.setText(manager.getPrefsReminderTime());
        } else {
            tvReminder.setText("Disabled");
        }
        if (manager.getPrefsIsPassword()) {
            tvPassword.setText("Enabled");
        } else {
            tvPassword.setText("Disabled");
        }

    }

    //click listener
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llReminder:
                    //start the activity for configuring the daily reminder
                    startActivity(new Intent(getApplicationContext(), ReminderSettingsActivity.class));
                    break;
                case R.id.llCurrency:
                    //open a dialog for changing global Currency
                    new CurrencyDialog().show(getFragmentManager(), "Currency Dialog");
                    break;
                case R.id.llPassword:
                    //open activity for configuring password settings
                    startActivity(new Intent(getApplicationContext(), PasswordSettingsActivity.class));
                    break;
                case R.id.llCategories:
                    //start activity for managing(adding/deleting) categories
                    startActivity(new Intent(getApplicationContext(), CategoriesManagerActivity.class));
                    break;
                case R.id.tvAbout:

                    //open a dialog with info about us , libraries , version etc

                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                    builder.setMessage("This is an application for managing your personal expenses and incomes" +
                            "\n\n\nCreated by Stampoulis Nikos and Zissis Nikos." +
                            "\n\nNo external libraries were used in this project" +
                            "\n\nSpecial thanks to A,B,C,D for the icons used in the app")
                            .setTitle("About Us");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    };

}
