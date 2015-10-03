package com.ngngteam.pocketwallet.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Vromia on 21/09/2015.
 */
public class BackupDropbox extends AsyncTask<String,String,String> {

    private DropboxAPI<AndroidAuthSession> api;
    private Context context;


    public BackupDropbox(DropboxAPI<AndroidAuthSession> api,Context context,SettingsActivity.BackupRestoreDialog dialog ){
        this.api=api;
        this.context=context;

        dialog.dismiss();
    }

    @Override
    protected String doInBackground(String... params) {
//        InputStream input;
//        OutputStream output;
//
//        String SDPath = Environment.getExternalStorageDirectory().getPath();
//        String DBPath="/data/"+context.getPackageName()+"/databases/MoneyDatabase";
//
//        try {
//
//            input = new FileInputStream(Environment.getDataDirectory() + DBPath);
//            //Set the output folder on the SD card
//
//            File directory = new File(SDPath + "/Cash");
//            //If this directory doesn't exist create it
//            if (!directory.exists()) {
//                directory.mkdir();
//            }
//
//            output = new FileOutputStream(directory.getPath() + "/MoneyDatabase");
//
//
//            byte[] buffer = new byte[1024];
//            int size;
//            while ((size = input.read(buffer)) > 0) {
//                output.write(buffer, 0, size);
//            }
//
//            output.flush();
//            output.close();
//            input.close();
//
//
//
//
//        } catch (FileNotFoundException e) {
//            Toast.makeText(context, "FILE ERROR " + e, Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        } catch (IOException e) {
//            //Toast.makeText(context, "IO ERROR " + e, Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
        String MoneyDBpath = "/data/" +context.getPackageName() + "/databases/MoneyDatabase";
        String MoneyOutputName = "/MoneyDatabase";
        BackupRestoreSD backupRestoreSD = new BackupRestoreSD(MoneyDBpath, MoneyOutputName, context);
        backupRestoreSD.backup();

        File MoneyBackup = new File(backupRestoreSD.getSDPath() + "/Cash/MoneyDatabase");

        String CategoriesDBpath = "/data/" + context.getPackageName() + "/databases/categories";
        String CategoriesOutputName = "/CategoryDatabase";
        backupRestoreSD = new BackupRestoreSD(CategoriesDBpath, CategoriesOutputName, context);
        backupRestoreSD.backup();


        File CategoryBackup = new File(backupRestoreSD.getSDPath() + "/Cash/CategoryDatabase");

        try {


            InputStream Mis=new FileInputStream(MoneyBackup);
            InputStream Cis=new FileInputStream(CategoryBackup);


            //List<DropboxAPI.Entry> search=api.search("https://api.dropbox.com/1/metadata/auto/Apps/Pocket-Wallet","Backup",1000,false);
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




//            if(metadata.bytes!=0 ){
//                Log.i("List", "not empty");
//                api.delete("/Backup");
//                api.putFile("Backup", is, backup.length(), null, null);
//            }else {
//                Log.i("List","empty");
//                api.putFile("Backup", is, backup.length(), null, null);
//            }


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

        api.getSession().unlink();
        Toast.makeText(context,"Backup was done successfully on Dropbox",Toast.LENGTH_LONG).show();
    }
}
