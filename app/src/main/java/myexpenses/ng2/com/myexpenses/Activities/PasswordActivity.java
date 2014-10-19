package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.SharedPrefsManager;

//Activity for the user to enter the password for the app to unlock
public class PasswordActivity extends Activity {

    //number buttons
    private Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bOk;
    //password edit text
    private EditText etPassword;
    //shared prefs manager
    SharedPrefsManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        //init manager
        manager = new SharedPrefsManager(getApplicationContext());

        //init UI
        initUI();

        //init listeners
        initListeners();

    }

    //init UI
    private void initUI(){
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        b5 = (Button) findViewById(R.id.b5);
        b6 = (Button) findViewById(R.id.b6);
        b7 = (Button) findViewById(R.id.b7);
        b8 = (Button) findViewById(R.id.b8);
        b9 = (Button) findViewById(R.id.b9);
        b0 = (Button) findViewById(R.id.b0);
        bOk = (Button) findViewById(R.id.bOk);

        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    //init listeners
    private void initListeners(){
        b1.setOnClickListener(listener);
        b2.setOnClickListener(listener);
        b3.setOnClickListener(listener);
        b4.setOnClickListener(listener);
        b5.setOnClickListener(listener);
        b6.setOnClickListener(listener);
        b7.setOnClickListener(listener);
        b8.setOnClickListener(listener);
        b9.setOnClickListener(listener);
        b0.setOnClickListener(listener);
        bOk.setOnClickListener(listener);
    }

    //buttons listener
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the password text so far
            String text = etPassword.getText().toString();

            //if Ok was clicked
            if(v.getId() == R.id.bOk){
                //check if password is right
                //if(manager.getPrefsPassword().equals(text)){
                  if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_key_password_value" , "").equals(text)){
                    //correct password
                    //launch overview activity
                    Intent intent = new Intent(getApplicationContext() , OverviewActivity.class);
                    startActivity(intent);
                    //and destroy this one
                    finish();
                }else{
                    //wrong password
                    //warn the user
                    Toast.makeText(getApplicationContext() , "Wrong Password" , Toast.LENGTH_SHORT).show();
                    //clear the password edit text
                    etPassword.setText("");
                }
            }else {
                //else a number was clicked

                //append the clicked number to the current password
                text += ((Button) v).getText().toString();
                etPassword.setText(text);
            }

        }
    };

}
