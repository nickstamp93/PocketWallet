package com.ngngteam.pocketwallet.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;


import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;

import jxl.format.Alignment;
import jxl.write.*;


/**
 * Created by Nick Zisis on 07-Oct-15.
 */
public class ExportExcel {

    private MoneyDatabase db;
    private Cursor expenseCursor, incomeCursor;
    private WorkbookSettings wbSettings;
    private WritableWorkbook workbook;


    private String filename;
    private String currency;
    private final int REQUEST_CODE_RESOLUTION = 1;
    private final int EXPORT_CODE = 4;

    private Context context;
    private DropboxAPI<AndroidAuthSession> api;
    private SettingsActivity activity;
    private GoogleApiClient client;

    private ProgressDialog progressDialog;
    private TextView tvCommand;





    public ExportExcel(String filename, Context context) {
        this.filename = filename;
        this.context = context;

        init();
    }

    public ExportExcel(String filename, Context context, DropboxAPI<AndroidAuthSession> api, SettingsActivity.ExportDialog dialog) {
        this.filename = filename;
        this.context = context;
        this.api = api;

        init();

        dialog.dismiss();

        exportToDropBox();

    }


    public ExportExcel(String filename, Context context, SettingsActivity act) {
        this.filename = filename;
        this.context = context;
        this.activity = act;

        init();

        progressDialog = new ProgressDialog(act);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_bar);
        tvCommand = (TextView) progressDialog.findViewById(R.id.tvCommand);
        tvCommand.setText(context.getResources().getString(R.string.connecting));


        client = new GoogleApiClient.Builder(context).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                //Log.i("Connected", "Client has connected");

                DriveFileExists();
                updateGoogleDriveContent();


            }

            @Override
            public void onConnectionSuspended(int i) {
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

        client.connect();


    }

    public void createFileToDrive() {


        CustomPropertyKey key = new CustomPropertyKey("pocket_folder", CustomPropertyKey.PRIVATE);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("Pocket-Wallet").setCustomProperty(key, "pocket_folder").build();

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
    public void DriveFileExists() {


        CustomPropertyKey key = new CustomPropertyKey("pocket_folder", CustomPropertyKey.PRIVATE);
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket_folder"), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()) {
                    Log.i("File", "No contents with this query");
                    return;
                }

                MetadataBuffer mbuffer = metadataBufferResult.getMetadataBuffer();
                if (mbuffer.getCount() > 0) {
                    Metadata metadata = mbuffer.get(0);
                    DriveId driveId = metadata.getDriveId();
                    SharedPrefsManager manager = new SharedPrefsManager(context);
                    manager.startEditing();
                    manager.setPrefsDriverFolderId(driveId.encodeToString());
                    manager.commit();
                    Log.i("AlreadyExists", driveId.encodeToString());

                } else {
                    createFileToDrive();
                    Log.i("Create", "created");
                }

            }
        });

    }


    /**
     * Method updateGoogleDriveContent checks if there is a backup on Google Drive. If it is then it deletes the old one and creates
     * the new backup. Otherwise it just creates the backup by calling the methods saveDBToDrive and saveCategoriesDBToDrive.
     */
    public void updateGoogleDriveContent() {

        CustomPropertyKey key = new CustomPropertyKey("pocket_export", CustomPropertyKey.PRIVATE);
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket_export"), Filters.eq(SearchableField.MIME_TYPE, "application/vnd.ms-excel"), Filters.eq(SearchableField.TRASHED, false))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                if (!metadataBufferResult.getStatus().isSuccess()) {
                    Log.i("File", "No contents with this query");
                    return;
                }

                MetadataBuffer mbuffer = metadataBufferResult.getMetadataBuffer();
                if (mbuffer.getCount() > 0) {
                    Log.i("Query", "Success");
                    Metadata metadata = mbuffer.get(0);
                    String fileID = metadata.getDriveId().encodeToString();
                    Drive.DriveApi.getFile(client, DriveId.decodeFromString(fileID)).delete(client);
                    exportExcelToDrive();

                } else {
                    exportExcelToDrive();
                    Log.i("Query", "fail");
                }
                progressDialog.dismiss();

            }
        });


    }


    private void init() {
        db = new MoneyDatabase(context);
        expenseCursor = db.getAllExpenses();
        incomeCursor = db.getAllIncomes();

        currency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.pref_key_currency), context.getResources().getString(R.string.pref_currency_default_value));

    }

    public void exportExcelToSD() {

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()+"/Pocket-Wallet");

        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        File file = new File(directory, filename);
        writeExcel(file);

    }


    public void exportToDropBox() {
        new UploadExcelToDropBox().execute();
    }


    private void exportExcelToDrive() {


        Drive.DriveApi.newDriveContents(client).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i("Fail", "Fail to create new contents");
                }

                OutputStream output = driveContentsResult.getDriveContents().getOutputStream();
                writeExcel(output);


                CustomPropertyKey key = new CustomPropertyKey("pocket_export", CustomPropertyKey.PRIVATE);
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/vnd.ms-excel").setTitle("Pocket-Wallet export").setCustomProperty(key, "pocket_export").build();

                SharedPrefsManager manager = new SharedPrefsManager(context);
                String folderID = manager.getPrefsDriverFolderId();

                DriveId id = DriveId.decodeFromString(folderID);


                IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(driveContentsResult.getDriveContents()).setActivityStartFolder(id).build(client);

                try {

                    activity.startIntentSenderForResult(intentSender, EXPORT_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void writeExcel(File dir) {

        wbSettings = new WorkbookSettings();
        if (Locale.getDefault().getDisplayLanguage().equals("Ελληνικά")) {
            wbSettings.setLocale(new Locale("el", "EL"));
        } else {
            wbSettings.setLocale(new Locale("en", "EN"));
        }

        try {

            workbook = Workbook.createWorkbook(dir, wbSettings);

            WritableSheet expenseSheet = workbook.createSheet("Expenses", 0);
            WritableSheet incomeSheet = workbook.createSheet("Incomes", 1);

            try {

                //Sheet Expenses
                WritableFont times16font = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
                WritableCellFormat times16format = new WritableCellFormat(times16font);
                times16format.setAlignment(Alignment.CENTRE);

                WritableFont times13font = new WritableFont(WritableFont.TIMES, 13);
                WritableCellFormat times13format = new WritableCellFormat(times13font);
                times13format.setAlignment(Alignment.CENTRE);


                expenseSheet.addCell(new Label(0, 0, "Amount", times16format));
                expenseSheet.addCell(new Label(1, 0, "Category", times16format));
                expenseSheet.addCell(new Label(2, 0, "Date", times16format));
                expenseSheet.addCell(new Label(3, 0, "Notes", times16format));

                for (int i = 0; i < 4; i++) {
                    CellView cv = expenseSheet.getColumnView(i);
                    cv.setAutosize(true);
                    expenseSheet.setColumnView(i, cv);
                }


                int counter = 1;

                for (expenseCursor.moveToFirst(); !expenseCursor.isAfterLast(); expenseCursor.moveToNext()) {

                    String category = expenseCursor.getString(1);
                    String date = expenseCursor.getString(2);
                    String tokens[] = date.split("-");
                    String reformedDate = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

                    double amount = Double.parseDouble(expenseCursor.getString(3));
                    String notes = expenseCursor.getString(4);

                    expenseSheet.addCell(new Label(0, counter, amount + currency, times13format));
                    expenseSheet.addCell(new Label(1, counter, category, times13format));
                    expenseSheet.addCell(new Label(2, counter, reformedDate, times13format));
                    expenseSheet.addCell(new Label(3, counter, notes, times13format));

                    counter++;

                }


                //Sheet Incomes
                incomeSheet.addCell(new Label(0, 0, "Amount", times16format));
                incomeSheet.addCell(new Label(1, 0, "Category", times16format));
                incomeSheet.addCell(new Label(2, 0, "Date", times16format));
                incomeSheet.addCell(new Label(3, 0, "Notes", times16format));

                for (int i = 0; i < 4; i++) {
                    CellView cv = incomeSheet.getColumnView(i);
                    cv.setAutosize(true);
                    incomeSheet.setColumnView(i, cv);
                }


                counter = 1;

                for (incomeCursor.moveToFirst(); !incomeCursor.isAfterLast(); incomeCursor.moveToNext()) {

                    String category = incomeCursor.getString(2);
                    String date = incomeCursor.getString(3);
                    String tokens[] = date.split("-");
                    String reformedDate = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

                    double amount = Double.parseDouble(incomeCursor.getString(1));
                    String notes = incomeCursor.getString(4);

                    incomeSheet.addCell(new Label(0, counter, amount + currency, times13format));
                    incomeSheet.addCell(new Label(1, counter, category, times13format));
                    incomeSheet.addCell(new Label(2, counter, reformedDate, times13format));
                    incomeSheet.addCell(new Label(3, counter, notes, times13format));

                    counter++;

                }


            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void writeExcel(OutputStream output){
        wbSettings = new WorkbookSettings();
        if (Locale.getDefault().getDisplayLanguage().equals("Ελληνικά")) {
            wbSettings.setLocale(new Locale("el", "EL"));
        } else {
            wbSettings.setLocale(new Locale("en", "EN"));
        }

        try {

            workbook = Workbook.createWorkbook(output, wbSettings);

            WritableSheet expenseSheet = workbook.createSheet("Expenses", 0);
            WritableSheet incomeSheet = workbook.createSheet("Incomes", 1);

            try {

                //Sheet Expenses
                WritableFont times16font = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
                WritableCellFormat times16format = new WritableCellFormat(times16font);
                times16format.setAlignment(Alignment.CENTRE);

                WritableFont times13font = new WritableFont(WritableFont.TIMES, 13);
                WritableCellFormat times13format = new WritableCellFormat(times13font);
                times13format.setAlignment(Alignment.CENTRE);


                expenseSheet.addCell(new Label(0, 0, "Amount", times16format));
                expenseSheet.addCell(new Label(1, 0, "Category", times16format));
                expenseSheet.addCell(new Label(2, 0, "Date", times16format));
                expenseSheet.addCell(new Label(3, 0, "Notes", times16format));

                for (int i = 0; i < 4; i++) {
                    CellView cv = expenseSheet.getColumnView(i);
                    cv.setAutosize(true);
                    expenseSheet.setColumnView(i, cv);
                }


                int counter = 1;

                for (expenseCursor.moveToFirst(); !expenseCursor.isAfterLast(); expenseCursor.moveToNext()) {

                    String category = expenseCursor.getString(1);
                    String date = expenseCursor.getString(2);
                    String tokens[] = date.split("-");
                    String reformedDate = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

                    double amount = Double.parseDouble(expenseCursor.getString(3));
                    String notes = expenseCursor.getString(4);

                    expenseSheet.addCell(new Label(0, counter, amount + currency, times13format));
                    expenseSheet.addCell(new Label(1, counter, category, times13format));
                    expenseSheet.addCell(new Label(2, counter, reformedDate, times13format));
                    expenseSheet.addCell(new Label(3, counter, notes, times13format));

                    counter++;

                }


                //Sheet Incomes
                incomeSheet.addCell(new Label(0, 0, "Amount", times16format));
                incomeSheet.addCell(new Label(1, 0, "Category", times16format));
                incomeSheet.addCell(new Label(2, 0, "Date", times16format));
                incomeSheet.addCell(new Label(3, 0, "Notes", times16format));

                for (int i = 0; i < 4; i++) {
                    CellView cv = incomeSheet.getColumnView(i);
                    cv.setAutosize(true);
                    incomeSheet.setColumnView(i, cv);
                }


                counter = 1;

                for (incomeCursor.moveToFirst(); !incomeCursor.isAfterLast(); incomeCursor.moveToNext()) {

                    String category = incomeCursor.getString(2);
                    String date = incomeCursor.getString(3);
                    String tokens[] = date.split("-");
                    String reformedDate = tokens[2] + "-" + tokens[1] + "-" + tokens[0];

                    double amount = Double.parseDouble(incomeCursor.getString(1));
                    String notes = incomeCursor.getString(4);

                    incomeSheet.addCell(new Label(0, counter, amount + currency, times13format));
                    incomeSheet.addCell(new Label(1, counter, category, times13format));
                    incomeSheet.addCell(new Label(2, counter, reformedDate, times13format));
                    incomeSheet.addCell(new Label(3, counter, notes, times13format));

                    counter++;

                }


            } catch (WriteException e) {
                e.printStackTrace();
            }

            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class UploadExcelToDropBox extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.custom_progress_bar);
            tvCommand = (TextView) progressDialog.findViewById(R.id.tvCommand);
            tvCommand.setText(context.getResources().getString(R.string.uploading));
        }

        @Override
        protected String doInBackground(String... params) {

            exportExcelToSD();
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard.getAbsolutePath() + "/Pocket-Wallet/" + filename);

            try {
                InputStream inputStream = new FileInputStream(file);

                DropboxAPI.Entry metadata = api.metadata("/", 1000, null, true, null);


                boolean flag = false;
                List<DropboxAPI.Entry> CFolder = metadata.contents;

                for (DropboxAPI.Entry entry : CFolder) {
                    if (entry.fileName().equals("PocketWalletExport.xls")) {
                        flag = true;
                        break;
                    }


                }

                if (!flag) {
                    api.putFile("PocketWalletExport.xls", inputStream, file.length(), null, null);

                } else {
                    api.delete("/PocketWalletExport.xls");
                    api.putFile("PocketWalletExport.xls", inputStream, file.length(), null, null);

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DropboxException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            api.getSession().unlink();
            Toast.makeText(context, context.getResources().getString(R.string.dropbox_export), Toast.LENGTH_LONG).show();
        }
    }


}
