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

        //get the id of the notification
        int id = intent.getExtras().getInt("id");
        //get the action - which button was pushed
        String action = intent.getAction();

        //get the notification's recurrent item
        MoneyDatabase db = new MoneyDatabase(context);
        RecurrentTransaction item = db.getRecurrent(id);

        if (action != null) {
            //insert into db , one regular transaction
            //and update the next date for the recurrent
            Log.i("nikos", "done " + id);
            item.addOneTransaction(context);
            db.updateRecurrent(item);

        } else {
            //cancel
            //do nothing , the nextDate remains the same , it will be
            //displayed until it is done
            Log.i("nikos", "cancel " + id);
        }
        db.close();

        //Dimsiss the notification after the user clicked on either button
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

    }
}
