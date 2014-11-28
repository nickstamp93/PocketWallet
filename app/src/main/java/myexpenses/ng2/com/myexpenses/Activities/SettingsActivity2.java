package myexpenses.ng2.com.myexpenses.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import java.util.Calendar;
import java.util.prefs.Preferences;

import myexpenses.ng2.com.myexpenses.BroadcastReceivers.ReminderReceiver;
import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerDialog;
import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerSwatch;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.PasswordDialog;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class SettingsActivity2 extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getInt("pref_key_theme" , getResources().getColor(R.color.black))==getResources().getColor(R.color.Fuchsia)){
            setTheme(R.style.AppThemeFuchsia);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.black))) {
            setTheme(R.style.AppThemeBlack);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.green))) {
            setTheme(R.style.AppThemeGreen);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.Orange))) {
            setTheme(R.style.AppThemeOrange);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.teal))) {
            setTheme(R.style.AppThemeTeal);
        }else if((prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black))==getResources().getColor(R.color.white))) {
            setTheme(R.style.AppThemeWhite);
        }
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);

        initSummary(getPreferenceScreen());

        Preference screen = (Preference) findPreference("pref_key_categories");
        Intent i = new Intent(this , CategoriesManagerActivity.class);
        screen.setIntent(i);

        screen = (Preference) findPreference("pref_key_profile");
        i = new Intent(this , UserDetailsActivity.class);
        screen.setIntent(i);

        screen = (Preference) findPreference("pref_key_reminder_time");
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                startActivity(new Intent(SettingsActivity2.this, TransparentActivity.class));
//                act = new TransparentActivity(SettingsActivity2.this);
//                Calendar c = Calendar.getInstance();
//                RadialTimePickerDialog timeDialog = RadialTimePickerDialog.newInstance(SettingsActivity2.this, c.getTime().getHours(), c.getTime().getMinutes(), true);
//                timeDialog.show(i.getSupportFragmentManager() , "Nikos");

                return false;
            }
        });

        screen = (Preference) findPreference("pref_key_about");
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity2.this);

                builder.setMessage("This is an application for managing your personal expenses and incomes" +
                        "\n\n\nCreated by Stampoulis Nikos and Zissis Nikos." +
                        "\n\nNo external libraries were used in this project" +
                        "\n\nSpecial thanks to A,B,C,D for the icons used in the app")
                        .setTitle("About Us");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                return false;
            }
        });

        screen = (Preference) findPreference("pref_key_delete_income");
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity2.this);

                builder.setMessage("You are about to delete your income history.This cannot be undone.")
                        .setTitle("Caution");
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete income method from database
                        MoneyDatabase db = new MoneyDatabase(getApplicationContext());
                        db.deleteAllIncome();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        screen = (Preference) findPreference("pref_key_delete_expense");
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity2.this);

                builder.setMessage("You are about to delete your expense history.This cannot be undone.")
                        .setTitle("Caution");
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete expense method from database

                        MoneyDatabase db = new MoneyDatabase(getApplicationContext());
                        db.deleteAllExpense();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        screen = (Preference) findPreference("pref_key_theme");
        screen.setDefaultValue(getResources().getColor(R.color.Fuchsia));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity2.this);
                int[] mColor = new int[]{getResources().getColor(R.color.black),getResources().getColor(R.color.white)
                ,getResources().getColor(R.color.Orange) , getResources().getColor(R.color.green) ,
                        getResources().getColor(R.color.teal) , getResources().getColor(R.color.Fuchsia)};
                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 3, ColorPickerDialog.SIZE_SMALL);

                dialog.setSelectedColor(prefs.getInt("pref_key_theme" ,getResources().getColor(R.color.black)));
                dialog.setOnColorSelectedListener(colorSetListener);
                dialog.show(getFragmentManager(), "color");
                return false;
            }
        });


    }



    private ColorPickerSwatch.OnColorSelectedListener colorSetListener = new ColorPickerSwatch.OnColorSelectedListener() {
        @Override
        public void onColorSelected(int color) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity2.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("pref_key_theme", color);
            editor.commit();
            Intent i = getIntent();
            overridePendingTransition(0,0);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0,0);
            startActivity(i);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("pref_key_password")
                && sharedPreferences.getString("pref_key_password_value", "").equals("")
                && sharedPreferences.getBoolean("pref_key_password" , false)){
            ((PasswordDialog)findPreference("pref_key_password_value")).show();
            //PasswordDialog d = new PasswordDialog(SettingsActivity2.this , null);
        }
        if(key.equals("pref_key_reminder") && sharedPreferences.getBoolean("pref_key_reminder",false)){
            SharedPrefsManager manager = new SharedPrefsManager(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Daily Reminder activated\nNext reminder at " + manager.getPrefsReminderTime(), Toast.LENGTH_SHORT).show();

            setAlarm();
        }
        updatePrefSummary(findPreference(key));

    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {

        if (p.getKey().equals("pref_key_currency")) {
            ListPreference pref = (ListPreference) p;
            pref.setSummary(pref.getValue());
        }else if(p.getKey().equals("pref_key_reminder_time")){
            String sum = new SharedPrefsManager(getApplicationContext()).getPrefsReminderTime();
            p.setSummary(sum);
        }
    }

    public void updatePassword(){
        SwitchPreference p =  (SwitchPreference)findPreference("pref_key_password");
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("pref_key_password" , false).commit();
        p.setChecked(false);
    }

    public void setAlarm(){
        PendingIntent pendingIntent;
        Intent myIntent;
        AlarmManager alarmManager;
        SharedPrefsManager manager = new SharedPrefsManager(getApplicationContext());

        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        Log.i("nikos", "instance date:" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if(hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)){
            calendar.add(Calendar.DATE , 1);
            Log.i("nikos" , "date changed: " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                    + "  " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute);
        }

        //set the calendar instance to the right date and time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //create an intent with the notification service
        myIntent = new Intent(getApplicationContext() , ReminderReceiver.class);

        //and a pending intent containing the previous intent
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        //create an alarm manager instance (alarm manager , repeating notifications)
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //set next notification at the above date-time , service starts every 24 hours
        alarmManager.setRepeating(AlarmManager.RTC , calendar.getTimeInMillis(), 24*60*60*1000 , pendingIntent);

        Log.i("nikos", "alarm set for " + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
                + "  " + hour + ":" + minute);
    }

}
