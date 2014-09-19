package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import myexpenses.ng2.com.myexpenses.Activities.ReminderSettingsActivity;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 9/12/2014.
 */
public class TimeDialog extends DialogFragment  implements TimePickerDialog.OnTimeSetListener{

    //UI elements
    TextView tvReminderTime;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get a calendar instance to have access to the current time
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);

        int minute = c.get(Calendar.MINUTE);

        //init the textview from the parent activity
        tvReminderTime = (TextView) getActivity().findViewById(R.id.tvReminderTime);

        // Create a new instance of TimePickerDialog with the current time and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // when time is set by the user
        //save the new time in the preference file
        SharedPrefsManager manager = new SharedPrefsManager(getActivity().getApplicationContext());
        manager.startEditing();
        manager.setPrefsReminderTime(hourOfDay , minute);
        manager.commit();
        //and update the parent textview with the new time
        tvReminderTime.setText(manager.getPrefsReminderTime());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        //on dialog dismiss , set up the notification with the new time
        ((ReminderSettingsActivity)getActivity()).setAlarm();


    }
}
