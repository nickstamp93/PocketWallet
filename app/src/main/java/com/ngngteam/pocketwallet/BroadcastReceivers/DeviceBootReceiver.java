package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ngngteam.pocketwallet.Utils.RecurrentUtils;

/**
 * Created by nickstamp on 7/22/2015.
 * this broadcast receiver is used when the device is rebooted
 * It sets an alarm manager (repeating task) that calls the Recurrent receiver
 * according to the interval
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

/*
        // Construct an intent that will execute the AlarmReceiver
        Intent i = new Intent(context, RecurrentReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, RecurrentReceiver.REQUEST_CODE,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 5 * 1000; // 5 seconds
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);*/

        RecurrentUtils.startRecurrentService(context);

        Log.i("nikos", "alarm set");

    }
}
