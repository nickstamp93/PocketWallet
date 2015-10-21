package com.ngngteam.pocketwallet.Utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick Zisis on 17/07/2015.
 */
public class BackupRestoreSD {

    private InputStream input;
    private OutputStream output;
    private String DBPath;
    private String SDPath;
    private String outputName;
    private Context context;

    public BackupRestoreSD(String DBPath, String outputName, Context context) {
        this.DBPath = DBPath;
        this.outputName = outputName;
        this.context = context;

        SDPath = Environment.getExternalStorageDirectory().getPath();

    }

    public boolean backup() {

        boolean success = false;

        try {

            input = new FileInputStream(Environment.getDataDirectory() + DBPath);
            //Set the output folder on the SD card

            File directory = new File(SDPath + "/Pocket-Wallet");
            //If this directory doesn't exist create it
            if (!directory.exists()) {
                directory.mkdir();
            }

            output = new FileOutputStream(directory.getPath() + outputName);


            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) > 0) {
                output.write(buffer, 0, size);
            }

            output.flush();
            output.close();
            input.close();

            success = true;


        } catch (FileNotFoundException e) {
            Toast.makeText(context, "FILE ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context, "IO ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return success;
    }

    public boolean restore() {
        boolean success = false;
        try {

            output = new FileOutputStream(Environment.getDataDirectory() + DBPath);


            File directory = new File(SDPath + "/Pocket-Wallet");


            input = new FileInputStream(directory.getPath() + outputName);


            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) > 0) {
                output.write(buffer, 0, size);
            }

            output.flush();
            output.close();
            input.close();

            success = true;


        } catch (FileNotFoundException e) {
            Toast.makeText(context, "FILE ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context, "IO ERROR " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        return success;


    }


    public String getSDPath() {
        return SDPath;
    }


}
