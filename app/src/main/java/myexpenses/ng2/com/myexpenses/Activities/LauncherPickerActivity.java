package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class LauncherPickerActivity extends Activity {

    SharedPrefsManager manager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        manager = new SharedPrefsManager(getApplicationContext());

        if(manager.getPrefsIsPassword()){
            intent = new Intent(getApplicationContext() , PasswordActivity.class);
        }else{
            intent = new Intent(getApplicationContext() , OverviewActivity.class);
        }

        startActivity(intent);

        this.finish();


    }

}
