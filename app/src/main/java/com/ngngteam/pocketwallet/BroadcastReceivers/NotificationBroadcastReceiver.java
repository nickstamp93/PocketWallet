package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.RecurrentTransaction;

/**
 * Created by nickstamp on 7/27/2015.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getExtras().getInt("id");
        String action = intent.getAction();

        if (action != null) {
            //insert

            Log.i("nikos", "done " + id);
            RecurrentTransaction item = new MoneyDatabase(context).getRecurrent(id);
            item.show();

        } else {
            //cancel , make it pending
            Log.i("nikos", "cancel " + id);
        }

        // if you want cancel notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
//        Log.i("nikos" , "canceling " + id);

    }
}
