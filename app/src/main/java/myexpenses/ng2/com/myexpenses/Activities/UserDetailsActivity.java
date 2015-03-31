package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;
import myexpenses.ng2.com.myexpenses.Utils.Themer;

public class UserDetailsActivity extends Activity {

    //SharedPrefsManager object
    SharedPrefsManager manager;

    EditText etSavings, etUsername;
    Button bOk, bCancel;
    RadioGroup radioGroup;

    LinearLayout llSavings;

    //variables set by user
    float savings;
    String username, grouping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //apply theme
        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_user_details2);

        //init the UI
        initUI();

        //init the Listeners
        setUpUI();

        //init values
        fillUIValues();

    }

    //init the UI Views
    private void initUI() {

        etUsername = (EditText) findViewById(R.id.etUsername);

        //the linear layout for the savings
        llSavings = (LinearLayout) findViewById(R.id.llSavings);

        etSavings = (EditText) findViewById(R.id.etSavings);
        radioGroup = (RadioGroup) findViewById(R.id.rgGrouping);

        bOk = (Button) findViewById(R.id.bOk);
        bCancel = (Button) findViewById(R.id.bCancel);

        //apply button coloring to the buttons
        Themer.setBackgroundColor(this, bOk, false);
        Themer.setBackgroundColor(this, bCancel, true);

    }

    //init form values according to preference file
    private void fillUIValues() {

        manager = new SharedPrefsManager(getApplicationContext());

        etUsername.setText(manager.getPrefsUsername());
        etSavings.setText(manager.getPrefsSavings() + "");

        if (manager.getPrefsGrouping().equalsIgnoreCase(getResources().getString(R.string.pref_grouping_monthly))) {
            radioGroup.check(R.id.rbMonthly);
        } else {
            radioGroup.check(R.id.rbWeekly);
        }
    }

    //init the Listeners
    private void setUpUI() {
        bOk.setOnClickListener(buttonListener);
        bCancel.setOnClickListener(buttonListener);

    }


    //the buttons listener
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bOk:
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
        }
    }

}
