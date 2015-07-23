package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ngngteam.pocketwallet.Services.RecurrentTransactionsService;

/**
 * Created by nickstamp on 7/22/2015.
 * this broadcast receiver is used to start the RecurrentTransactionService when called
 */
public class RecurrentReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RecurrentTransactionsService.class);
        context.startService(i);

    }


}
