package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.Activities.AddExpenseActivity;
import myexpenses.ng2.com.myexpenses.Activities.OverviewActivity;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 7/11/2014.
 */
public class PriceDialog extends DialogFragment {

    private Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bOk;
    private EditText etPassword;
    private Dialog dialog;
    private String price;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog=new Dialog(getActivity());

        //remove title feature from dialog
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set content view
        dialog.setContentView(R.layout.activity_password);

        initUi();
        initListeners();


        return dialog;
    }

    public void initUi(){
        b1 = (Button) dialog.findViewById(R.id.b1);
        b2 = (Button) dialog.findViewById(R.id.b2);
        b3 = (Button) dialog.findViewById(R.id.b3);
        b4 = (Button) dialog.findViewById(R.id.b4);
        b5 = (Button) dialog.findViewById(R.id.b5);
        b6 = (Button) dialog.findViewById(R.id.b6);
        b7 = (Button) dialog.findViewById(R.id.b7);
        b8 = (Button) dialog.findViewById(R.id.b8);
        b9 = (Button) dialog.findViewById(R.id.b9);
        b0 = (Button) dialog.findViewById(R.id.b0);
        bOk = (Button) dialog.findViewById(R.id.bOk);

        etPassword = (EditText) dialog.findViewById(R.id.etPassword);

        price="";
    }

    //init listeners
    private void initListeners(){
        b1.setOnClickListener(listener);
        b2.setOnClickListener(listener);
        b3.setOnClickListener(listener);
        b4.setOnClickListener(listener);
        b5.setOnClickListener(listener);
        b6.setOnClickListener(listener);
        b7.setOnClickListener(listener);
        b8.setOnClickListener(listener);
        b9.setOnClickListener(listener);
        b0.setOnClickListener(listener);
        bOk.setOnClickListener(listener);
    }

    //buttons listener
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.bOk){

                AddExpenseActivity act=(AddExpenseActivity) getActivity();
                act.setPrice(price);

                dialog.dismiss();

            }else{
                price += ((Button) v).getText().toString();
                etPassword.setText(price);
            }

        }


    };


}
