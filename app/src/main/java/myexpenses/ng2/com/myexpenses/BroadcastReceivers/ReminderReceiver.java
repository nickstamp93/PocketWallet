package myexpenses.ng2.com.myexpenses.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import myexpenses.ng2.com.myexpenses.Utils.AlarmService;

//broadcast receiver that receives the intents broadcasted by the alarm manager and starts our alarm service
public class ReminderReceiver extends BroadcastReceiver {
    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //on receive , create an intent with our service and start it
        Intent service = new Intent(context , AlarmService.class);
        context.startService(service);


    }
}
