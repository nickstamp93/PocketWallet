package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Activities.AddExpenseActivity;
import myexpenses.ng2.com.myexpenses.Activities.AddIncomeActivity;
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
    private boolean expense;

    public CalendarDialog(boolean expense){
        this.expense=expense;
    }


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
        String day=now.monthDay+"",month=now.month+"";
        //date=now.monthDay+"-"+now.month+"-"+now.year;
        if(now.monthDay<10){
            day="0"+now.monthDay;
        } if(now.month<10){
            month="0"+now.month;
        }
        date=now.year+"-"+month+"-"+day;

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
                if(expense) {
                    AddExpenseActivity act = (AddExpenseActivity) getActivity();
                    act.setDate(date);
                }else{
                    AddIncomeActivity act=(AddIncomeActivity) getActivity();
                    act.setIncomeDate(date);
                }
                dialog.dismiss();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //date="No one";
                Toast.makeText(context,"Process Canceled",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                //date=dayOfMonth+"-"+month+"-"+year;
                String day=dayOfMonth+"",mon=month+"";
                //date=now.monthDay+"-"+now.month+"-"+now.year;

                Log.i("Month=", mon);
                if(Integer.parseInt(day)<10){
                    day="0"+dayOfMonth;
                }
                 if(Integer.parseInt(mon)<10){
                    mon="0"+month;
                }
                //date=now.year+"-"+now.month+"-"+day;
                date=year+"-"+mon+"-"+day;
                Toast.makeText(context,"Selected Date\n\n"+date,Toast.LENGTH_LONG).show();
            }
        });

    }

    public String getDate(){
        return date;
    }


}
