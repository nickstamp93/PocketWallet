package myexpenses.ng2.com.myexpenses.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 8/20/2014.
 */
public class DateFormatDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a Format");
        builder.setItems(R.array.date_formats , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String formats[] = getResources().getStringArray(R.array.date_formats);
                String selected = formats[which];
                Log.i("nikos" , selected);
                getDialog().dismiss();
            }
        });
        return builder.create();
    }
}
