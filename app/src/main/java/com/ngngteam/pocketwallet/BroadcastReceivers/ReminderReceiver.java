package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

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

            //get the notification service from the system
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            //create an intent to be  used on click notification
            Intent intent1 = new Intent(context, LauncherPickerActivity.class);

            //create the notification and add flags
            Notification notification = new Notification(R.drawable.ic_launcher, context.getResources().getString(R.string.text_reminder_message), System.currentTimeMillis());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //this ensures that clicking on the notification opens up the overview activity
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            //add the notification details
            notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.text_reminder_content), pendingNotificationIntent);

            //build the notification and issue it
            manager.notify(0, notification);
        }

    }
}
