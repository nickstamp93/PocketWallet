package myexpenses.ng2.com.myexpenses.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.AttributeSet;
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
public class PasswordDialog extends DialogPreference {

    //pass edit texts
    private EditText etCurrent , etNew , etNewRe;
    //buttons
    private Button bOk , bCancel;
    //pass check box(for showing passwords)
    private CheckBox chbShowPass;
    //dialog object
    Dialog dialog;

    public PasswordDialog(Context context, AttributeSet attrs) {
        super(context, attrs);


        setDialogLayoutResource(R.layout.password_dialog);

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);


        builder.setTitle(null);
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);

        dialog = getDialog();



    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        etCurrent = (EditText) view.findViewById(R.id.etCurrentPass);

        SharedPreferences prefs = getSharedPreferences();

        String pass = prefs.getString("pref_key_password_value" , "");
        if(pass.equals("")){
            etCurrent.setEnabled(false);
        }

        etNew = (EditText) view.findViewById(R.id.etNewPass);
        etNewRe = (EditText) view.findViewById(R.id.etNewPassRe);

        chbShowPass = (CheckBox) view.findViewById(R.id.chbShowPass);


        bOk = (Button) view.findViewById(R.id.bOk);
        bCancel = (Button) view.findViewById(R.id.bCancel);

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences();

                String pass = prefs.getString("pref_key_password_value" , "");




                //check if the 2 new passes are the same and the current pass is correct
                if(etCurrent.getText().toString().equals(pass) && etNew.getText().toString().equals(etNewRe.getText().toString())){
                    //if all that are ok , store the new pass to the prefs file
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("pref_key_password_value" , etNew.getText().toString());
                    editor.commit();
                    //notify the user
                    Toast.makeText(getContext() , "Pass Changed" , Toast.LENGTH_SHORT).show();
                    //close dialog
                    getDialog().dismiss();
                }else{
                    //else , something went wrong , don't change a thing
                    Toast.makeText(getContext(), "Failed to change password" , Toast.LENGTH_SHORT).show();
                }



            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        //initListeners();

    }

    /*
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
    */

    public void show(){
        onClick();
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
/*
//init listeners
    private void initListeners() {
        bOk.setOnClickListener(listener);
        bCancel.setOnClickListener(listener);

        chbShowPass.setOnCheckedChangeListener(chbListener);
    }
*/
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

    /*
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
    */
}
