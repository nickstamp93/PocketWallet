package com.ngngteam.pocketwallet.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
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
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


import com.ngngteam.pocketwallet.Adapters.GridAdapter;
import com.ngngteam.pocketwallet.BroadcastReceivers.ReminderReceiver;
import com.ngngteam.pocketwallet.Model.GridItem;
import com.ngngteam.pocketwallet.Utils.BackupDropbox;
import com.ngngteam.pocketwallet.Utils.BackupRestoreDrive;
import com.ngngteam.pocketwallet.Utils.BackupRestoreSD;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;

import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerDialog;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerSwatch;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    private final int REQUEST_CODE_RESOLUTION = 1;
    private final int TRANSACTIONS_CODE=2;
    private final int CATEGORIES_CODE=3;
    final static private String APP_KEY = "slba7p9039i59nw";
    final static private String APP_SECRET = "srq6a16ada8u517";


    private GoogleApiClient client;
    private BackupRestoreDrive drive;
    private DropboxAPI<AndroidAuthSession> api;

    private boolean backup;

    private BackupRestoreDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        //init preference screen from xml file
        addPreferencesFromResource(R.xml.preferences);

        //init the summaries for the screens
        initSummaries(getPreferenceScreen());

        setPreferenceActions();

    }


    class Restore extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            restoreDBFileFromDrive();

            return null;
        }
    }


    //set all the preferences and their actions
    private void setPreferenceActions() {

        if (PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).
                getString(getString(R.string.pref_key_pattern), "1234").equals("1234")) {
            disablePassword();
        }

        //when user clicks on "profile" preference item
        //launch intent with target the UserDetailsActivity class
        Preference screen = findPreference(getResources().getString(R.string.pref_key_profile));
        Intent i = new Intent(this, UserDetailsActivity.class);
        screen.setIntent(i);

        //when user clicks on "reminder time" preference item
        //start TransparentActivity which contains the RadialTimeDialog
        //doing it this way because the RadialTimePickerDialog must have FragmentActivity as a parent
        //and this activity is a PreferenceActivity
        screen = findPreference(getResources().getString(R.string.pref_key_reminder_time));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SettingsActivity.this, TransparentActivity.class));
                return false;
            }
        });

        //when user clicks on "pattern lock" preference item
        //launch intent with target the PatternLockActivity class
        screen = findPreference(getResources().getString(R.string.pref_key_pattern));
        i = new Intent(this, PatternLockActivity.class).putExtra("mode", "edit");
        screen.setIntent(i);

        //when user clicks on "about" preference item
        //launch an alert dialog with the aboout text
        screen = findPreference(getResources().getString(R.string.pref_key_about));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(getResources().getString(R.string.text_about))
                        .setTitle(getResources().getString(R.string.dialog_title_about));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });

        //when user clicks on "version" preference item
        //launch intent with target the VersionActivity class
        screen = findPreference(getResources().getString(R.string.pref_key_version));
        i = new Intent(this, VersionActivity.class);
        screen.setIntent(i);

        //when user clicks on "rate app" preference item
        //launch the market with the app's page
        screen = findPreference(getResources().getString(R.string.pref_key_rate_app));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

                return false;
            }
        });

        //when user clicks on "delete income" preference item
        //ask for confirmation and delete the income data records
        screen = findPreference(getResources().getString(R.string.pref_key_delete_income));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setMessage(getResources().getString(R.string.dialog_text_delete_income_confirm));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete income method from database
                        MoneyDatabase db = new MoneyDatabase(getApplicationContext());
                        db.deleteAllIncome();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });


        //when user clicks on "delete expense" preference item
        //ask for confirmation and delete the expense data records
        screen = findPreference(getResources().getString(R.string.pref_key_delete_expense));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setMessage(getResources().getString(R.string.dialog_text_delete_expense_confirm))
                        .setTitle(getResources().getString(R.string.dialog_title_caution));
                builder.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete expense method from database

                        MoneyDatabase db = new MoneyDatabase(SettingsActivity.this);
                        db.deleteAllExpense();
                        db.close();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        //when user clicks on "backup" preference item
        //call an async task that performs the backup
        screen = findPreference(getResources().getString(R.string.pref_key_backup));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //TODO call the async task with the backup procedure

/*                   SD Card code
                String MoneyDBpath="/data/"+getPackageName()+"/databases/MoneyDatabase";
                String MoneyOutputName="/MoneyDatabase";
                BackupRestoreSD backupRestoreSD =new BackupRestoreSD(MoneyDBpath,MoneyOutputName,SettingsActivity.this);
                boolean MoneySuccess= backupRestoreSD.backup();

                String CategoriesDBpath="/data/"+getPackageName()+"/databases/categories";
                String CategoriesOutputName="/CategoryDatabase";


               backupRestoreSD =new BackupRestoreSD(CategoriesDBpath,CategoriesOutputName,SettingsActivity.this);
                boolean CategorySuccess= backupRestoreSD.backup();


                if(MoneySuccess && CategorySuccess){
                    Toast.makeText(SettingsActivity.this, "Backup Done Succesfully!", Toast.LENGTH_LONG)
                            .show();

                }*/


/*
                   Google Drive code

                  client =new GoogleApiClient.Builder(SettingsActivity.this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i("Connected","Client has connected");

                        SharedPrefsManager manager=new SharedPrefsManager(SettingsActivity.this);
                        String folderID=manager.getPrefsDriverFolderId();
                        if(folderID.equals("-1")){
                            createFileToDrive();
                        }else{
                            GoogleDriveFolderExists();
                        }
                        FileAlreadyExists();


                        saveDBToDrive();
                        saveIDOfDriveFile();
                        restoreDBFileFromDrive();
                        new Restore().execute();


                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("Suspended","Client has suspended");
                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        if(connectionResult.hasResolution()){
                            try {
                                connectionResult.startResolutionForResult(SettingsActivity.this,REQUEST_CODE_RESOLUTION);
                            }catch (IntentSender.SendIntentException e){
                                e.printStackTrace();
                            }
                        }else{
                            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), SettingsActivity.this, 0).show();
                                                   }
                    }
                }).build();


                client.connect();

*/
                backup=true;
                dialog = new BackupRestoreDialog().newInstance(backup);
                dialog.show(getFragmentManager(), "dialog");


                return false;
            }
        });


        //when user clicks on "backup" preference item
        //call an async task that performs the backup
        screen = findPreference(getResources().getString(R.string.pref_key_restore));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //TODO call the async task with the restore procedure

//                String MoneyDBpath="/data/"+getPackageName()+"/databases/MoneyDatabase";
//                String MoneyOutputName="/MoneyDatabase";
//                BackupRestoreSD backupRestoreSD =new BackupRestoreSD(MoneyDBpath,MoneyOutputName,SettingsActivity.this);
//                boolean MoneySuccess= backupRestoreSD.restore();
//
//                String CategoriesDBpath="/data/"+getPackageName()+"/databases/categories";
//                String CategoriesOutputName="/CategoryDatabase";
//                backupRestoreSD =new BackupRestoreSD(CategoriesDBpath,CategoriesOutputName,SettingsActivity.this);
//                boolean CategorySuccess= backupRestoreSD.restore();
//
//                if(MoneySuccess && CategorySuccess){
//                    Toast.makeText(SettingsActivity.this, "Restore Done Succesfully!", Toast.LENGTH_LONG)
//                            .show();
//
//                }
                backup=false;
                dialog = new BackupRestoreDialog().newInstance(false);
                dialog.show(getFragmentManager(), "dialog");


                return false;
            }
        });


        //when the user clicks on the "theme" preference item
        screen = findPreference(getResources().getString(R.string.pref_key_theme));
        screen.setDefaultValue(getResources().getColor(R.color.background_material_dark));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                int[] mColor = new int[]{
                        getResources().getColor(R.color.background_material_dark),
                        getResources().getColor(R.color.background_material_light)
                };
                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, mColor, 0, 2, ColorPickerDialog.SIZE_LARGE);
                dialog.setSelectedColor(prefs.getInt(getResources().getString(R.string.pref_key_theme), getResources().getColor(R.color.background_material_light)));
                dialog.setOnColorSelectedListener(colorSetListener);
                dialog.show(getFragmentManager(), "color");
                return false;
            }
        });
    }


    private ColorPickerSwatch.OnColorSelectedListener colorSetListener = new ColorPickerSwatch.OnColorSelectedListener() {
        @Override
        public void onColorSelected(int color) {
            //when color is selected
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
            SharedPreferences.Editor editor = prefs.edit();

            //save the new theme color
            editor.putInt(getResources().getString(R.string.pref_key_theme), color);
            editor.commit();

            //set the theme changed variable to true
            SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
            manager.startEditing();
            manager.setPrefsThemeChanged(true);
            manager.commit();

            //and then destroy the dialog
            Intent i = getIntent();
            overridePendingTransition(0, 0);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            drive.connect();
        } else if (requestCode == TRANSACTIONS_CODE && resultCode == RESULT_OK) {
            Log.i("DB", "db successfully uploaded");
            drive.saveIDOfTransactionsDriveFile();
        }else if(requestCode == CATEGORIES_CODE && resultCode == RESULT_OK){
            drive.saveIDOfCategoriesDriveFile();
        }
    }


//    public void FileAlreadyExists() {
//
//        CustomPropertyKey key = new CustomPropertyKey("pocket", CustomPropertyKey.PRIVATE);
//        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket"), Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite"), Filters.eq(SearchableField.TRASHED, false))).build();
//        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//            @Override
//            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
//                if (!metadataBufferResult.getStatus().isSuccess()) {
//                    Log.i("File", "No contents with this query");
//                    return;
//                }
//
//                MetadataBuffer mbuffer = metadataBufferResult.getMetadataBuffer();
//                if (mbuffer.getCount() > 0) {
//                    Log.i("Query", "Success");
//                    Metadata metadata = mbuffer.get(0);
//                    String fileID = metadata.getDriveId().encodeToString();
//                    Drive.DriveApi.getFile(client, DriveId.decodeFromString(fileID)).delete(client);
//                    saveDBToDrive();
//
//                } else {
//                    saveDBToDrive();
//                    Log.i("Query", "fail");
//                }
//
//            }
//        });
//
//
//    }


    public void saveIDOfDriveFile() {

        CustomPropertyKey key = new CustomPropertyKey("pocket", CustomPropertyKey.PRIVATE);
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(key, "pocket"), Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite"))).build();
        Drive.DriveApi.query(client, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                //metadataBufferResult.getMetadataBuffer().
                if (!metadataBufferResult.getStatus().isSuccess()) {
                    Log.i("File", "No contents with this query");
                    return;
                }


                Metadata metadata = metadataBufferResult.getMetadataBuffer().get(0);
                String fileID = metadata.getDriveId().encodeToString();

                Log.i("FileID", fileID);

                SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
                manager.startEditing();
                manager.setPrefsTransactionsDriverFileId(fileID);
                manager.commit();

            }
        });
    }

    public void GoogleDriveFolderExists() {

        SharedPrefsManager manager = new SharedPrefsManager(this);
        String folderID = manager.getPrefsDriverFolderId();

        DriveId id = DriveId.decodeFromString(folderID);
        DriveFolder folder = Drive.DriveApi.getFolder(client, id);
        folder.getMetadata(client).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
            @Override
            public void onResult(DriveResource.MetadataResult metadataResult) {
                if (!metadataResult.getStatus().isSuccess()) {
                    Log.i("Folder", "Folder doesnt exist");
                    return;
                }

                Log.i("Folder", "Exists");


                Metadata metadata = metadataResult.getMetadata();
                if (metadata.isTrashed() || metadata.isExplicitlyTrashed()) {
                    createFileToDrive();
                    Log.i("Folder", "Folder is trashed");
                }


            }
        });


    }

    public void createFileToDrive() {


        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("Pocket-Wallet").build();

        Drive.DriveApi.getRootFolder(client).createFolder(client, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if (!driveFolderResult.getStatus().isSuccess()) {
                    Log.i("Folder", "Error while trying to create the folder");
                    return;
                }
                Log.i("Folder", "Folder created " + driveFolderResult.getDriveFolder().getDriveId());
                SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
                manager.startEditing();
                manager.setPrefsDriverFolderId(driveFolderResult.getDriveFolder().getDriveId().encodeToString());
                manager.commit();

            }
        });

    }


//    public void saveeDBToDrive() {
//
//
//        Drive.DriveApi.newDriveContents(client).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//            @Override
//            public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
//
//                if (!driveContentsResult.getStatus().isSuccess()) {
//                    Log.i("Fail", "Fail to create new contents");
//                }
//
//
//                String MoneyDBpath = "/data/" + getPackageName() + "/databases/MoneyDatabase";
//
//
//                try {
//                    InputStream input = new FileInputStream(Environment.getDataDirectory() + MoneyDBpath);
//                    OutputStream output = driveContentsResult.getDriveContents().getOutputStream();
//
//
//                    byte[] buffer = new byte[1024];
//                    int size;
//                    while ((size = input.read(buffer)) > 0) {
//                        output.write(buffer, 0, size);
//                    }
//
//                    output.flush();
//                    output.close();
//                    input.close();
//
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                CustomPropertyKey key = new CustomPropertyKey("pocket", CustomPropertyKey.PRIVATE);
//                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/x-sqlite").setTitle("Pocket-Wallet backup").setCustomProperty(key, "pocket").build();
//
//                SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
//                String folderID = manager.getPrefsDriverFolderId();
//
//                DriveId id = DriveId.decodeFromString(folderID);
//
//
//                IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(driveContentsResult.getDriveContents()).setActivityStartFolder(id).build(client);
//                //intentSen
//                try {
//                    //startIntentSender(intentSender, null, 0, 0, 0);
//                    startIntentSenderForResult(intentSender, 2, null, 0, 0, 0);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//
//    }

    public void restoreDBFileFromDrive() {


        SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
        String fileID = manager.getPrefsTransactionsDriverFileId();
        DriveId id = DriveId.decodeFromString(fileID);
        DriveFile file = Drive.DriveApi.getFile(client, id);

        String MoneyDBpath = "/data/" + getPackageName() + "/databases/MoneyDatabase";

        DriveApi.DriveContentsResult contentsResult = file.open(client, DriveFile.MODE_READ_ONLY, null).await();
        if (!contentsResult.getStatus().isSuccess()) {
            Log.i("File", "Did not open");
        }

        DriveContents driveContents = contentsResult.getDriveContents();
        InputStream input = driveContents.getInputStream();
        OutputStream output;
        try {
            output = new FileOutputStream(Environment.getDataDirectory() + MoneyDBpath);

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


    }


    @Override
    protected void onResume() {
        super.onResume();
        //on resume , update the summaries , and  any preferences launched
        initSummaries(getPreferenceScreen());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if(api!=null) {
            if (api.getSession().authenticationSuccessful() && backup) {

                Log.i("Dropbox","Authentication success");
                try {
                    // Required to complete auth, sets the access token on the session
                    api.getSession().finishAuthentication();

                    String accessToken = api.getSession().getOAuth2AccessToken();

                    // new Upload().execute();
                    new BackupDropbox(api, SettingsActivity.this,dialog).execute();

                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(getResources().getString(R.string.pref_key_password))) {

            //if the pass just been enabled
            if (PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean(getResources().getString(R.string.pref_key_password), false)) {


                //launch pattern activity to enter new pattern
                Intent i = new Intent(SettingsActivity.this, PatternLockActivity.class);
                i.putExtra("mode", "edit");
                startActivity(i);
            } else {

                sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().
                        putString(getString(R.string.pref_key_pattern), "none").commit();
                //alert the user
                Toast.makeText(this, getResources().getString(R.string.toast_text_password_off), Toast.LENGTH_SHORT).show();
            }

        }

        if (key.equals(getResources().getString(R.string.pref_key_reminder)) && sharedPreferences.getBoolean(getResources().getString(R.string.pref_key_reminder), false)) {

            //alert the user
            SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text_reminder) + " " + manager.getPrefsReminderTime(), Toast.LENGTH_SHORT).show();

            setAlarm();
        }
        updatePrefSummary(findPreference(key));

    }

    //init summaries of the preference screen
    private void initSummaries(Preference p) {

        //if preference is a PreferenceGroup , init summary for each child in it
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummaries(pGrp.getPreference(i));
            }
        } else {
            //else just update this item's summary
            updatePrefSummary(p);
        }

        //if pattern lock disabled
        if (!PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
                .getBoolean(getResources().getString(R.string.pref_key_password), false)) {
            SwitchPreference pattern = (SwitchPreference) findPreference(getResources().getString(R.string.pref_key_password));
            pattern.setChecked(false);
        }
    }

    //update summary accordingly to preference key
    private void updatePrefSummary(Preference p) {

        if (p.getKey().equals(getResources().getString(R.string.pref_key_currency))) {
            ListPreference pref = (ListPreference) p;
            pref.setSummary(pref.getValue());
        } else if (p.getKey().equals(getResources().getString(R.string.pref_key_reminder_time))) {
            String sum = new SharedPrefsManager(SettingsActivity.this).getPrefsReminderTime();
            p.setSummary(sum);
        }
    }

    //sets the password off
    public void disablePassword() {
        SwitchPreference p = (SwitchPreference) findPreference(getResources().getString(R.string.pref_key_password));
        PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putBoolean(getResources().getString(R.string.pref_key_password), false).commit();
        p.setChecked(false);
    }

    //set the alarm for the daily reminder
    public void setAlarm() {

        SharedPrefsManager manager = new SharedPrefsManager(SettingsActivity.this);

        //get calendar instance
        Calendar calendar = Calendar.getInstance();

        //get the preferred time from the preferences file
        String[] time = manager.getPrefsReminderTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        //if this time has passed in the day , set the next notification for tomorrow , same time
        if (hour < calendar.get(Calendar.HOUR_OF_DAY) || hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
            calendar.add(Calendar.DATE, 1);
        }

        //set the calendar instance to the saved date and time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //create an intent with the notification service
        Intent myIntent = new Intent(getApplicationContext(), ReminderReceiver.class);

        //and a pending intent containing the previous intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        //create an alarm manager instance (alarm manager , repeating notifications)
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //set next notification at the above date-time , service starts every 24 hours
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);

    }


    /**
     * Created by Vromia on 04/08/2015.
     */
    public class BackupRestoreDialog extends DialogFragment {

        private ArrayList<GridItem> items;
        private GridView gridView;
        private Dialog dialog;
        public boolean backup;

        public BackupRestoreDialog() {
        }

        public BackupRestoreDialog newInstance(boolean backup) {
            BackupRestoreDialog d = new BackupRestoreDialog();
            Bundle bundle = new Bundle();
            bundle.putBoolean("backup", backup);

            d.setArguments(bundle);

            return d;

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.backup_dialog);
            dialog.setTitle("Backup Destination");

            backup = (boolean) getArguments().getBoolean("backup");

            initUI();
            init();
            initListeners();

            return dialog;

        }


        private void initUI() {
            gridView = (GridView) dialog.findViewById(R.id.gridview);
        }

        private void init() {
            items = new ArrayList<>();
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.dropbox), "Add to Dropbox"));
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.drive), "Google Drive"));
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.sd), "SD card"));

            GridAdapter adapter = new GridAdapter(getActivity(), R.layout.grid_item_menu, items);
            gridView.setAdapter(adapter);
        }


        private void initListeners() {

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {

                        case 0:
                            //TODO dropbox
                            AppKeyPair appKeyPair=new AppKeyPair(APP_KEY,APP_SECRET);
                            AndroidAuthSession session=new AndroidAuthSession(appKeyPair);
                            api=new DropboxAPI<>(session);

                            api.getSession().startOAuth2Authentication(SettingsActivity.this);



                            break;
                        case 1:
                            drive = new BackupRestoreDrive(getBaseContext(), SettingsActivity.this, backup);
                            dialog.dismiss();
                            break;
                        case 2:
                            if (backup) {
                                String MoneyDBpath = "/data/" + getPackageName() + "/databases/MoneyDatabase";
                                String MoneyOutputName = "/MoneyDatabase";
                                BackupRestoreSD backupRestoreSD = new BackupRestoreSD(MoneyDBpath, MoneyOutputName, SettingsActivity.this);
                                boolean MoneySuccess = backupRestoreSD.backup();

                                String CategoriesDBpath = "/data/" + getPackageName() + "/databases/categories";
                                String CategoriesOutputName = "/CategoryDatabase";


                                backupRestoreSD = new BackupRestoreSD(CategoriesDBpath, CategoriesOutputName, SettingsActivity.this);
                                boolean CategorySuccess = backupRestoreSD.backup();


                                if (MoneySuccess && CategorySuccess) {
                                    Toast.makeText(SettingsActivity.this, "Backup Done Succesfully!", Toast.LENGTH_LONG)
                                            .show();

                                }
                            } else {
                                String MoneyDBpath = "/data/" + getPackageName() + "/databases/MoneyDatabase";
                                String MoneyOutputName = "/MoneyDatabase";
                                BackupRestoreSD backupRestoreSD = new BackupRestoreSD(MoneyDBpath, MoneyOutputName, SettingsActivity.this);
                                boolean MoneySuccess = backupRestoreSD.restore();

                                String CategoriesDBpath = "/data/" + getPackageName() + "/databases/categories";
                                String CategoriesOutputName = "/CategoryDatabase";
                                backupRestoreSD = new BackupRestoreSD(CategoriesDBpath, CategoriesOutputName, SettingsActivity.this);
                                boolean CategorySuccess = backupRestoreSD.restore();

                                if (MoneySuccess && CategorySuccess) {
                                    Toast.makeText(SettingsActivity.this, "Restore Done Succesfully!", Toast.LENGTH_LONG)
                                            .show();

                                }
                            }


                    }


                }
            });

        }


    }


}
