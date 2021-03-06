package com.ngngteam.pocketwallet.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.ngngteam.pocketwallet.BroadcastReceivers.ReminderReceiver;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;

import java.util.Calendar;

public class TransparentActivity extends FragmentActivity implements RadialTimePickerDialog.OnTimeSetListener, RadialTimePickerDialog.OnDialogDismissListener {

    //Shared Preferences Manager
    SharedPrefsManager manager;

    //variables needed for the the notification via the alarm manager
    private Intent myIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    //Time dialog
    private RadialTimePickerDialog timeDialog;
    //Calendar variable
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        timeDialog.show(getSupportFragmentManager(), "Time Dialog");

    }

    //init variables
    private void init() {
        manager = new SharedPrefsManager(getApplicationContext());

        calendar = Calendar.getInstance();

        timeDialog = RadialTimePickerDialog.newInstance(TransparentActivity.this, calendar.getTime().getHours(), calendar.getTime().getMinutes(), true);
        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_key_theme", getResources().getColor(R.color.background_material_dark)) == getResources().getColor(R.color.background_material_dark)) {
            timeDialog.setThemeDark(true);
        } else if ((PreferenceManager.getDefaultSharedPreferences(this).getInt("pref_key_theme", getResources().getColor(R.color.background_material_dark)) == getResources().getColor(R.color.background_material_light))) {
            timeDialog.setThemeDark(false);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            finish();
        }
    }

    @Override
    public void onDialogDismiss(DialogInterface dialogInterface) {
        TransparentActivity.this.finish();
    }

    //set the reminder
    public void setAlarm() {
        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if (hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
            calendar.add(Calendar.DATE, 1);
        }

        //set the calendar instance to the right date and time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //create an intent with the notification service
        myIntent = new Intent(getApplicationContext(), ReminderReceiver.class);

        //and a pending intent containing the previous intent
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        //create an alarm manager instance (alarm manager , repeating notifications)
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set next notification at the above date-time , service starts every 24 hours
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);


    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hour, int minutes) {
        //on time set , save the new time in the preferences file
        SharedPrefsManager manager = new SharedPrefsManager(getApplicationContext());
        manager.startEditing();
        manager.setPrefsReminderTime(hour, minutes);
        manager.commit();

        //and set the alarm to this time
        setAlarm();
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text_reminder) + " " + manager.getPrefsReminderTime(), Toast.LENGTH_SHORT).show();

        //destroy current activity
        TransparentActivity.this.finish();
    }
}
