package com.ngngteam.pocketwallet.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

        RecurrentUtils.startRecurrentService(context);


    }
}
