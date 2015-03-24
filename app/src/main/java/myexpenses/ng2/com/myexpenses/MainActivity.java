package myexpenses.ng2.com.myexpenses;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Activities.AddExpenseActivity;
import myexpenses.ng2.com.myexpenses.Activities.AddIncomeActivity;
import myexpenses.ng2.com.myexpenses.Activities.CategoriesManagerActivity;
import myexpenses.ng2.com.myexpenses.Activities.CreateCategoryActivity;
import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.Activities.OverviewActivity;
import myexpenses.ng2.com.myexpenses.Activities.SettingsActivity2;
import myexpenses.ng2.com.myexpenses.Activities.UserDetailsActivity;
import myexpenses.ng2.com.myexpenses.ColorPicker.ColorPickerDialog;
import myexpenses.ng2.com.myexpenses.Utils.LetterImageView;

public class MainActivity extends FragmentActivity implements RadialTimePickerDialog.OnTimeSetListener, CalendarDatePickerDialog.OnDateSetListener {


            Button bOverview, bAddIncome, bAddExpense, bSettings, bHistory,
            bUserDetails, bCategories, bSettings2, bColorPicker,
            bTimePciker, bDatePicker,bCreateCategory;



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

                bTimePciker = (Button) findViewById(R.id.bRadialTimePicker);
        bDatePicker = (Button) findViewById(R.id.bDatePicker);



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
        bTimePciker.setOnClickListener(actClickListener);
        bDatePicker.setOnClickListener(actClickListener);





        bCreateCategory.setOnClickListener(actClickListener);
    }

    private View.OnClickListener actClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bOverview:
                    startActivity(new Intent(getApplicationContext(), OverviewActivity.class));
                    break;
                case R.id.bAddExpense:
                    startActivity(new Intent(getApplicationContext(), AddExpenseActivity.class));
                    break;
                case R.id.bAddIncome:
                    startActivity(new Intent(getApplicationContext(), AddIncomeActivity.class));
                    break;
                case R.id.bHistory:
                    startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                    break;
                case R.id.bUserDetails:
                    startActivity(new Intent(getApplicationContext(), UserDetailsActivity.class));
                    break;
                case R.id.bCategories:
                    startActivity(new Intent(getApplicationContext(), CategoriesManagerActivity.class));
                    break;
                case R.id.bSettings2:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity2.class));
                    break;
                case R.id.bColorPicker:


                    int[] mColor = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.BLACK, Color.CYAN, Color.DKGRAY, Color.GRAY
                            , Color.LTGRAY, Color.MAGENTA, Color.YELLOW};
                    ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 4, ColorPickerDialog.SIZE_SMALL);

                    dialog.setSelectedColor(Color.RED);
                    dialog.show(getFragmentManager(), "Color Picker");
                    dialog.showProgressBarView();
                    break;
                case R.id.bRadialTimePicker:
                    Calendar c = Calendar.getInstance();

                    RadialTimePickerDialog timeDialog = RadialTimePickerDialog.newInstance(MainActivity.this, c.getTime().getHours(), c.getTime().getMinutes(), true);

                    timeDialog.show(getSupportFragmentManager(), "Time");
                    break;
                case R.id.bDatePicker:

                    Calendar cal = Calendar.getInstance();

                    CalendarDatePickerDialog dateDialog = CalendarDatePickerDialog.newInstance(MainActivity.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    dateDialog.show(getSupportFragmentManager(), "Date");
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

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
        bTimePciker.setText(String.valueOf(i) + ":" + String.valueOf(i2));
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2, int i3) {
        bDatePicker.setText(i + "-" + i2 + "-" + i3);
    }
}
