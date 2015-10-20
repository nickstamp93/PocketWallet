package com.ngngteam.pocketwallet.Utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;
import com.ngngteam.pocketwallet.R;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by Nick Zisis on 03-Oct-15.
 * Class RestoreDropbox perform a restore on Money and Categories Database that is saved on Dropbox.
 */
public class RestoreDropbox extends AsyncTask<String, Integer, String> {

    private DropboxAPI<AndroidAuthSession> api;
    private Context context;
    private boolean exists;

    private ProgressDialog progressDialog;
    private TextView tvCommand;


    public RestoreDropbox(DropboxAPI<AndroidAuthSession> api, Context context, SettingsActivity.BackupRestoreDialog dialog) {
        this.api = api;
        this.context = context;
        exists = true;


        dialog.dismiss();

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_bar);
        tvCommand = (TextView) progressDialog.findViewById(R.id.tvCommand);
        tvCommand.setText(context.getResources().getString(R.string.downloading));
    }

    @Override
    protected String doInBackground(String... params) {


        try {
            String MoneyDBpath = "/data/" + context.getPackageName() + "/databases/MoneyDatabase";
            String CategoriesDBpath = "/data/" + context.getPackageName() + "/databases/categories";

            OutputStream MoneyOutput = new FileOutputStream(Environment.getDataDirectory() + MoneyDBpath);
            OutputStream CategoriesOutput = new FileOutputStream(Environment.getDataDirectory() + CategoriesDBpath);


            DropboxAPI.DropboxInputStream input = api.getFileStream("TransactionsBackup", null);
            DropboxAPI.DropboxInputStream Cinput = api.getFileStream("CategoriesBackup", null);


            byte[] buffer = new byte[1024];
            int size;


            while ((size = input.read(buffer)) > 0) {
                MoneyOutput.write(buffer, 0, size);

            }

            MoneyOutput.flush();
            MoneyOutput.close();
            input.close();


            buffer = new byte[1024];
            while ((size = Cinput.read(buffer)) > 0) {
                CategoriesOutput.write(buffer, 0, size);
            }

            CategoriesOutput.flush();
            CategoriesOutput.close();
            Cinput.close();


        } catch (DropboxException e) {
            e.printStackTrace();
            exists = false;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exists = false;

        } catch (IOException e) {
            e.printStackTrace();

        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        progressDialog.dismiss();
        api.getSession().unlink();
        if (exists) {
            Toast.makeText(context, context.getResources().getString(R.string.dropbox_restore), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.no_dropbox_backup), Toast.LENGTH_LONG).show();
        }
    }

}
