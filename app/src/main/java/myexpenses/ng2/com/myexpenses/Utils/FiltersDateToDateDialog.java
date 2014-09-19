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

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 15/9/2014.
 */
public class FiltersDateToDateDialog extends DialogFragment implements FiltersDateDialog.Date {

    private Button bOk,bCancel,bFromDate,bToDate;
    private TextView tvFrom,tvTo;
    private Dialog dialog;
    private String from,to;
    private FiltersDateDialog fdialog;
    private boolean flag;
    private DatetoDate dtd;

    public interface DatetoDate{
        public void getDatetoDateFromDialogFragment(String from,String to);
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

        fdialog=new FiltersDateDialog();
        fdialog.setTargetFragment(FiltersDateToDateDialog.this,0);


        dtd=(DatetoDate) getTargetFragment();

    }


    public void initListeners(){

       bFromDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               flag=true;
               Log.i("Nikos","bike");
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

                if(to==null || from==null || reformedDateFrom.compareTo(reformedDateTo)>=0 ){
                    Toast.makeText(getActivity(),"Problem with your dates plz check them and try again",Toast.LENGTH_LONG).show();
                }else{
                    dtd.getDatetoDateFromDialogFragment(from,to);
                    getTargetFragment().onResume();
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


    @Override
    public void getDateFromDialogFragment(String date) {

        if(flag){
            from=date;
            tvFrom.setText(from);
        }else{
            to=date;
            tvTo.setText(to);
        }

    }
}
