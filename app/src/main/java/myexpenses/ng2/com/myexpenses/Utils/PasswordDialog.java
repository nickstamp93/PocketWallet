package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 9/23/2014.
 */
public class PasswordDialog extends DialogFragment {

    private EditText etCurrent , etNew , etNewRe;
    private Button bOk , bCancel;
    private CheckBox chbShowPass;
    Dialog dialog;
    SharedPrefsManager manager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity());

        //remove the dialog feature
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.password_dialog);

        initUI();

        initListeners();


        return dialog;
    }


    private void initUI() {
        etCurrent = (EditText) dialog.findViewById(R.id.etCurrentPass);
        etNew = (EditText) dialog.findViewById(R.id.etNewPass);
        etNewRe = (EditText) dialog.findViewById(R.id.etNewPassRe);

        chbShowPass = (CheckBox) dialog.findViewById(R.id.chbShowPass);


        bOk = (Button) dialog.findViewById(R.id.bOk);
        bCancel = (Button) dialog.findViewById(R.id.bCancel);

    }


    private void initListeners() {
        bOk.setOnClickListener(listener);
        bCancel.setOnClickListener(listener);


        chbShowPass.setOnCheckedChangeListener(chbListener);
    }

    private   CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER);
            }else{
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        }
    };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bOk:
                    manager = new SharedPrefsManager(getActivity());
                    String pass = manager.getPrefsPassword();

                    //check if the 2 new passes are the same and the current pass is correct
                    if(etCurrent.getText().toString().equals(pass) && etNew.getText().toString().equals(etNewRe.getText().toString())){
                        manager.startEditing();
                        manager.setPrefsPassword(etNew.getText().toString());
                        manager.commit();
                        Toast.makeText(getActivity(), "Pass Changed" , Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Failed to change password" , Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bCancel:
                    dismiss();
            }
        }
    };
}
