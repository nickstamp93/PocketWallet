package com.ngngteam.pocketwallet.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.ngngteam.pocketwallet.BroadcastReceivers.RecurrentReceiver;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;

/**
 * Created by nickstamp on 7/22/2015.
 * service that checks if there is any recurrent transaction that must be
 * done today .If so , for every transaction needed , it should create a
 * notification providing the user with two options . Either insert the transaction or cancel it.
 */
public class RecurrentTransactionsService extends IntentService {

    public static boolean isRunning = false;

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


        Log.i("MyTestService", "Service running");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
