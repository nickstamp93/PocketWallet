package com.ngngteam.pocketwallet.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ngngteam.pocketwallet.Activities.RecurrentTransactionsActivity;
import com.ngngteam.pocketwallet.BroadcastReceivers.NotificationBroadcastReceiver;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;
import com.ngngteam.pocketwallet.R;

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
        for (RecurrentTransaction t : db.getTodaysRecurrents()) {
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

        //create the notification
        Notification n = builder
                //set title
                .setContentTitle("Transaction for today ")
                        //set content
                .setContentText(item.getName())
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
