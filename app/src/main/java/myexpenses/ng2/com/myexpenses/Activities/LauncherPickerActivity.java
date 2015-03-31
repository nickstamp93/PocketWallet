package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import myexpenses.ng2.com.myexpenses.R;

//launcher activity of the app
public class LauncherPickerActivity extends Activity {

    //an intent
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //if password protection is enabled
        boolean isPass = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_key_password), false);

        //if enabled , launch the password activity
        if (isPass) {
            intent = new Intent(getApplicationContext(), PasswordActivity.class);
        }//else launch the overview activity
        else {
            intent = new Intent(getApplicationContext(), OverviewActivity.class);
        }

        //start the correct activity with the chosen intent
        startActivity(intent);

        //destroy this activity
        this.finish();

    }

}
