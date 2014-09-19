package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CalendarView;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 15/9/2014.
 */
public class FiltersDateDialog extends CalendarDialog {

    private CalendarView cv ;
    private Dialog dialog;
    private Date dat;
    private String date;

    public interface Date{
        public void getDateFromDialogFragment(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_date_dialog);

        initUI();
        initListeners();

        Log.i("Nikos", "FiltersDateDialog");


        return dialog;
    }

    private void initUI(){
        cv=(CalendarView)dialog.findViewById(R.id.cvfilters);
        dat=(Date) getTargetFragment();

    }

    private void initListeners(){

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                date=dayOfMonth+"-"+month+"-"+year;
                dat.getDateFromDialogFragment(date);
                getTargetFragment().onResume();
                dialog.dismiss();
            }
        });

    }



}
