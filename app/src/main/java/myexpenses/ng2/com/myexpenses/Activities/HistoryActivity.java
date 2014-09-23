package myexpenses.ng2.com.myexpenses.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;
import myexpenses.ng2.com.myexpenses.Utils.FiltersDateDialog;
import myexpenses.ng2.com.myexpenses.Utils.FiltersDateToDateDialog;
import myexpenses.ng2.com.myexpenses.Utils.HistoryListViewAdapter;


public class HistoryActivity extends Activity  {

    private HistoryListViewAdapter adapter;
    private Cursor c;
    private MoneyDatabase db;
    private ListView lv;

  // private String filters[] ;
  //  private FilterAdapter Fadapter;
  //  private int firstAppearance=0;
    private AlertDialog dialog=null;
    private boolean switcher =true;
    private Menu menu;



    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return super.startInstrumentation(className, profileFile, arguments);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        lv = (ListView) findViewById(R.id.lvHistory);

        db = new MoneyDatabase(getApplicationContext());
        c = db.getCursorExpense();

        startManagingCursor(c);
        adapter = new HistoryListViewAdapter(getApplicationContext() , c);
        adapter.setTheView(true);

        lv.setAdapter(adapter);

    }

    public void refreshList(Cursor cursor){
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }



/*
  private void setFilters(){

      ActionBar actionBar=getActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

      filters=getResources().getStringArray(R.array.filters);
      ArrayList<String> entries=new ArrayList<String>();

      for (int i=0; i<filters.length; i++){
          entries.add(filters[i]);
      }

      Fadapter=new FilterAdapter(getApplicationContext(),entries);
      SpinnerAdapter spinnerAdapter;


   //   actionBar.setListNavigationCallbacks(Fadapter,this);



  }*/

 public void saveExpenseFiltersDate(String date){
     c=db.getExpensesByDate(date);
     refreshList(c);
 }

 public void saveIncomeFiltersDate(String date){
     c=db.getIncomeByDate(date);
     refreshList(c);
 }


 public void saveFiltersDateToDate(String from,String to){
     c=db.getExpensesByDateToDate(from,to);
     refreshList(c);
 }

 public void saveIncomeFiltersDateToDate(String from,String to){
     c=db.getIncomeByDateToDate(from,to);
     refreshList(c);
 }

    public void dismissDialog(){
        dialog.dismiss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.history_expense, this.menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        switch (id){

            case R.id.action_settings:
                return true;

            case R.id.FiltersStandar:
                c=db.getCursorExpense();
                this.refreshList(c);
                break;


            case R.id.FiltersCategory:
                final String categories[]=getResources().getStringArray(R.array.expenses_category);
                CharSequence[] items=new CharSequence[categories.length];
                for (int i=0; i<categories.length; i++){
                    items[i]=categories[i];
                }
                builder.setTitle("Choose a Category");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {


                        String chosenCategory = categories[item];
                        c = db.getExpensesByCategory(chosenCategory);
                        refreshList(c);
                        dialogInterface.dismiss();
                        dismissDialog();


                    }
                });
                dialog=builder.create();
                dialog.show();
                break;

            case R.id.OrderPrice:
                final CharSequence[] orderItems={"Ascending","Declining"};
                builder.setTitle("Order of items depended on price");
                builder.setSingleChoiceItems(orderItems,-1,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc=false;
                        if(pos==0){
                            asc=true;
                        }
                        c=db.getExpensesByPriceOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                dialog=builder.create();
                dialog.show();
                break;

            case R.id.FiltersDate:
                FiltersDateDialog DDialog=new FiltersDateDialog(true,true);
                DDialog.show(getFragmentManager(),"FiltersDate Dialog");
                break;

            case R.id.FiltersDTD:
                FiltersDateToDateDialog DTDDialog=new FiltersDateToDateDialog(true);
                DTDDialog.show(getFragmentManager(),"FiltersDateToDaTE Dialog");
                break;

            case R.id.FiltersNTO:
                c=db.getExpensesFromNewestToOldest();
                refreshList(c);
                break;

            case R.id.IncomeStandar:
                c=db.getCursorIncomes();
                refreshList(c);
                break;

            case R.id.IncomeSource:
                final EditText source=new EditText(this);
                source.setHint("Source");
                source.setSingleLine(true);

                LinearLayout ll=new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(source);
                builder.setTitle("Write the source of the income");
                builder.setView(ll);
                builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String incomeSource;
                        incomeSource = source.getText().toString();
                        c = db.getIncomesBySource(incomeSource);
                        refreshList(c);
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialog=builder.create();
                dialog.show();
                break;

            case R.id.IncomeDate:
                FiltersDateDialog dDialog=new FiltersDateDialog(true,false);
                dDialog.show(getFragmentManager(),"Date Income Filters Dialog");
                break;

            case R.id.IncomeDTD:
                FiltersDateToDateDialog dtdDialog=new FiltersDateToDateDialog(false);
                dtdDialog.show(getFragmentManager(),"Date to Date Income Filters Dialog");
                break;

            case R.id.IncomeNTO:
                c=db.getIncomeByNewestToOldest();
                refreshList(c);
                break;

            case R.id.toggleButton:

                if(switcher){


                    switcher=false;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_income, menu);
                    c=db.getCursorIncomes();
                    adapter=new HistoryListViewAdapter(getApplicationContext(),c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);

                }else{

                    switcher=true;
                    menu.clear();
                    getMenuInflater().inflate(R.menu.history_expense,menu);
                    c=db.getCursorExpense();
                    adapter=new HistoryListViewAdapter(getApplicationContext(),c);
                    adapter.setTheView(switcher);
                    lv.setAdapter(adapter);

                }



        }


        return super.onOptionsItemSelected(item);
    }
/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem filters= menu.getItem(1);
        SubMenu subMenu=filters.getSubMenu();
        MenuItem category=subMenu.getItem(1);
        MenuItem price=subMenu.getItem(2);
        MenuItem nto=subMenu.getItem(5);
        if(!switcher){
            category.setTitle("Source");
            price.setTitle("Order depended on amount");
            nto.setTitle("Newest Income to oldest");
        }else{
            category.setTitle("Category");
            price.setTitle("Order depended on price");
            nto.setTitle("Newest Expense to oldest");
        }


        return true;

    }*/
    /*
    @Override
    public boolean onNavigationItemSelected(int position, long l) {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        Log.i("Listener","bike");


        switch (position){

            case 0:
                if(firstAppearance!=0){
                    c=db.getCursorExpense();
                    this.refreshList(c);
                }
                break;

            case 1:
                Log.i("Categories","Mpainei");
                firstAppearance=1;

                final String categories[]=getResources().getStringArray(R.array.expenses_category);
                CharSequence[] items=new CharSequence[categories.length];
                for (int i=0; i<categories.length; i++){
                    items[i]=categories[i];
                }
                builder.setTitle("Choose a Category");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {


                            String chosenCategory = categories[item];
                            Log.i("Item =", chosenCategory);
                            c = db.getExpensesByCategory(chosenCategory);
                            refreshList(c);
                            dialogInterface.dismiss();
                            dismissDialog();


                    }
                });
                   dialog=builder.create();
                   dialog.show();
                   Log.i("Category","Still active");
                 break;

            case 2:
                firstAppearance=1;
                final CharSequence[] orderItems={"Ascending","Declining"};
                builder.setTitle("Order of items depended on price");
                builder.setSingleChoiceItems(orderItems,-1,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        boolean asc=false;
                        if(pos==0){
                           asc=true;
                        }
                        c=db.getExpensesByPriceOrder(asc);
                        refreshList(c);
                        dialogInterface.dismiss();

                    }
                });
                AlertDialog dialog1=builder.create();
                dialog1.show();
                break;

            case 3:
                firstAppearance=1;
                FiltersDateDialog DDialog=new FiltersDateDialog(true);
                DDialog.show(getFragmentManager(),"FiltersDate Dialog");
                break;

            case 4:
                firstAppearance=1;
                FiltersDateToDateDialog DTDDialog=new FiltersDateToDateDialog();
                DTDDialog.show(getFragmentManager(),"FiltersDateToDaTE Dialog");
                break;

            case 5:
                firstAppearance=1;
                c=db.getExpensesFromNewestToOldest();
                refreshList(c);
                break;



        }

        return true;
    }
*/

}
