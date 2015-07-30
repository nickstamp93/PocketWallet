package com.ngngteam.pocketwallet.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.ngngteam.pocketwallet.Activities.RecurrentTransactionsActivity;
import com.ngngteam.pocketwallet.BroadcastReceivers.NotificationBroadcastReceiver;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;
import com.ngngteam.pocketwallet.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;

/**
 * Created by nickstamp on 7/22/2015.
 * service that checks if there is any recurrent transaction that must be
 * done today .If so , for every transaction needed , it should create a
 * notification providing the user with two options . Either insert the transaction or cancel it.
 */
public class RecurrentTransactionsService extends IntentService {

    public static boolean isRunning = false;

    private NotificationManager notificationManager;

    public RecurrentTransactionsService() {
        super("MyTestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here

        MoneyDatabase db = new MoneyDatabase(this);
        Log.i("nikos", "Today:" + db.getRecurrentsNotification().size() + " transactions");
        for (RecurrentTransaction t : db.getRecurrentsNotification()) {
            createNotification(t);
        }

    }


    private void createNotification(RecurrentTransaction item) {

        //on notification click  , launch recurrent transactions
        Intent intent = new Intent(this, RecurrentTransactionsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, item.getId(), intent, 0);

        //notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //done action
        Intent doneIntent = new Intent(this, NotificationBroadcastReceiver.class);
        doneIntent.putExtra("id", item.getId());
        doneIntent.setAction("action_done");
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(this, item.getId(), doneIntent, 0);

        //cancel action
        Intent cancelIntent = new Intent(this, NotificationBroadcastReceiver.class);
        cancelIntent.putExtra("id", item.getId());
        intent.setAction("action_cancel");
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, item.getId(), cancelIntent, 0);

        String title = "";
        try {
            Date nextDate = new SimpleDateFormat("yyyy-MM-dd").parse(item.getNextDate());
            Calendar cToday = Calendar.getInstance();
            Calendar cDate = Calendar.getInstance();
            cDate.setTime(nextDate);
            if (cToday.get(Calendar.YEAR) == cDate.get(Calendar.YEAR)
                    && cToday.get(Calendar.DAY_OF_YEAR) == cDate.get(Calendar.DAY_OF_YEAR)) {
                //is today
                Log.i("nikos", "it is today");

                title = item.getName() + " is for today";
            } else {
                Log.i("nikos", "it is pending");
                title = item.getName() + " was for " + item.getNextDate();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //create the notification
        Notification n = builder
                //set title
                .setContentTitle(title)
                        //set content
                .setContentText(item.getAmount()
                        + PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_currency), "€"))
                        //set icon
                .setSmallIcon(R.drawable.ic_launcher)
                        //set intent to launch on content click
                .setContentIntent(pIntent)
                        //cancel notification on click
                .setAutoCancel(true)
                        //add the actions
                .addAction(R.drawable.ic_action_add, "Done", donePendingIntent)
                .addAction(R.drawable.ic_clear_search_holo_light, "Not Done", cancelPendingIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.i("nikos", item.getId() + "");
        notificationManager.notify(item.getId(), n);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
