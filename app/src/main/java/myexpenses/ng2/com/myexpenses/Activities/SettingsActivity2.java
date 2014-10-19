package myexpenses.ng2.com.myexpenses.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import java.util.prefs.Preferences;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.PasswordDialog;

public class SettingsActivity2 extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


        initSummary(getPreferenceScreen());

        Preference screen = (Preference) findPreference("pref_key_categories");
        Intent i = new Intent(this , CategoriesManagerActivity.class);
        screen.setIntent(i);

        screen = (Preference) findPreference("pref_key_profile");
        i = new Intent(this , UserDetailsActivity.class);
        screen.setIntent(i);

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


    }

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
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
    }

}
