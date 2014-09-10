package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 8/21/2014.
 */
public class CurrencyDialog extends DialogFragment {

    SharedPrefsManager manager;

    Button bEuro,bDollar,bYen,bPound,bSwissFranc,bRupee;
    TextView tvCurrency;
    Dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_currency);

        initUI();

        initListeners();

        return dialog;

    }

    private void initUI(){

        bEuro = (Button) dialog.findViewById(R.id.bEuro);
        bDollar = (Button) dialog.findViewById(R.id.bDollar);
        bYen = (Button) dialog.findViewById(R.id.bYen);
        bPound = (Button) dialog.findViewById(R.id.bPound);
        bSwissFranc = (Button) dialog.findViewById(R.id.bSwissFranc);
        bRupee = (Button) dialog.findViewById(R.id.bRupee);

        tvCurrency = (TextView) getActivity().findViewById(R.id.tvCurrency);

    }

    private void initListeners(){
        bEuro.setOnClickListener(listener);
        bDollar.setOnClickListener(listener);
        bPound.setOnClickListener(listener);
        bRupee.setOnClickListener(listener);
        bYen.setOnClickListener(listener);
        bSwissFranc.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            manager = new SharedPrefsManager(getActivity().getApplicationContext());

            switch (v.getId()){
                case R.id.bEuro:
                    tvCurrency.setText(getResources().getString(R.string.currency_euro));
                    break;
                case R.id.bDollar:
                    tvCurrency.setText(getResources().getString(R.string.currency_dollar));
                    break;
                case R.id.bYen:
                    tvCurrency.setText(getResources().getString(R.string.currency_yen));
                    break;
                case R.id.bPound:
                    tvCurrency.setText(getResources().getString(R.string.currency_pound));
                    break;
                case R.id.bRupee:
                    tvCurrency.setText(getResources().getString(R.string.currency_rupee));
                    break;
                case R.id.bSwissFranc:
                    tvCurrency.setText(getResources().getString(R.string.currency_swiss_franc));
                    break;
            }

            manager.startEditing();
            manager.setPrefsCurrency(tvCurrency.getText().toString());
            manager.commit();

            dismiss();
        }
    };

}
