package myexpenses.ng2.com.myexpenses.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import myexpenses.ng2.com.myexpenses.Activities.SettingsActivity;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 9/23/2014.
 */
public class PasswordDialog extends DialogPreference {

    //pass edit texts
    private EditText etCurrent, etNew, etNewRe;

    //buttons
    private Button bOk, bCancel;

    //pass check box(for showing passwords)
    private CheckBox chbShowPass;

    //dialog object
    private Dialog dialog;

    //Context
    private Context context;

    public PasswordDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        setPersistent(false);
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

        initUI(view);

        initListeners();

    }

    private void initListeners() {

        chbShowPass.setOnCheckedChangeListener(chbListener);

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences();

                String pass = prefs.getString("pref_key_password_value", "");

                //check if the 2 new passes are the same , not empty and the current pass is correct
                if (etCurrent.getText().toString().equals(pass) &&
                        etNew.getText().toString().equals(etNewRe.getText().toString())
                        && !etNew.getText().toString().trim().equals("")) {

                    //if all that are ok , store the new pass to the prefs file
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("pref_key_password_value", etNew.getText().toString());
                    editor.commit();

                    //notify the user
                    Toast.makeText(getContext(), "Pass Changed", Toast.LENGTH_SHORT).show();

                    //close dialog
                    getDialog().dismiss();
                } else {
                    //else , something went wrong , don't change a thing
                    Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SettingsActivity) context).disablePassword();
                getDialog().dismiss();
            }
        });
    }

    private void initUI(View view) {
        etCurrent = (EditText) view.findViewById(R.id.etCurrentPass);

        SharedPreferences prefs = getSharedPreferences();

        String pass = prefs.getString("pref_key_password_value", "");
        if (pass.trim().equals("1234")) {
            etCurrent.setText("1234");
            etCurrent.setEnabled(false);
        }

        etNew = (EditText) view.findViewById(R.id.etNewPass);
        etNewRe = (EditText) view.findViewById(R.id.etNewPassRe);

        chbShowPass = (CheckBox) view.findViewById(R.id.chbShowPass);

        bOk = (Button) view.findViewById(R.id.bOk);
        bCancel = (Button) view.findViewById(R.id.bCancel);

    }


    //check box listener
    private CompoundButton.OnCheckedChangeListener chbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                //if checked , set passwords visible
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                //else , hide them (show asterisks)
                etNew.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                etNewRe.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        }
    };

}
