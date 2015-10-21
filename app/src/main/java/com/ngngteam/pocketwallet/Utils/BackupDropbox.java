package com.ngngteam.pocketwallet.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;
import com.ngngteam.pocketwallet.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Nick Zisis on 21/09/2015.
 * Class BackupDropbox extends a thread of Android and it creates a backup of the databases on DropBox
 */
public class BackupDropbox extends AsyncTask<String,String,String> {

    private DropboxAPI<AndroidAuthSession> api;
    private Context context;

    private ProgressDialog progressDialog;
    private TextView tvCommand;

    public BackupDropbox(DropboxAPI<AndroidAuthSession> api,Context context,SettingsActivity.BackupRestoreDialog dialog ){
        this.api=api;
        this.context=context;

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
        tvCommand.setText(context.getString(R.string.uploading));
    }

    /**
     * First of all create a backup of Money and Categories Database on the SD card. After that, get those backups from the
     * SD card and upload it to the DropBox after the app checks if it already exist. If the backup already exists then delete
     * the old and create the new one.
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {

        String MoneyDBpath = "/data/" +context.getPackageName() + "/databases/MoneyDatabase";
        String MoneyOutputName = "/TransactionsBackup";
        BackupRestoreSD backupRestoreSD = new BackupRestoreSD(MoneyDBpath, MoneyOutputName, context);
        backupRestoreSD.backup();

        File MoneyBackup = new File(backupRestoreSD.getSDPath() + "/Pocket-Wallet/TransactionsBackup");

        String CategoriesDBpath = "/data/" + context.getPackageName() + "/databases/categories";
        String CategoriesOutputName = "/CategoriesBackup";
        backupRestoreSD = new BackupRestoreSD(CategoriesDBpath, CategoriesOutputName, context);
        backupRestoreSD.backup();


        File CategoryBackup = new File(backupRestoreSD.getSDPath() + "/Pocket-Wallet/CategoriesBackup");

        try {


            InputStream Mis=new FileInputStream(MoneyBackup);
            InputStream Cis=new FileInputStream(CategoryBackup);



            DropboxAPI.Entry metadata=api.metadata("/",1000,null,true,null);


            boolean flag=false,cflag=false;
            List<DropboxAPI.Entry> CFolder = metadata.contents;

            for (DropboxAPI.Entry entry : CFolder) {
               if(entry.fileName().equals("TransactionsBackup") ){
                   flag=true;
               }else if( entry.fileName().equals("CategoriesBackup")){
                   cflag=true;
               }

                if(flag && cflag){
                    break;
                }

            }

            if(!flag){
                api.putFile("TransactionsBackup", Mis, MoneyBackup.length(), null, null);

            }else{
                api.delete("/TransactionsBackup");
                api.putFile("TransactionsBackup", Mis, MoneyBackup.length(), null, null);

            }


            if(!cflag){
                api.putFile("CategoriesBackup", Cis, CategoryBackup.length(), null, null);
            }else {
                api.delete("/CategoriesBackup");
                api.putFile("CategoriesBackup", Cis, CategoryBackup.length(), null, null);

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (DropboxException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        api.getSession().unlink();
        Toast.makeText(context,context.getString(R.string.dropbox_backup),Toast.LENGTH_LONG).show();
    }
}
