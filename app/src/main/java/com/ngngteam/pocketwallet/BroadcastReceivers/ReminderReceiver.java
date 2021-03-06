package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ngngteam.pocketwallet.Activities.LauncherPickerActivity;
import com.ngngteam.pocketwallet.R;

//broadcast receiver that receives the intents broadcasted by the alarm manager and starts our alarm service
public class ReminderReceiver extends BroadcastReceiver {

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //if the daily reminder is enabled then go on to the procedure
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.pref_key_reminder), false)) {

            createNotification(context);

        }

    }

    private void createNotification(Context context) {

        //create an intent to be  used on click notification
        Intent intent1 = new Intent(context, LauncherPickerActivity.class);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //create the notification
        Notification n = builder
                //set title
                .setContentTitle(context.getResources().getString(R.string.app_name))
                        //set content
                .setContentText(context.getResources().getString(R.string.text_reminder_content))
                        //set icon
                .setSmallIcon(R.drawable.ic_launcher)
                        //set intent to launch on content click
                .setContentIntent(pendingNotificationIntent)
                        //cancel notification on click
                .setAutoCancel(true)
                .build();


        //notification manager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        notificationManager.notify(99, n);
    }
}
