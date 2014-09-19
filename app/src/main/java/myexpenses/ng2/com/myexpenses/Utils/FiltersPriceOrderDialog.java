package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.Data.MoneyDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 15/9/2014.
 */
public class FiltersPriceOrderDialog extends DialogFragment {

    private Button bOk,bCancel;
    private RadioGroup rgPriceOrder;
    private RadioButton rbOrder;

    private Dialog dialog;
    private PriceOrder order;
    private String priceOrder;

    public interface PriceOrder{
        public void getPriceOrderFromDialogFragment(String priceOrder);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog=new Dialog(getActivity());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filters_priceorder_dialog);

        initUi();
        initListeners();

        return dialog;
    }

    private void initUi(){


        rgPriceOrder=(RadioGroup)dialog.findViewById(R.id.rgPriceOrder);
        bOk=(Button) dialog.findViewById(R.id.bdPOk);
        bCancel=(Button)dialog.findViewById(R.id.bdPCancel);

        order=(PriceOrder) getTargetFragment();

        int selectedId=rgPriceOrder.getCheckedRadioButtonId();
        rbOrder=(RadioButton) rgPriceOrder.findViewById(selectedId);
        priceOrder=rbOrder.getText().toString();
        order.getPriceOrderFromDialogFragment(priceOrder);

    }

    private void initListeners(){
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId=rgPriceOrder.getCheckedRadioButtonId();
                rbOrder=(RadioButton) rgPriceOrder.findViewById(selectedId);
                priceOrder=rbOrder.getText().toString();
                order.getPriceOrderFromDialogFragment(priceOrder);
                getTargetFragment().onResume();
                dialog.dismiss();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Return back no choice happened", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

}
