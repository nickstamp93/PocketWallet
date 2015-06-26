package com.ngngteam.pocketwallet.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.ngngteam.pocketwallet.R;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;

/**
 * Created by Nikos on 26/6/2015.
 */
public class ChangelogDialog extends DialogFragment {

    public ChangelogDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogRecyclerView chgList= (ChangeLogRecyclerView) layoutInflater.inflate(R.layout.dialog_changelog, null);

        return new AlertDialog.Builder(getActivity() , R.style.AppCompatAlertDialogStyle)
                .setTitle("Title")
                .setView(chgList)
                .setPositiveButton(R.string.button_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();

    }
}
