package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;

import myexpenses.ng2.com.myexpenses.Activities.OverviewActivity;
import myexpenses.ng2.com.myexpenses.MainActivity;
import myexpenses.ng2.com.myexpenses.R;

public class AlarmService extends Service {

    //Notification manager
    private NotificationManager manager;


    //Shared Preferences Manager
    private SharedPrefsManager prefsManager;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //when service starts , open the preference file
        prefsManager = new SharedPrefsManager(getApplicationContext());
        //if the daily reminder is enabled then go on to the procedure
        //if(prefsManager.getPrefsReminder()) {
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("pref_key_reminder",false)){
            //get the notification service from the system
            manager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            //create an intent to be  used on click notification
            Intent intent1 = new Intent(this.getApplicationContext(), OverviewActivity.class);

            //create the notification and add flags
            Notification notification = new Notification(R.drawable.ic_launcher, "This is a test message", System.currentTimeMillis());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //this ensures that clicking on the notification opens up the overview activity
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.setLatestEventInfo(this.getApplicationContext(), "Alarm Manager Demo", "notification content", pendingNotificationIntent);

            //build the notification and issue it
            manager.notify(0, notification);
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
