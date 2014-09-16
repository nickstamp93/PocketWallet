package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class UserDetailsActivity extends Activity {

    //SharedPrefsManager object
    SharedPrefsManager manager;

    //View objects for the XML management
    LinearLayout llSalary;
    EditText etSalary, etSavings, etUsername, etBalance;
    CheckBox chbBonus, chbSalary;
    Button bOk, bCancel;
    RadioGroup radioGroup;

    //variables for storing the user inputs
    float savings, salary, balance;
    String username, salFreq, nextPaymentDate;
    boolean onSalary, bonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //init the UI
        initUI();

        //init the Listeners
        initListeners();
    }

    //init the UI Views
    private void initUI() {

        bOk = (Button) findViewById(R.id.bOk);
        bCancel = (Button) findViewById(R.id.bCancel);

        etSalary = (EditText) findViewById(R.id.etSalary);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etSavings = (EditText) findViewById(R.id.etSavings);
        etBalance = (EditText) findViewById(R.id.etBalance);

        chbBonus = (CheckBox) findViewById(R.id.chbBonus);
        chbSalary = (CheckBox) findViewById(R.id.chbSalary);

        radioGroup = (RadioGroup) findViewById(R.id.rgFrequency);

        llSalary = (LinearLayout) findViewById(R.id.llSalary);

    }

    //init the Listeners
    private void initListeners() {
        bOk.setOnClickListener(buttonListener);
        bCancel.setOnClickListener(buttonListener);

        chbSalary.setOnCheckedChangeListener(checkBoxListener);

    }

    //the onSalary checkboxListener
    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            for (int i = 0; i < llSalary.getChildCount(); i++) {
                View view = llSalary.getChildAt(i);
                view.setEnabled(isChecked);
            }
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
                view.setEnabled(isChecked);
            }
            chbBonus.setEnabled(isChecked);
            if (!isChecked) {
                chbBonus.setChecked(false);
            }
        }
    };

    //the buttons listener
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bOk:
                    //if form is not complete, alert the user
                    if (!isFormComplete()) {
                        Toast.makeText(getApplicationContext(), "Please complete the form", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //if form is complete , store these data to the shared prefs file
                    manager = new SharedPrefsManager(getApplicationContext());
                    manager.startEditing();

                    getDataFromXml();

                    manager.setPrefsIsProfile(true);

                    manager.setPrefsUsername(username);
                    manager.setPrefsSavings(savings);
                    manager.setPrefsBalance(balance);
                    manager.setPrefsBonus(bonus);
                    manager.setPrefsOnSalary(onSalary);
                    manager.setPrefsSalary(salary);
                    manager.setPrefsSalFreq(salFreq);
                    manager.setPrefsNpd(nextPaymentDate);
                    manager.setPrefsCurrency("€");

                    manager.commit();

                    //set the activity result to an OK state
                    setResult(RESULT_OK);
                    //and alert the user for profile creation
                    Toast.makeText(getApplicationContext(), "Account Profile created successfully", Toast.LENGTH_LONG).show();
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
        if (etBalance.getText().toString().trim().length() < 1) {
            return false;
        }
        if (chbSalary.isChecked()) {
            if (etSalary.getText().toString().trim().length() < 1) {
                return false;
            }
        }

        return true;
    }

    //get user data from the XML and store them in the variables
    private void getDataFromXml() {
        username = etUsername.getText().toString();
        savings = Float.parseFloat(etSavings.getText().toString());
        onSalary = chbSalary.isChecked();
        bonus = chbBonus.isChecked();
        balance = Float.parseFloat(etBalance.getText().toString());

        if (onSalary) {
            salary = Float.parseFloat(etSalary.getText().toString());
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rbMonthly:
                    salFreq = "monthly";
                    break;
                case R.id.rbWeekly:
                    salFreq = "weekly";
                    break;
                default:
                    salFreq = "monthly";
            }
        } else {
            salary = 0;
            salFreq = "none";
        }
        setNextPaymentDate();

    }

    //set the next payment date in connection with the salFreq and the current date
    private void setNextPaymentDate() {

        //if no salary then return
        if (!chbSalary.isChecked()) {
            return;
        }

        Calendar c = Calendar.getInstance();
        int paymentDay;
        if (salFreq.equals("weekly")) {
            //default weekly payment day is monday
            paymentDay = Calendar.MONDAY;
        } else {
            //and for monthly payment is the 1st of each month
            paymentDay = 1;
        }

        //if salary frequency is 'weekly' add up to 7 days
        if (salFreq.equalsIgnoreCase("weekly")) {

            while (c.get(Calendar.DAY_OF_WEEK) != paymentDay) {
                c.add(Calendar.DATE, 1);
            }
            //else add one month
        } else if (salFreq.equalsIgnoreCase("monthly")) {
            while (c.get(Calendar.DAY_OF_MONTH) != paymentDay) {
                c.add(Calendar.DATE, 1);
            }
        }

        //here we have found the next payment date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        nextPaymentDate = day + "-" + (month + 1) + "-" + year;

        //and now we have to format it to the date format 'dd-MM-yyyy'
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {

            Date currentDate = format.parse(nextPaymentDate);

            nextPaymentDate = format.format(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
    }
}
