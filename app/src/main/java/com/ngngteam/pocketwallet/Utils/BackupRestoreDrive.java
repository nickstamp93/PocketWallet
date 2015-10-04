package com.ngngteam.pocketwallet.Utils;


import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick Zisis on 04/08/2015.
 */
public class BackupRestoreDrive {

    private GoogleApiClient client;
    private Context context;
    private SettingsActivity activity;
    private final int REQUEST_CODE_RESOLUTION = 1;
    private final int TRANSACTIONS_CODE=2,CATEGORIES_CODE=3;
    private boolean backup;

    public BackupRestoreDrive(final Context context,SettingsActivity act, final boolean backup){

        this.context=context;
        this.activity=act;
        this.backup=backup;

        client =new GoogleApiClient.Builder(context).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                //Log.i("Connected", "Client has connected");
                if (backup) {
                    DriveFileExists();
                    updateGoogleDriveContent();
                } else {
                   new Restore().execute();

                }


            }

            @Override
            public void onConnectionSuspended(int i)
            {
                //Log.i("Suspended", "Client has suspended");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), activity, 0).show();
                }
            }
        }).build();

       this.connect();


    }

    public void connect(){
        client.connect();
    }

    /**
     * Method createFileToDrive creates a folder on the Google Drive with unique attribute a private custom property key
     * and also save the id of this Drive Folder to the shared preferences of the app.
     */
    public void createFileToDrive(){


        CustomPropertyKey key=new CustomPropertyKey("pocket_folder",CustomPropertyKey.PRIVATE);
        MetadataChangeSet changeSet=new MetadataChangeSet.Builder().setTitle("Pocket-Wallet").setCustomProperty(key,"pocket_folder").build();

        Drive.DriveApi.getRootFolder(client).createFolder(client, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if (!driveFolderResult.getStatus().isSuccess()) {
                    Log.i("Folder", "Error while trying to create the folder");
                    return;
                }
                Log.i("Folder", "Folder created " + driveFolderResult.getDriveFolder().getDriveId());
                SharedPrefsManager manager = new SharedPrefsManager(context);
                manager.startEditing();
                manager.setPrefsDriverFolderId(driveFolderResult.getDriveFolder().getDriveId().encodeToString());
                manager.commit();

            }
        });

    }

    /**
     * Method DriveFileExists runs a query on Google Drive to find if the folder Pocket Wallet exists. If it already exists then
     * it takes the id of this Folder and save it to the Shared Preferences of the app. Otherwise if it doesn't exist then it calls the
     * method createFileToDrive to create again the folder.
     */
    public void DriveFileExists(){


        CustomPropertyKey key=new CustomPropertyKey("pocket_folder",CustomPropertyKey.PRIVATE);
        Query query=new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket_folder"), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(client,query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                if(!metadataBufferResult.getStatus().isSuccess()){
                    Log.i("File","No contents with this query");
                    return;
                }

                MetadataBuffer mbuffer=metadataBufferResult.getMetadataBuffer();
                if(mbuffer.getCount() > 0){
                    Metadata metadata=mbuffer.get(0);
                    DriveId driveId=metadata.getDriveId();
                    SharedPrefsManager manager = new SharedPrefsManager(context);
                    manager.startEditing();
                    manager.setPrefsDriverFolderId(driveId.encodeToString());
                    manager.commit();
                    Log.i("AlreadyExists", driveId.encodeToString());

                }else{
                    createFileToDrive();
                    Log.i("Create","created");
                }

            }
        });



    }

    /**
     * Method updateGoogleDriveContent checks if there is a backup on Google Drive. If it is then it deletes the old one and creates
     * the new backup. Otherwise it just creates the backup by calling the methods saveDBToDrive and saveCategoriesDBToDrive.
     */
    public void updateGoogleDriveContent(){

        CustomPropertyKey key=new CustomPropertyKey("pocket_money",CustomPropertyKey.PRIVATE);
        Query query=new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket_money"), Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite"), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                if(!metadataBufferResult.getStatus().isSuccess()){
                    Log.i("File","No contents with this query");
                    return;
                }

                MetadataBuffer mbuffer=metadataBufferResult.getMetadataBuffer();
                if(mbuffer.getCount() > 0){
                    Log.i("Query","Success");
                    Metadata metadata=mbuffer.get(0);
                    String fileID=metadata.getDriveId().encodeToString();
                    Drive.DriveApi.getFile(client,DriveId.decodeFromString(fileID)).delete(client);
                    saveDBToDrive();

                }else{
                    saveDBToDrive();
                    Log.i("Query", "fail");
                }

            }
        });

         key=new CustomPropertyKey("pocket_categories",CustomPropertyKey.PRIVATE);
         query=new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket_categories"), Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite"), Filters.eq(SearchableField.TRASHED, false))).build();
         Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                if(!metadataBufferResult.getStatus().isSuccess()){
                    Log.i("File","No contents with this query");
                    return;
                }

                MetadataBuffer mbuffer=metadataBufferResult.getMetadataBuffer();
                if(mbuffer.getCount() > 0){
                    Log.i("Query","Success");
                    Metadata metadata=mbuffer.get(0);
                    String fileID=metadata.getDriveId().encodeToString();
                    Drive.DriveApi.getFile(client,DriveId.decodeFromString(fileID)).delete(client);
                    saveCategoriesDBToDrive();

                }else{
                    saveCategoriesDBToDrive();
                    Log.i("Query", "fail");
                }

            }
        });



    }

    /**
     * Method saveDBToDrive creates a backup of Money Database to Google Drive.
     */
    public void saveDBToDrive(){


        Drive.DriveApi.newDriveContents(client).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i("Fail", "Fail to create new contents");
                }


                String MoneyDBpath = "/data/" + context.getPackageName() + "/databases/MoneyDatabase";


                try {
                    InputStream input = new FileInputStream(Environment.getDataDirectory() + MoneyDBpath);
                    OutputStream output = driveContentsResult.getDriveContents().getOutputStream();


                    byte[] buffer = new byte[1024];
                    int size;
                    while ((size = input.read(buffer)) > 0) {
                        output.write(buffer, 0, size);
                    }

                    output.flush();
                    output.close();
                    input.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                CustomPropertyKey key=new CustomPropertyKey("pocket_money",CustomPropertyKey.PRIVATE);
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/x-sqlite").setTitle("Pocket-Wallet Transactions backup").setCustomProperty(key, "pocket_money").build();

                SharedPrefsManager manager = new SharedPrefsManager(context);
                String folderID = manager.getPrefsDriverFolderId();

                DriveId id = DriveId.decodeFromString(folderID);


                IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(driveContentsResult.getDriveContents()).setActivityStartFolder(id).build(client);

                try {

                    activity.startIntentSenderForResult(intentSender, TRANSACTIONS_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    /**
     * Method saveCategoriesDBToDrive creates a backup of Categories Database to Google Drive.
     */
    public void saveCategoriesDBToDrive(){
        Drive.DriveApi.newDriveContents(client).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i("Fail", "Fail to create new contents");
                }


                String CategoryDBpath = "/data/" + context.getPackageName() + "/databases/categories";


                try {
                    InputStream input = new FileInputStream(Environment.getDataDirectory() + CategoryDBpath);
                    OutputStream output = driveContentsResult.getDriveContents().getOutputStream();


                    byte[] buffer = new byte[1024];
                    int size;
                    while ((size = input.read(buffer)) > 0) {
                        output.write(buffer, 0, size);
                    }

                    output.flush();
                    output.close();
                    input.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                CustomPropertyKey key=new CustomPropertyKey("pocket_categories",CustomPropertyKey.PRIVATE);
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/x-sqlite").setTitle("Pocket-Wallet Categories backup").setCustomProperty(key, "pocket_categories").build();

                SharedPrefsManager manager = new SharedPrefsManager(context);
                String folderID = manager.getPrefsDriverFolderId();

                DriveId id = DriveId.decodeFromString(folderID);


                IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(driveContentsResult.getDriveContents()).setActivityStartFolder(id).build(client);
                //intentSen
                try {
                    //startIntentSender(intentSender, null, 0, 0, 0);
                    activity.startIntentSenderForResult(intentSender, CATEGORIES_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });


    }




    public void saveIDOfTransactionsDriveFile(){

        CustomPropertyKey key=new CustomPropertyKey("pocket_money",CustomPropertyKey.PRIVATE);
        Query query=new Query.Builder().addFilter(Filters.and(Filters.eq(key,"pocket_money") , Filters.eq(SearchableField.MIME_TYPE,"application/x-sqlite"))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {

                if(!metadataBufferResult.getStatus().isSuccess()){
                    Log.i("File","No contents with this query");
                    return;
                }
                SharedPrefsManager manager=new SharedPrefsManager(context);

                if(metadataBufferResult.getMetadataBuffer().getCount()>0){
                    Metadata metadata=metadataBufferResult.getMetadataBuffer().get(0);
                    String fileID= metadata.getDriveId().encodeToString();

                    Log.i("FileID",fileID);


                    manager.startEditing();
                    manager.setPrefsTransactionsDriverFileId(fileID);
                    manager.commit();
                    Log.i("InsideMoneyID", manager.getPrefsTransactionsDriverFileId());
                }else{
                    manager.startEditing();
                    manager.setPrefsTransactionsDriverFileId("-1");
                    manager.commit();
                }


            }
        });
    }

    public void saveIDOfCategoriesDriveFile(){

        CustomPropertyKey key=new CustomPropertyKey("pocket_categories",CustomPropertyKey.PRIVATE);
        Query query=new Query.Builder().addFilter(Filters.and(Filters.eq(key,"pocket_categories") , Filters.eq(SearchableField.MIME_TYPE,"application/x-sqlite"))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                //metadataBufferResult.getMetadataBuffer().
                if(!metadataBufferResult.getStatus().isSuccess()){
                    Log.i("File","No contents with this query");
                    return;
                }

                SharedPrefsManager manager=new SharedPrefsManager(context);

                if(metadataBufferResult.getMetadataBuffer().getCount()>0){
                    Metadata metadata=metadataBufferResult.getMetadataBuffer().get(0);
                    String fileID= metadata.getDriveId().encodeToString();

                    Log.i("FileID",fileID);


                    manager.startEditing();
                    manager.setPrefsCategoriesDriverFileId(fileID);
                    manager.commit();
                }else{
                    manager.startEditing();
                    manager.setPrefsCategoriesDriverFileId("-1");
                    manager.commit();
                }


            }
        });
    }

    /**
     * Method restoreMoneyDBFileFromDrive restores from Google Drive the backup of Money Database.
     */
    public void restoreMoneyDBFileFromDrive(){

        SharedPrefsManager manager=new SharedPrefsManager(context);
        String fileID=manager.getPrefsTransactionsDriverFileId();
        DriveId id=DriveId.decodeFromString(fileID);
        DriveFile file=Drive.DriveApi.getFile(client, id);

        String CategoriesDBpath = "/data/" + context.getPackageName() + "/databases/MoneyDatabase";

        DriveApi.DriveContentsResult contentsResult=file.open(client,DriveFile.MODE_READ_ONLY,null).await();
        if(!contentsResult.getStatus().isSuccess()){
            Log.i("File","Did not open");
        }

        DriveContents driveContents= contentsResult.getDriveContents();
        InputStream input=driveContents.getInputStream();
        OutputStream output;
        try {
            output=new FileOutputStream(Environment.getDataDirectory()+CategoriesDBpath);

            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) > 0) {
                output.write(buffer, 0, size);
            }

            output.flush();
            output.close();
            input.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Method restoreCategoriesDBFileFromDrive restores from Google Drive the backup of Categories Database.
     */
    public void restoreCategoriesDBFileFromDrive(){


        SharedPrefsManager manager=new SharedPrefsManager(context);
        String fileID=manager.getPrefsCategoriesDriverFileId();
        DriveId id=DriveId.decodeFromString(fileID);
        DriveFile file=Drive.DriveApi.getFile(client, id);

        String CategoriesDBpath = "/data/" + context.getPackageName() + "/databases/categories";

        DriveApi.DriveContentsResult contentsResult=file.open(client,DriveFile.MODE_READ_ONLY,null).await();
        if(!contentsResult.getStatus().isSuccess()){
            Log.i("File","Did not open");
        }

        DriveContents driveContents= contentsResult.getDriveContents();
        InputStream input=driveContents.getInputStream();
        OutputStream output;
        try {
            output=new FileOutputStream(Environment.getDataDirectory()+CategoriesDBpath);

            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) > 0) {
                output.write(buffer, 0, size);
            }

            output.flush();
            output.close();
            input.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    /**
     * Inner class Restore which extends a thread on Android used to perform the restore from the Google Drive.
     */
    class Restore extends AsyncTask<Void,Void,Void> {

        boolean restore;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            saveIDOfTransactionsDriveFile();
            saveIDOfCategoriesDriveFile();

        }

        @Override
        protected Void doInBackground(Void... params) {

            //Because it takes some time to connect to Drive this thread waits 1 sec for the app to connect, to retrieve
            //the right values.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SharedPrefsManager manager = new SharedPrefsManager(context);
         //   Log.i("MoneyID",manager.getPrefsTransactionsDriverFileId());
            if(manager.getPrefsTransactionsDriverFileId().equals("-1")){
                restore=false;
            }else {
                restoreMoneyDBFileFromDrive();
                restoreCategoriesDBFileFromDrive();
                restore =true;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(restore){
                Toast.makeText(context,"Restore performed successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"No backup found in Google Drive",Toast.LENGTH_LONG).show();
            }
        }
    }


}
