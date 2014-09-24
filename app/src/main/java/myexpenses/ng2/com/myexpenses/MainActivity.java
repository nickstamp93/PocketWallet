package myexpenses.ng2.com.myexpenses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import myexpenses.ng2.com.myexpenses.Activities.AddExpenseActivity;
import myexpenses.ng2.com.myexpenses.Activities.AddIncomeActivity;
import myexpenses.ng2.com.myexpenses.Activities.CategoriesManagerActivity;
import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.Activities.OverviewActivity;
import myexpenses.ng2.com.myexpenses.Activities.SettingsActivity;
import myexpenses.ng2.com.myexpenses.Activities.UserDetailsActivity;
import myexpenses.ng2.com.myexpenses.R;

public class MainActivity extends Activity {

    Button bOverview,bAddIncome,bAddExpense , bSettings , bHistory , bUserDetails , bCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    private void initUI() {
        bOverview = (Button) findViewById(R.id.bOverview);
        bAddIncome = (Button) findViewById(R.id.bAddIncome);
        bAddExpense = (Button) findViewById(R.id.bAddExpense);
        bHistory = (Button) findViewById(R.id.bHistory);
        bSettings = (Button) findViewById(R.id.bSettings);
        bUserDetails = (Button) findViewById(R.id.bUserDetails);
        bCategories = (Button) findViewById(R.id.bCategories);

        bOverview.setOnClickListener(actClickListener);
        bAddIncome.setOnClickListener(actClickListener);
        bAddExpense.setOnClickListener(actClickListener);
        bHistory.setOnClickListener(actClickListener);
        bSettings.setOnClickListener(actClickListener);
        bUserDetails.setOnClickListener(actClickListener);
        bCategories.setOnClickListener(actClickListener);
    }

    private View.OnClickListener actClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bOverview:
                    startActivity(new Intent(getApplicationContext() , OverviewActivity.class));
                    break;
                case R.id.bAddExpense:
                    startActivity(new Intent(getApplicationContext() , AddExpenseActivity.class));
                    break;
                case R.id.bAddIncome:
                    startActivity(new Intent(getApplicationContext() , AddIncomeActivity.class));
                    break;
                case R.id.bHistory:
                    startActivity(new Intent(getApplicationContext() , HistoryActivity.class));
                    break;
                case R.id.bSettings:
                    startActivity(new Intent(getApplicationContext() , SettingsActivity.class));
                    break;
                case R.id.bUserDetails:
                    startActivity(new Intent(getApplicationContext() , UserDetailsActivity.class));
                    break;
                case R.id.bCategories:
                    startActivity(new Intent(getApplicationContext() , CategoriesManagerActivity.class));
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
