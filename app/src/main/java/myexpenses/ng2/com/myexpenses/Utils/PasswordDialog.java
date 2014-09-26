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
//dialog for changing the app password
public class PasswordDialog extends DialogFragment {

    //pass edit texts
    private EditText etCurrent , etNew , etNewRe;
    //buttons
    private Button bOk , bCancel;
    //pass check box(for showing passwords)
    private CheckBox chbShowPass;
    //dialog object
    Dialog dialog;
    //shared prefs manager
    SharedPrefsManager manager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //init dialog object
        dialog = new Dialog(getActivity());

        //remove the title feature
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set content view
        dialog.setContentView(R.layout.password_dialog);

        //init UI
        initUI();

        //init listeners
        initListeners();

        //return dialog object
        return dialog;
    }


    //init UI
    private void initUI() {
        etCurrent = (EditText) dialog.findViewById(R.id.etCurrentPass);
        etNew = (EditText) dialog.findViewById(R.id.etNewPass);
        etNewRe = (EditText) dialog.findViewById(R.id.etNewPassRe);

        chbShowPass = (CheckBox) dialog.findViewById(R.id.chbShowPass);


        bOk = (Button) dialog.findViewById(R.id.bOk);
        bCancel = (Button) dialog.findViewById(R.id.bCancel);

    }

//init listeners
    private void initListeners() {
        bOk.setOnClickListener(listener);
        bCancel.setOnClickListener(listener);

        chbShowPass.setOnCheckedChangeListener(chbListener);
    }

    //check box listener
    private   CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                //if checked , set passwords visible
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER);
            }else{
                //else , hide them (show asterisks)
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        }
    };

    //click listener
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bOk:
                    //button ok clicked
                    //init manager
                    manager = new SharedPrefsManager(getActivity());
                    //get saved password
                    String pass = manager.getPrefsPassword();

                    //check if the 2 new passes are the same and the current pass is correct
                    if(etCurrent.getText().toString().equals(pass) && etNew.getText().toString().equals(etNewRe.getText().toString())){
                        //if all that are ok , store the new pass to the prefs file
                        manager.startEditing();
                        manager.setPrefsPassword(etNew.getText().toString());
                        manager.commit();
                        //notify the user
                        Toast.makeText(getActivity(), "Pass Changed" , Toast.LENGTH_SHORT).show();
                        //close dialog
                        dismiss();
                    }else{
                        //else , something went wrong , don't change a thing
                        Toast.makeText(getActivity(), "Failed to change password" , Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bCancel:
                    //button cancel clicked
                    //close dialog without saving any changes
                    dismiss();
            }
        }
    };
}
