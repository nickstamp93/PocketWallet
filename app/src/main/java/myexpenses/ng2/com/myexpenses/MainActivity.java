package myexpenses.ng2.com.myexpenses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import myexpenses.ng2.com.myexpenses.Activities.AddExpenseActivity;
import myexpenses.ng2.com.myexpenses.Activities.AddIncomeActivity;
import myexpenses.ng2.com.myexpenses.Activities.CategoriesManagerActivity;
import myexpenses.ng2.com.myexpenses.Activities.CreateCategoryActivity;
import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.Activities.OverviewActivity;
import myexpenses.ng2.com.myexpenses.Activities.SettingsActivity;
import myexpenses.ng2.com.myexpenses.Activities.SettingsActivity2;
import myexpenses.ng2.com.myexpenses.Activities.UserDetailsActivity;
import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerDialog;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.LetterImageView;

public class MainActivity extends Activity {

    Button bOverview,bAddIncome,bAddExpense , bSettings , bHistory , bUserDetails , bCategories , bSettings2 , bColorPicker,bCreateCategory;

    LetterImageView iv;

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
        bSettings2 = (Button) findViewById(R.id.bSettings2);
        bColorPicker = (Button) findViewById(R.id.bColorPicker);
        bCreateCategory=(Button) findViewById(R.id.bCreateCategory);
        iv = (LetterImageView) findViewById(R.id.liv);
        iv.setOval(true);
        iv.setmBackgroundPaint(Color.RED);
        iv.setLetter('H');

        bOverview.setOnClickListener(actClickListener);
        bAddIncome.setOnClickListener(actClickListener);
        bAddExpense.setOnClickListener(actClickListener);
        bHistory.setOnClickListener(actClickListener);
        bSettings.setOnClickListener(actClickListener);
        bUserDetails.setOnClickListener(actClickListener);
        bCategories.setOnClickListener(actClickListener);
        bSettings2.setOnClickListener(actClickListener);
        bColorPicker.setOnClickListener(actClickListener);
        bCreateCategory.setOnClickListener(actClickListener);
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
                case R.id.bSettings2:
                    startActivity(new Intent(getApplicationContext() , SettingsActivity2.class));
                    break;
                case R.id.bColorPicker:
                    int[] mColor = new int[]{Color.RED , Color.BLUE , Color.GREEN , Color.BLACK , Color.CYAN , Color.DKGRAY , Color.GRAY
                            , Color.LTGRAY , Color.MAGENTA , Color.YELLOW};
                    ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title ,mColor , 0  , 5 , ColorPickerDialog.SIZE_SMALL);
                    dialog.setSelectedColor(Color.RED);
                    dialog.show(getFragmentManager() , "Color Picker");
                    break;
                case R.id.bCreateCategory:
                    startActivity(new Intent(getApplicationContext(), CreateCategoryActivity.class));
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
