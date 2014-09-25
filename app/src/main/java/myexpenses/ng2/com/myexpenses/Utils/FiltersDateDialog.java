package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;
import android.widget.CalendarView;

import myexpenses.ng2.com.myexpenses.Activities.HistoryActivity;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 15/9/2014.
 * FiltersDateDialog class that extends DialogFragment is used when we want to find expenses or incomes by their date.
 * HistoryActivity calls this class more specific in filters.
 */
public class FiltersDateDialog extends DialogFragment {

    private CalendarView cv ;
    private Dialog dialog;
    private String date;
    //variable activity is to check if this class is called from the HistoryActivity or FiltersDateToDateDialog
    //and variable expense is to check if this class is called to find expenses or incomes
    private boolean activity,expense;
    private Date dat;
//Interface Date is used to send-retrieve data from a Parent Fragment to a child fragment
   public interface Date{
        public void getDateFromDialogFragment(String date);
    }

    public FiltersDateDialog(boolean activity,boolean expense){
        this.activity=activity;
        this.expense=expense;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_date_dialog);

        initUI();
        initListeners();


        return dialog;
    }

    private void initUI(){
        cv=(CalendarView)dialog.findViewById(R.id.cvfilters);
        //if activity is false it means that the parent fragment FiltersDateToDateDialog is called this fragment so we need
        //to initialize the interface Date dat.
        if(!activity){
            dat=(Date) getTargetFragment();
        }

    }

    private void initListeners(){

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String day=dayOfMonth+"",mon=month+"";
                //Check if the day and month is <10 to add a leading zero in front of them
                if(dayOfMonth<10){
                    day="0"+dayOfMonth;
                }if(month<10){
                    mon="0"+month;
                }
                date=day+"-"+mon+"-"+year;
              //if activity is true it means that HistoryActivity called this fragment and then we need to check
              //if the date is for Income or Expense and call the appropriate method to initialize the date.
                if(activity) {
                    HistoryActivity act=(HistoryActivity)getActivity();
                    if(expense) act.saveExpenseFiltersDate(date);
                    else act.saveIncomeFiltersDate(date);
               //else the parent Fragment called this fragment so we need to initialize also the variable date to the parent
                //fragment. So we call the method of the interface getDateFromDialogFragment that has been implemented in the
                 //parent Fragment
                }else {
                    dat.getDateFromDialogFragment(date);
                }

                dialog.dismiss();
            }
        });

    }



}
