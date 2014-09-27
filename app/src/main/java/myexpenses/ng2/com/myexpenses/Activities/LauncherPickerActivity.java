package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

//this activity is the launcher activity for this app
public class LauncherPickerActivity extends Activity {

    //Shared Preferences manager
    SharedPrefsManager manager;
    //an intent
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init the manager
        manager = new SharedPrefsManager(getApplicationContext());

        //if the app is password pretoceted enabled , Password Activity must open before everything
        if(manager.getPrefsIsPassword()){
            intent = new Intent(getApplicationContext() , PasswordActivity.class);
        }else{
            //else launch the default Overview Activity
            intent = new Intent(getApplicationContext() , OverviewActivity.class);
        }

        //start the correct activity with the chosen intent
        startActivity(intent);

        //destroy this activity
        this.finish();


    }

}
