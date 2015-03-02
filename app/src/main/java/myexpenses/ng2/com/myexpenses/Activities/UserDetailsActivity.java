package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    //LinearLayout llSalary;
    EditText  etSavings, etUsername;//, etBalance; //etSalary,
    //CheckBox chbSalary;
    Button bOk, bCancel;
    RadioGroup radioGroup;

    //variables for storing the user inputs
    float savings, balance; // ,salary
    String username , grouping; //, salFreq, nextPaymentDate,salfreqWeekly;
    //int salfreqMonthly;
    int position;
    //boolean onSalary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //apply theme
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getInt("pref_key_theme" , getResources().getColor(R.color.black))==getResources().getColor(R.color.Fuchsia)){
            setTheme(R.style.AppThemeFuchsia);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.black))) {
            setTheme(R.style.AppThemeBlack);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.green))) {
            setTheme(R.style.AppThemeGreen);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.Orange))) {
            setTheme(R.style.AppThemeOrange);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.teal))) {
            setTheme(R.style.AppThemeTeal);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.white))) {
            setTheme(R.style.AppThemeWhite);
        }


        setContentView(R.layout.activity_user_details);

        //init the UI
        initUI();

        //init the Listeners
        initListeners();

        //init values
        initValues();

    }

    //init the UI Views
    private void initUI() {

        bOk = (Button) findViewById(R.id.bOk);
        bCancel = (Button) findViewById(R.id.bCancel);

        //etSalary = (EditText) findViewById(R.id.etSalary);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etSavings = (EditText) findViewById(R.id.etSavings);
//        etBalance = (EditText) findViewById(R.id.etBalance);

        //chbSalary = (CheckBox) findViewById(R.id.chbSalary);

        radioGroup = (RadioGroup) findViewById(R.id.rgGrouping);

       // llSalary = (LinearLayout) findViewById(R.id.llSalary);

    }

    private void initValues(){

        manager = new SharedPrefsManager(getApplicationContext());

//        salfreqWeekly = manager.getPrefsSalFreqWeekly();
//        salfreqMonthly = manager.getPrefsSalFreqMonthly();
        position = 0;

        etUsername.setText(manager.getPrefsUsername());
        etSavings.setText(String.valueOf(manager.getPrefsSavings()));
        //etBalance.setText(String.valueOf(manager.getPrefsBalance()));
        if(manager.getPrefsGrouping().equalsIgnoreCase("monthly")){
            radioGroup.check(R.id.rbMonthly);
        }else{
            radioGroup.check(R.id.rbWeekly);
        }
//        chbSalary.setChecked(manager.getPrefsOnSalary());
//        String salFreq = manager.getPrefsSalFreq();
//        if(salFreq.equalsIgnoreCase("monthly")){
//            radioGroup.check(R.id.rbMonthly);
//        }else{
//            radioGroup.check(R.id.rbWeekly);
//        }
//        etSalary.setText(String.valueOf(manager.getPrefsSalary()));

    }

    //init the Listeners
    private void initListeners() {
        bOk.setOnClickListener(buttonListener);
        bCancel.setOnClickListener(buttonListener);

//        chbSalary.setOnCheckedChangeListener(checkBoxListener);

//        radioGroup.getChildAt(0).setOnClickListener(radiogroupListener);
//        radioGroup.getChildAt(1).setOnClickListener(radiogroupListener);

    }


    /*private View.OnClickListener radiogroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
            switch (v.getId()){
                case R.id.rbWeekly:
                    //call select day of week dialog
                    final CharSequence[] weekItems={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
                    builder.setTitle("Day of Week");
                    builder.setSingleChoiceItems(weekItems, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            position = which;
                            salfreqWeekly = weekItems[which].toString();
                            Toast.makeText(UserDetailsActivity.this, "Day of week:" + salfreqWeekly, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;
                case R.id.rbMonthly:
                    //call select day of month dialog
                    //call select day of week dialog
                    final CharSequence[] monthItems={"1","10","15","20"};
                    builder.setTitle("Day of Week");
                    builder.setSingleChoiceItems(monthItems , 0 , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            salfreqMonthly = Integer.parseInt(monthItems[which].toString());
                            Toast.makeText(UserDetailsActivity.this , "Day of month:" + salfreqMonthly , Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                    break;

            }
        }
    };*/
    //the onSalary checkboxListener
    /*private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                int id = radioGroup.getCheckedRadioButtonId();
                if(id == R.id.rbWeekly){
                    radioGroup.getChildAt(0).performClick();
                }else{
                    radioGroup.getChildAt(1).performClick();
                }

            }
            for (int i = 0; i < llSalary.getChildCount(); i++) {
                View view = llSalary.getChildAt(i);
                view.setEnabled(isChecked);
            }
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
                view.setEnabled(isChecked);
            }
        }
    };*/

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
                    manager.startEditing();

                    getDataFromXml();

                    manager.setPrefsIsProfile(true);

                    manager.setPrefsUsername(username);
                    manager.setPrefsSavings(savings);
//                    manager.setPrefsBalance(balance);
//                    manager.setPrefsOnSalary(onSalary);
//                    manager.setPrefsSalary(salary);
//                    manager.setPrefsSalFreqMonthly(salfreqMonthly);
//                    manager.setPrefsSalFreqWeekly(salfreqWeekly);
//                    manager.setPrefsSalFreq(salFreq);
//                    manager.setPrefsNpd(nextPaymentDate);
                    manager.setPrefsCurrency("€");
                    manager.setPrefsGrouping(grouping);

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
//        if (etBalance.getText().toString().trim().length() < 1) {
//            return false;
//        }
//        if (chbSalary.isChecked()) {
//            if (etSalary.getText().toString().trim().length() < 1) {
//                return false;
//            }
//        }

        return true;
    }

    //get user data from the XML and store them in the variables
    private void getDataFromXml() {
        username = etUsername.getText().toString();
        savings = Float.parseFloat(etSavings.getText().toString());
//        onSalary = chbSalary.isChecked();
        //balance = Float.parseFloat(etBalance.getText().toString());
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.rbWeekly:
                grouping = "weekly";
                break;
            case R.id.rbMonthly:
                grouping = "monthly";
                break;
        }

//        if (onSalary) {
//            salary = Float.parseFloat(etSalary.getText().toString());
//            switch (radioGroup.getCheckedRadioButtonId()) {
//                case R.id.rbMonthly:
//                    salFreq = "monthly";
//                    break;
//                case R.id.rbWeekly:
//                    salFreq = "weekly";
//                    break;
//                default:
//                    salFreq = "monthly";
//            }
//        } else {
//            salary = 0;
//            salFreq = "none";
//        }
//        setNextPaymentDate();

    }

    //set the next payment date in connection with the salFreq and the current date
    /*private void setNextPaymentDate() {

        //if no salary then return
        if (!chbSalary.isChecked()) {
            return;
        }

        Calendar c = Calendar.getInstance();
        int paymentDay;
        if (salFreq.equals("weekly")) {
            //default weekly payment day is monday
            paymentDay = position+1;
        } else {
            //and for monthly payment is the 1st of each month
            paymentDay = salfreqMonthly;
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
    }*/
}
