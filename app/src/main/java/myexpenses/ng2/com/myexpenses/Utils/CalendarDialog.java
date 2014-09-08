package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Eleni on 28/7/2014.
 */
public class CalendarDialog {

 private Button Ok,Cancel;
 private CalendarView cv;
 private Dialog dialog;
 private Context context;
 private String date;

   public CalendarDialog(Context context){

       this.context=context;
       dialog=new Dialog(context);
       dialog.setContentView(R.layout.calendar_dialog);

       Ok=(Button) dialog.findViewById(R.id.bdOk);
       Cancel=(Button) dialog.findViewById(R.id.bdCancel);
       cv=(CalendarView) dialog.findViewById(R.id.cvdialog);

       Time now=new Time();
       now.setToNow();
       date=now.monthDay+"-"+now.month+"-"+now.year;

       initListeners();

       dialog.show();
   }

   private void initListeners(){
      Ok.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              Toast.makeText(context,"Process Finished, the date that you chose is "+date ,Toast.LENGTH_SHORT).show();
              dialog.dismiss();
          }
      });

      Cancel.setOnClickListener(new View.OnClickListener() {
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
