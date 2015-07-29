package com.ngngteam.pocketwallet.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ngngteam.pocketwallet.BroadcastReceivers.RecurrentReceiver;

/**
 * Created by nickstamp on 7/22/2015.
 */
public class RecurrentUtils {

    public static void startRecurrentService(Context context) {


        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, RecurrentReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, RecurrentReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 30 * 1000; // 5 seconds
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);

    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, RecurrentReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, RecurrentReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

}
