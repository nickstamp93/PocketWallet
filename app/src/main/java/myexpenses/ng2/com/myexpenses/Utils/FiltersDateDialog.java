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
 */
public class FiltersDateDialog extends DialogFragment {

    private CalendarView cv ;
    private Dialog dialog;
    private String date;
    private boolean activity,expense;
    private Date dat;

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
        if(!activity){
            dat=(Date) getTargetFragment();
        }

    }

    private void initListeners(){

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String day=dayOfMonth+"",mon=month+"";
                if(dayOfMonth<10){
                    day="0"+dayOfMonth;
                }if(month<10){
                    mon="0"+month;
                }
                date=day+"-"+mon+"-"+year;

                if(activity) {
                    HistoryActivity act=(HistoryActivity)getActivity();
                    if(expense) act.saveExpenseFiltersDate(date);
                    else act.saveIncomeFiltersDate(date);

                }else {
                    dat.getDateFromDialogFragment(date);
                }

                dialog.dismiss();
            }
        });

    }



}
