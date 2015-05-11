package com.ngngteam.pocketwallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.ngngteam.pocketwallet.Adapters.HistoryListViewAdapter;
import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Model.ExpenseItem;
import com.ngngteam.pocketwallet.Model.IncomeItem;
import com.ngngteam.pocketwallet.R;

import java.util.Calendar;


public class HistoryActivity extends AppCompatActivity {

    private HistoryListViewAdapter adapter;
    private Cursor c;
    private MoneyDatabase db;
    private ListView lv;
    private AlertDialog dialog = null;
    private boolean switcher = true, update = false;
    private Menu menu;
    private CalendarDatePickerDialog Cdialog;

    private static int result = 1;
    private Calendar calendar;


    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return super.startInstrumentation(className, profileFile, arguments);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Themer.setThemeToActivity(this);

        setContentView(R.layout.activity_history);

        init();

        initUI();

        setUpUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setUpUI() {
        adapter.setTheView(true);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                c.moveToPosition(position);
                update = true;
                stopManagingCursor(c);

                if (switcher) {
                    Intent processExpense = new Intent(HistoryActivity.this, AddExpenseActivity.class);
                    ExpenseItem expense = new ExpenseItem(c.getString(1), c.getString(4), Double.parseDouble(c.getString(3)), c.getString(2));
                    expense.setId(Integer.parseInt(c.getString(0)));
                    processExpense.putExtra("Expense", expense);
                    startActivity(processExpense);
                } else {
                    Intent processIncome = new Intent(HistoryActivity.this, AddIncomeActivity.class);
                    IncomeItem income = new IncomeItem(Double.parseDouble(c.getString(1)), c.getString(3), c.getString(2));
                    income.setId(Integer.parseInt(c.getString(0)));
                    processIncome.putExtra("Income", income);
                    startActivity(processIncome);
                }
            }
        });
    }

    private void initUI() {
        lv = (ListView) findViewById(R.id.lvHistory);
    }

    private void init() {
        db = new MoneyDatabase(getApplicationContext());
        c = db.getExpensesFromNewestToOldest();

        startManagingCursor(c);
        adapter = new HistoryListViewAdapter(getApplicationContext(), c);
    }

    // Refresh the view of HistoryActivity using different cursor
    public void refreshList(Cursor cursor) {
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    //This method is called by DatePickerDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
    //the expenses in specific date(parameter)
    public void saveExpenseFiltersDate(String date) {
        c = db.getExpensesByDate(date);
        refreshList(c);
    }


    //This method is called by DatePickerDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
    //the incomes in specific date(parameter)
    public void saveIncomeFiltersDate(String date) {
        c = db.getIncomeByDate(date);
        refreshList(c);
    }

    //This method is called by FiltersDateToDateDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
    //the expenses with date between parameters from and to
    public void saveFiltersDateToDate(String from, String to) {
        c = db.getExpensesByDateToDate(from, to);
        refreshList(c);
    }

    //This method is called by FiltersDateToDateDialog when the dialog is about to close and set the cursor of HistoryActivity to be all
    //the incomes with date between parameters from and to
    public void saveIncomeFiltersDateToDate(String from, String to) {
        c = db.getIncomeByDateToDate(from, to);
        refreshList(c);
    }

    //Dismiss the field Dialog dialog
    public void dismissDialog() {
        dialog.dismiss();

    }

    //We keep in a variable the actionBar Menu so we can process it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.history_expense, this.menu);

        return true;
    }


    //All the menu items with their listeners. More specific the drop down menu for filtering expenses and incomes and also
    //the toggle button to change view in HistoryActivity between the expenses and incomes.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int counter = 0;

        switch (id) {
            case R.id.FiltersCategory:

                final String categories[];
                Cursor cats = new CategoryDatabase(this).getAllCategories(switcher);
                categories = new String[cats.getCount()];
                for (cats.moveToFirst(); !cats.isAfterLast(); cats.moveToNext()) {
                    categories[counter++] = cats.getString(1);
                }

                builder.setTitle(getResources().getString(R.string.dialog_title_categories));

                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        String chosenCategory = categories[item];
                        c = db.getExpensesByCategory(chosenCategory);
                        refreshList(c);
                        dialogInterface.dismiss();
                        dismissDialog();

                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.OrderPrice:

                final CharSequence[] orderItems = {getResources().getString(R.string.text_ascending), getResources().getString(R.string.text_declining)};
                builder.setTitle(getResources().getString(R.string.dialog_title_price));
                builder.setSingleChoiceItems(orderItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc = false;
                        if (pos == 0) {
                            asc = true;
                        }
                        c = db.getExpensesByPriceOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.FiltersDate:
                calendar = Calendar.getInstance();
                Cdialog = CalendarDatePickerDialog.newInstance(dListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(), "Date Filter");
                break;

            case R.id.FiltersDTD:
                stopManagingCursor(c);
                Intent ExpenseFilterDTD = new Intent(HistoryActivity.this, FiltersDateToDateActivity.class);
                startActivityForResult(ExpenseFilterDTD, result);
                break;

            case R.id.FiltersNTO:
                c = db.getExpensesFromNewestToOldest();
                refreshList(c);
                break;

            case R.id.IncomeSource:

                final String sources[];
                counter = 0;
                Cursor cSource = new CategoryDatabase(this).getAllCategories(switcher);
                sources = new String[cSource.getCount()];
                for (cSource.moveToFirst(); !cSource.isAfterLast(); cSource.moveToNext()) {
                    sources[counter++] = cSource.getString(1);
                }

                builder.setTitle(getResources().getString(R.string.dialog_title_categories));

                builder.setItems(sources, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String chosenCategory = sources[i];
                        c = db.getIncomesBySource(chosenCategory);
                        refreshList(c);
                        dialogInterface.dismiss();
                    }
                });

                dialog = builder.create();
                dialog.show();
                break;
            case R.id.IncomeDate:
                calendar = Calendar.getInstance();
                Cdialog = CalendarDatePickerDialog.newInstance(dListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Cdialog.show(getSupportFragmentManager(), "Date Filter");

                break;

            case R.id.IncomeDTD:
                stopManagingCursor(c);
                Intent IncomeFilterDTD = new Intent(HistoryActivity.this, FiltersDateToDateActivity.class);
                startActivityForResult(IncomeFilterDTD, result);
                break;

            case R.id.IncomeNTO:
                c = db.getIncomeByNewestToOldest();
                refreshList(c);
                break;

            case R.id.IncomeAmount:
                final CharSequence[] Items = {getResources().getString(R.string.text_ascending), getResources().getString(R.string.text_declining)};
                builder.setTitle(getResources().getString(R.string.dialog_title_amount));
                builder.setSingleChoiceItems(Items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc = false;
                        if (pos == 0) {
                            asc = true;
                        }
                        c = db.getIncomeByAmountOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                dialog = builder.create();
                dialog.show();

                break;

            //When the toggleButton is pressed we check the boolean variable switcher.If it is true it means that we are in
            //Expenses so we change the variable switcher we clear the menu and add the Income Menu and the standar cursor
            //for the income items. To do that we need to create new adapter and set the ListView to have this adapter. The
            //same thing happens when we are in Incomes. Finally we use 2 menu xml history_expense - history_income to change
            //the menus between Expenses and Incomes, in both menu there is a item with the same id (toggleButton)
            case R.id.toggleButton:

                switcher = !switcher;
                if (!switcher) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_income, menu);
                    c = db.getIncomeByNewestToOldest();
                    adapter.closeCDB();
                    adapter = new HistoryListViewAdapter(getApplicationContext(), c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);
                } else {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_expense, menu);
                    c = db.getExpensesFromNewestToOldest();
                    adapter.closeCDB();
                    adapter = new HistoryListViewAdapter(getApplicationContext(), c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private CalendarDatePickerDialog.OnDateSetListener dListener = new CalendarDatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int cYear, int cMonth, int cDay) {
            String month, day, date;
            cMonth++;
            if (cMonth < 10) {
                month = "0" + cMonth;
            } else {
                month = String.valueOf(cMonth);
            }
            if (cDay < 10) {
                day = "0" + cDay;
            } else {
                day = String.valueOf(cDay);
            }
            date = day + "-" + month + "-" + cYear;
            if (switcher) {
                saveExpenseFiltersDate(date);

            } else {
                saveIncomeFiltersDate(date);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == result) {
            if (resultCode == Activity.RESULT_OK) {

                String from = data.getStringExtra("From");
                String to = data.getStringExtra("To");

                if (switcher) {
                    saveFiltersDateToDate(from, to);
                } else {
                    saveIncomeFiltersDateToDate(from, to);
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (update) {
            update = false;
            c.requery();
        }
    }

}
