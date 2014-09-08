package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.CurrencyDialog;
import myexpenses.ng2.com.myexpenses.Utils.DateFormatDialog;

public class SettingsActivity extends Activity {

    TextView tvDateFormat , tvCurrency , tvCategories , tvRateApp , tvAbout;
    LinearLayout llDateFormat , llCurrency , llCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();

        initListeners();

//        Typeface typeface = Typeface.createFromAsset(getAssets() , "fonts/font_exo2.otf");
//
//        tvDateFormat.setTypeface(typeface);
//        tvCurrency.setTypeface(typeface);
//        tvCategories.setTypeface(typeface);

    }

    private void initListeners() {
        llDateFormat.setOnClickListener(clickListener);
        llCurrency.setOnClickListener(clickListener);
        llCategories.setOnClickListener(clickListener);

        tvAbout.setOnClickListener(clickListener);

    }

    private void initUI(){
        tvDateFormat = (TextView) findViewById(R.id.tvDateFormat);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvRateApp = (TextView) findViewById(R.id.tvRateApp);
        tvAbout = (TextView) findViewById(R.id.tvAbout);

        llDateFormat = (LinearLayout) findViewById(R.id.llDateFormat);
        llCurrency = (LinearLayout) findViewById(R.id.llCurrency);
        llCategories = (LinearLayout) findViewById(R.id.llCategories);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.llDateFormat:
                    new DateFormatDialog().show(getFragmentManager(),"DateDialog");
                    break;
                case R.id.llCurrency:
                    new CurrencyDialog().show(getFragmentManager() , "Dialog");
                    break;
                case R.id.llCategories:
                    break;
                case R.id.tvAbout:
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                                        builder.setMessage("This is an application for managing your personal expenses and incomes" +
                                                "\n\n\nCreated by Stampoulis Nikos and Zissis Nikos." +
                                                "\n\nNo external libraries were used in this project" +
                                                "\n\nSpecial thanks to A,B,C,D for the icons used in the app")
                                                .setTitle("About Us");
                    builder.setPositiveButton("Ok" , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
