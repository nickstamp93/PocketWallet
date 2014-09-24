package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.PasswordDialog;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

public class PasswordSettingsActivity extends Activity {

    CheckBox chbPassword;
    SharedPrefsManager manager;
    LinearLayout llChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_settings);

        manager = new SharedPrefsManager(getApplicationContext());

        chbPassword = (CheckBox) findViewById(R.id.chbPassword);

        llChangePass = (LinearLayout) findViewById(R.id.llChangePass);

        chbPassword.setChecked(manager.getPrefsIsPassword());
        llChangePass.setOnClickListener(clickListener);

        chbPassword.setOnCheckedChangeListener(listener);

    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            manager.startEditing();
            manager.setPrefsIsPassword(isChecked);
            manager.commit();
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new PasswordDialog().show(getFragmentManager() , "Password Dialog");
        }
    };

}
