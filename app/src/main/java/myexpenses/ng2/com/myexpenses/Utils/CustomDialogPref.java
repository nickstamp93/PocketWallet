package myexpenses.ng2.com.myexpenses.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 16/10/2014.
 */
public class CustomDialogPref extends DialogPreference {

    public CustomDialogPref(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_currency);

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        builder.setTitle("Select Currency");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel" , null);

    }

}
