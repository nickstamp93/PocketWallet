package com.ngngteam.pocketwallet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;


//launcher activity of the app
public class LauncherPickerActivity extends Activity {

    //an intent
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //if password protection is enabled
        boolean isPass = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_key_password), false);

        //if enabled , launch the password activity
        if (isPass && !PreferenceManager.getDefaultSharedPreferences(this).
                getString(getResources().getString(R.string.pref_key_pattern), "none").equals("none")) {
            intent = new Intent(getApplicationContext(), PatternLockActivity.class).putExtra("mode", "unlock");
        }//else launch the overview activity
        else {
            intent = new Intent(getApplicationContext(), OverviewActivity2.class);
        }

        //start the correct activity with the chosen intent
        startActivity(intent);

        //destroy this activity
        this.finish();

    }

}
