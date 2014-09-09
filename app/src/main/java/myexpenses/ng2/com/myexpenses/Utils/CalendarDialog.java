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

    private Button bOk,bCancel;
    private CalendarView cv;

    private Dialog dialog;
    private String date;
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context=getActivity();

        dialog=new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);

        Time now=new Time();
        now.setToNow();
        date=now.monthDay+"-"+now.month+"-"+now.year;

        initUI();
        initListeners();

        return dialog;
    }

    private void initUI(){
        bOk=(Button)dialog.findViewById(R.id.bdOk);
        bCancel=(Button) dialog.findViewById(R.id.bdCancel);
        cv=(CalendarView) dialog.findViewById(R.id.cvdialog);
    }

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
