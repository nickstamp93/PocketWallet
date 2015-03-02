package myexpenses.ng2.com.myexpenses.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.BroadcastReceivers.ReminderReceiver;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class TransparentActivity extends FragmentActivity implements RadialTimePickerDialog.OnTimeSetListener, RadialTimePickerDialog.OnDialogDismissListener {

    //Shared Preferences Manager
    SharedPrefsManager manager;

    //variables needed for the the notification via the alarm manager
    private Intent myIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        manager = new SharedPrefsManager(getApplicationContext());

        Calendar c = Calendar.getInstance();

        RadialTimePickerDialog timeDialog = RadialTimePickerDialog.newInstance(TransparentActivity.this, c.getTime().getHours(), c.getTime().getMinutes(), true);
        timeDialog.show(getSupportFragmentManager(), "Nikos");

    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
        //on time set , save the new time in the preferences file
        SharedPrefsManager manager = new SharedPrefsManager(getApplicationContext());
        manager.startEditing();
        manager.setPrefsReminderTime(i, i2);
        manager.commit();
        //and set the alarm to this time
        setAlarm();
        TransparentActivity.this.finish();
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

    public void setAlarm() {
        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        Log.i("nikos", "instance date:" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if (hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
            calendar.add(Calendar.DATE, 1);
            Log.i("nikos", "date changed: " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                    + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute);
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

        Log.i("nikos", "alarm set for " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + hour + ":" + minute);
    }

}
