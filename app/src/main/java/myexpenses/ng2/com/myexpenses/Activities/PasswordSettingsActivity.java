package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.PasswordDialog;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

//Activity for managing the password settings
public class PasswordSettingsActivity extends Activity {

    //password check box
    CheckBox chbPassword;
    //shared prefs manager
    SharedPrefsManager manager;
    //change pass ll
    LinearLayout llChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_settings);

        //init manager
        manager = new SharedPrefsManager(getApplicationContext());

        //init UI
        initUI();

        //init Listeners
        initListeners();



    }

    //init UI
    private void initUI(){
        chbPassword = (CheckBox) findViewById(R.id.chbPassword);

        llChangePass = (LinearLayout) findViewById(R.id.llChangePass);

    }

    //init Listeners
    private void initListeners(){
        chbPassword.setChecked(manager.getPrefsIsPassword());
        llChangePass.setOnClickListener(clickListener);

        chbPassword.setOnCheckedChangeListener(checkListener);
    }

    //check box listener
    private CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            manager.startEditing();
            manager.setPrefsIsPassword(isChecked);
            manager.commit();
        }
    };

    //click listener
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new PasswordDialog().show(getFragmentManager() , "Password Dialog");
        }
    };

}
