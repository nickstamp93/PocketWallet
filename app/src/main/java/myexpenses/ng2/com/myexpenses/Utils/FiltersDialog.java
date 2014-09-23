package myexpenses.ng2.com.myexpenses.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.security.Policy;
import java.sql.SQLException;
import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 14/9/2014.
 */
public class FiltersDialog extends DialogFragment implements FiltersCategoryDialog.Category,FiltersPriceOrderDialog.PriceOrder,FiltersDateDialog.Date {

    private ListView lv;
    private Dialog dialog;

    private String filters[] ;
    private FilterAdapter adapter;

    private MoneyDatabase db;
    private Cursor cursor;
    private String category,priceOrder,date,from,to;
    private int chosenCategory;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_dialog);

        initUI();
        initListeners();
        initBacisVariables();

        return dialog;

    }

    private void initUI(){
        lv=(ListView) dialog.findViewById(R.id.lvfilters);

        filters=getResources().getStringArray(R.array.filters);
        ArrayList<String> entries=new ArrayList<String>();

        for (int i=0; i<filters.length; i++){
            entries.add(filters[i]);
        }

        adapter=new FilterAdapter(getActivity(),entries);
        lv.setAdapter(adapter);



    }

    private void initBacisVariables(){
        db=new MoneyDatabase(getActivity());

        try{
            db.openDatabase();
        }catch (SQLException e){
            e.printStackTrace();
        }

        chosenCategory=0;
        category=null;
        priceOrder=null;
    }


    private void initListeners(){

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position){

                    case 0:
                            chosenCategory=1;
                            onResume();
                            break;

                    case 1:
                        onPause();
                        chosenCategory=2;
                        FiltersCategoryDialog Cdialog=new FiltersCategoryDialog();
                        Cdialog.setTargetFragment(FiltersDialog.this,0);
                        Cdialog.show(getFragmentManager(),"FiltersCategory dialog");
                        break;

                    case 2:
                        onPause();
                        chosenCategory=3;
                        FiltersPriceOrderDialog POdialog=new FiltersPriceOrderDialog();
                        POdialog.setTargetFragment(FiltersDialog.this,0);
                        POdialog.show(getFragmentManager(),"FiltersPriceOrder dialog");
                        break;

                    case 3:
                        onPause();
                        chosenCategory=4;
                       // FiltersDateDialog Ddialog=new FiltersDateDialog();
                       // Ddialog.setTargetFragment(FiltersDialog.this,0);
                       // Ddialog.show(getFragmentManager(),"FiltersDate Dialog");
                        break;

                    case 4:
                        onPause();
                        chosenCategory=5;
                      //  FiltersDateToDateDialog DTDdialog=new FiltersDateToDateDialog();
                       // DTDdialog.setTargetFragment(FiltersDialog.this,0);
                      //  DTDdialog.show(getFragmentManager(),"FiltersDatetoDate Dialog");
                        break;

                    case 5:
                        chosenCategory=6;
                        onResume();
                        break;


                }



            }
        });

    }

    @Override
    public void getTheCategoryFromDialogFragment(String category) {
        this.category=category;
    }
    @Override
    public void getPriceOrderFromDialogFragment(String priceOrder) {
       this.priceOrder=priceOrder;
    }
    @Override
    public void getDateFromDialogFragment(String date) {
        this.date=date;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        HistoryActivity activity=(HistoryActivity) getActivity();
        activity.refreshList(cursor);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        switch (chosenCategory){

            case 1:
                cursor=db.getCursorExpense();
                dialog.dismiss();
                break;

            case 2:

                cursor=db.getExpensesByCategory(category);
                dialog.dismiss();
                break;

            case 3:

                boolean asc=false;
                if(priceOrder.equals("Ascending")){
                    asc=true;
                }
                cursor=db.getExpensesByPriceOrder(asc);
                dialog.dismiss();
                break;

            case 4:
                cursor=db.getExpensesByDate(date);

                if(cursor.getCount()<=0){
                    Toast.makeText(getActivity(),"Sorry you didn't have an expense tha date so we load all the expenses again",Toast.LENGTH_LONG).show();
                    cursor=db.getCursorExpense();
                }
                dialog.dismiss();
                break;

            case 5:

                cursor=db.getExpensesByDateToDate(from,to);
                if(cursor.getCount()<=0){
                    Toast.makeText(getActivity(),"Sorry you didn't have any expenses between those dates so we load all the expenses again",Toast.LENGTH_LONG).show();
                    cursor=db.getCursorExpense();
                }
                dialog.dismiss();
                break;

            case 6:
                cursor=db.getExpensesFromNewestToOldest();
                dialog.dismiss();


        }


    }


}
