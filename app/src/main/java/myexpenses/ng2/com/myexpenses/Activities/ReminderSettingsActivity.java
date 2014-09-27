package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.BroadcastReceivers.ReminderReceiver;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;
import myexpenses.ng2.com.myexpenses.Utils.TimeDialog;

public class ReminderSettingsActivity extends Activity {

    //Shared Preferences Manager
    SharedPrefsManager manager;

    //UI elements
    LinearLayout llTime ;
    //check box reminder
    CheckBox chbReminder ;
    //text view for reminder
    TextView tvReminderTime;

    //variables needed for the the notification via the alarm manager
    private Intent myIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings);

        //init manager
        manager = new SharedPrefsManager(getApplicationContext());

        //initialize UI
        initUI();

        //initialize UI Listeners
        initListeners();


    }

    //init UI
    private void initUI() {
        llTime = (LinearLayout) findViewById(R.id.llTime);

        tvReminderTime = (TextView) findViewById(R.id.tvReminderTime);
        tvReminderTime.setText(manager.getPrefsReminderTime());

        chbReminder = (CheckBox) findViewById(R.id.chbReminder);


        //init according to user prefs
        chbReminder.setChecked(manager.getPrefsReminder());

        if(!manager.getPrefsReminder()){
            llTime.setEnabled(false);
            tvReminderTime.setEnabled(false);
        }

    }

    //init listeners
    private void initListeners() {
        llTime.setOnClickListener(listener);

        chbReminder.setOnCheckedChangeListener(chbListener);


    }

    //check box listener
    private CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.chbReminder:
                    //save user preference about reminder(on/off)
                    manager.startEditing();
                    manager.setPrefsReminder(isChecked);
                    manager.commit();

                    //enable/disable other settings accordingly to reminder activation
                    llTime.setEnabled(isChecked);
                    tvReminderTime.setEnabled(isChecked);

                    //notify the user about reminder status
                    if(isChecked){
                        Toast.makeText(getApplicationContext() , "Daily Reminder activated\nNext reminder at " + manager.getPrefsReminderTime() , Toast.LENGTH_SHORT).show();
                        //enable the notification
                        setAlarm();

                    }else{
                        Toast.makeText(getApplicationContext() , "Daily Reminder deactivated" , Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    //click listener
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.llTime:
                    //open dialog to pick time for the reminder
                    new TimeDialog().show(getFragmentManager() , "Reminder Time Dialog");
                    break;
            }
        }
    };

    //function to set the next notification
    //according to preferred time
    public void setAlarm(){
        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        Log.i("nikos", "instance date:" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if(hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)){
            calendar.add(Calendar.DATE , 1);
            Log.i("nikos" , "date changed: " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                    + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute);
        }

        //set the calendar instance to the right date and time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //create an intent with the notification service
        myIntent = new Intent(ReminderSettingsActivity.this , ReminderReceiver.class);

        //and a pending intent containing the previous intent
        pendingIntent = PendingIntent.getBroadcast(ReminderSettingsActivity.this, 0, myIntent, 0);

        //create an alarm manager instance (alarm manager , repeating notifications)
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //set next notification at the above date-time , service starts every 24 hours
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis(), 24*60*60*1000 , pendingIntent);

        Log.i("nikos" , "alarm set for " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + hour + ":" + minute);
    }

}
