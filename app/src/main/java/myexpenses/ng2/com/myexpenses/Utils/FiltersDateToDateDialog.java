package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 15/9/2014.
 * FiltersDateToDateDialog class that extends DialogFragment is used when we want to find expenses or incomes with date between two
 * dates. HistoryActivity calls this class more specific in filters. It also implement the Interface Date from Fragment
 * FiltersDateDialog because we need to send data from FiltersDateDialog to FiltersDateToDateDialog
 *
 */
public class FiltersDateToDateDialog extends DialogFragment implements FiltersDateDialog.Date {

    private Button bOk,bCancel,bFromDate,bToDate;
    private TextView tvFrom,tvTo;
    private Dialog dialog;
    private String from,to;
    //We use the fragment FiltersDateDialog to initialise the fields from and to that are our dates.
    private FiltersDateDialog fdialog;
    //variable flag is used because we want to know which button the user pressed so we initialise the right date
    //and variable expense is to check if this fragment is called for expenses or incomes
    private boolean flag,expense;


    public FiltersDateToDateDialog(boolean expense){
        this.expense=expense;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_datetodate_dialog);

        initUI();
        initListeners();

        return dialog;
    }


    public void initUI(){

        bOk=(Button) dialog.findViewById(R.id.bDTDOk);
        bCancel=(Button)dialog.findViewById(R.id.bDTDCancel);
        bFromDate=(Button) dialog.findViewById(R.id.bDTDFrom);
        bToDate=(Button) dialog.findViewById(R.id.bDTDTo);
        tvFrom=(TextView) dialog.findViewById(R.id.tvDTDFrom);
        tvTo=(TextView) dialog.findViewById(R.id.tvDTDTo);
        //initialise the dialog fragment and also set the target fragment to be this parent fragment because we need
        //to take some data from child fragment FiltersDateDialog
        fdialog=new FiltersDateDialog(false,expense);
        fdialog.setTargetFragment(FiltersDateToDateDialog.this,0);

    }


    public void initListeners(){

       bFromDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               flag=true;
               fdialog.show(getFragmentManager(),"FiltersDate dialog");

           }
       });

        bToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=false;
                fdialog.show(getFragmentManager(),"FiltersDate dialog");

            }
        });

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DateFrom[]=from.split("-");
                String DateTo[]=to.split("-");
                String reformedDateFrom=DateFrom[2]+"-"+DateFrom[1]+"-"+DateFrom[0];
                String reformedDateTo=DateTo[2]+"-"+DateTo[1]+"-"+DateTo[0];
               //When we press the Ok we check if the user press two dates and also if the dates are ok.
                if(to==null || from==null || reformedDateFrom.compareTo(reformedDateTo)>=0 ){
                    Toast.makeText(getActivity(),"Problem with your dates plz check them and try again",Toast.LENGTH_LONG).show();
                }else{

                    HistoryActivity activity=(HistoryActivity) getActivity();
                    //If expense is true it means that this fragment is called for expenses so we call the method
                    //saveFiltersDateToDate from HistoryActivity. Otherwise is called for incomes so we call the
                    //method saveIncomeFiltersDateToDate
                    if(expense) {
                        activity.saveFiltersDateToDate(from, to);
                    }else{
                        activity.saveIncomeFiltersDateToDate(from,to);
                    }
                    dialog.dismiss();

                }

            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Return back no choice happened",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });


    }

//getDateFromDialogFragment is the method from the interface Date and we need to override it because this class
 //implements Date
    @Override
    public void getDateFromDialogFragment(String date) {
//if the flag is true it means that the user press the Date (from) so we initialise the string variable from and also
//we set the Text of TextView tvFrom. Otherwise we do the same for Date (To).
        if(flag){
            from=date;
            tvFrom.setText(from);
        }else{
            to=date;
            tvTo.setText(to);
        }

    }
}
