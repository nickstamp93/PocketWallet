package com.ngngteam.pocketwallet.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.ngngteam.pocketwallet.BroadcastReceivers.ReminderReceiver;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerDialog;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerSwatch;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        //init preference screen from xml file
        addPreferencesFromResource(R.xml.preferences);

        //init the summaries for the screens
        initSummaries(getPreferenceScreen());

        setPreferenceActions();

    }

    //set all the preferences and their actions
    private void setPreferenceActions() {

        if (PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).
                getString(getString(R.string.pref_key_pattern), "1234").equals("1234")) {
            disablePassword();
        }

        //when user clicks on "profile" preference item
        //launch intent with target the UserDetailsActivity class
        Preference screen = findPreference(getResources().getString(R.string.pref_key_profile));
        Intent i = new Intent(this, UserDetailsActivity.class);
        screen.setIntent(i);

        //when user clicks on "reminder time" preference item
        //start TransparentActivity which contains the RadialTimeDialog
        //doing it this way because the RadialTimePickerDialog must have FragmentActivity as a parent
        //and this activity is a PreferenceActivity
        screen = findPreference(getResources().getString(R.string.pref_key_reminder_time));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SettingsActivity.this, TransparentActivity.class));
                return false;
            }
        });

        //when user clicks on "pattern lock" preference item
        //launch intent with target the PatternLockActivity class
        screen = findPreference(getResources().getString(R.string.pref_key_pattern));
        i = new Intent(this, PatternLockActivity.class).putExtra("mode", "edit");
        screen.setIntent(i);

        //when user clicks on "about" preference item
        //launch an alert dialog with the aboout text
        screen = findPreference(getResources().getString(R.string.pref_key_about));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(getResources().getString(R.string.text_about))
                        .setTitle(getResources().getString(R.string.dialog_title_about));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
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

        //when user clicks on "version" preference item
        //launch intent with target the VersionActivity class
        screen = findPreference(getResources().getString(R.string.pref_key_version));
        i = new Intent(this, VersionActivity.class);
        screen.setIntent(i);

        //when user clicks on "rate app" preference item
        //launch the market with the app's page
        screen = findPreference(getResources().getString(R.string.pref_key_rate_app));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

                return false;
            }
        });

        //when user clicks on "delete income" preference item
        //ask for confirmation and delete the income data records
        screen = findPreference(getResources().getString(R.string.pref_key_delete_income));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setMessage(getResources().getString(R.string.dialog_text_delete_income_confirm));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete income method from database
                        MoneyDatabase db = new MoneyDatabase(getApplicationContext());
                        db.deleteAllIncome();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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


        //when user clicks on "delete expense" preference item
        //ask for confirmation and delete the expense data records
        screen = findPreference(getResources().getString(R.string.pref_key_delete_expense));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setMessage(getResources().getString(R.string.dialog_text_delete_expense_confirm))
                        .setTitle(getResources().getString(R.string.dialog_title_caution));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete expense method from database

                        MoneyDatabase db = new MoneyDatabase(SettingsActivity.this);
                        db.deleteAllExpense();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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

        //when the user clicks on the "theme" preference item
        screen = findPreference(getResources().getString(R.string.pref_key_theme));
        screen.setDefaultValue(getResources().getColor(R.color.background_material_dark));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                int[] mColor = new int[]{
                        getResources().getColor(R.color.background_material_dark),
                        getResources().getColor(R.color.background_material_light)
                };
                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 2, ColorPickerDialog.SIZE_LARGE);
                dialog.setSelectedColor(prefs.getInt(getResources().getString(R.string.pref_key_theme), getResources().getColor(R.color.background_material_light)));
                dialog.setOnColorSelectedListener(colorSetListener);
                dialog.show(getFragmentManager(), "color");
                return false;
            }
        });
    }


    private ColorPickerSwatch.OnColorSelectedListener colorSetListener = new ColorPickerSwatch.OnColorSelectedListener() {
        @Override
        public void onColorSelected(int color) {
            //when color is selected
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = prefs.edit();

            //save the new theme color
            editor.putInt(getResources().getString(R.string.pref_key_theme), color);
            editor.commit();

            //set the theme changed variable to true
            SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
            manager.startEditing();
            manager.setPrefsThemeChanged(true);
            manager.commit();

            //and then destroy the dialog
            Intent i = getIntent();
            overridePendingTransition(0, 0);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //on resume , update the summaries , and  any preferences launched
        initSummaries(getPreferenceScreen());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(getResources().getString(R.string.pref_key_password))) {

            //if the pass just been enabled
            if (PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean(getResources().getString(R.string.pref_key_password), false)) {


                //launch pattern activity to enter new pattern
                Intent i = new Intent(SettingsActivity.this, PatternLockActivity.class);
                i.putExtra("mode", "edit");
                startActivity(i);
            } else {

                sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().
                        putString(getString(R.string.pref_key_pattern), "none").commit();
                //alert the user
                Toast.makeText(this, getResources().getString(R.string.toast_text_password_off), Toast.LENGTH_SHORT).show();
            }

        }

        if (key.equals(getResources().getString(R.string.pref_key_reminder)) && sharedPreferences.getBoolean(getResources().getString(R.string.pref_key_reminder), false)) {

            //alert the user
            SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text_reminder) + " " + manager.getPrefsReminderTime(), Toast.LENGTH_SHORT).show();

            setAlarm();
        }
        updatePrefSummary(findPreference(key));

    }

    //init summaries of the preference screen
    private void initSummaries(Preference p) {

        //if preference is a PreferenceGroup , init summary for each child in it
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummaries(pGrp.getPreference(i));
            }
        } else {
            //else just update this item's summary
            updatePrefSummary(p);
        }

        //if pattern lock disabled
        if (!PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
                .getBoolean(getResources().getString(R.string.pref_key_password), false)) {
            SwitchPreference pattern = (SwitchPreference) findPreference(getResources().getString(R.string.pref_key_password));
            pattern.setChecked(false);
        }
    }

    //update summary accordingly to preference key
    private void updatePrefSummary(Preference p) {

        if (p.getKey().equals(getResources().getString(R.string.pref_key_currency))) {
            ListPreference pref = (ListPreference) p;
            pref.setSummary(pref.getValue());
        } else if (p.getKey().equals(getResources().getString(R.string.pref_key_reminder_time))) {
            String sum = new SharedPrefsManager(SettingsActivity.this).getPrefsReminderTime();
            p.setSummary(sum);
        }
    }

    //sets the password off
    public void disablePassword() {
        SwitchPreference p = (SwitchPreference) findPreference(getResources().getString(R.string.pref_key_password));
        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean(getResources().getString(R.string.pref_key_password), false).commit();
        p.setChecked(false);
    }

    //set the alarm for the daily reminder
    public void setAlarm() {

        SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);

        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if (hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
            calendar.add(Calendar.DATE, 1);
        }

        //set the calendar instance to the saved date and time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //create an intent with the notification service
        Intent myIntent = new Intent(getApplicationContext(), ReminderReceiver.class);

        //and a pending intent containing the previous intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        //create an alarm manager instance (alarm manager , repeating notifications)
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set next notification at the above date-time , service starts every 24 hours
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);

    }

}
