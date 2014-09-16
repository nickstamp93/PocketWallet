package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Eleni on 28/7/2014.
 */
public class CalendarDialog extends DialogFragment {

    //dialog UI elements
    private Button bOk,bCancel;
    private CalendarView cv;

    //dialog variable
    private Dialog dialog;
    //string var for the date chosen
    private String date;
    //context
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get the parent activity context
        context=getActivity();

        //init dialog
        dialog=new Dialog(context);
        //and remove title feature
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //set the dialog content view
        dialog.setContentView(R.layout.calendar_dialog);

        //get current time , to set the dialog's initial values
        Time now=new Time();
        now.setToNow();
        date=now.monthDay+"-"+now.month+"-"+now.year;

        initUI();
        initListeners();

        return dialog;
    }

    //initialize UI elements
    private void initUI(){
        bOk=(Button)dialog.findViewById(R.id.bdOk);
        bCancel=(Button) dialog.findViewById(R.id.bdCancel);
        cv=(CalendarView) dialog.findViewById(R.id.cvdialog);
    }

    //define listeners
    private void initListeners(){
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,"Process Finished, the date that you chose is "+date ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date="No one";
                Toast.makeText(context,"Process Canceled",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                date=dayOfMonth+"-"+month+"-"+year;
                Toast.makeText(context,"Selected Date\n\n"+date,Toast.LENGTH_LONG).show();
            }
        });

    }

    public String getDate(){
        return date;
    }


}
