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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ngngteam.pocketwallet.Adapters.RecycleExportAdapter;
import com.ngngteam.pocketwallet.BroadcastReceivers.ReminderReceiver;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerDialog;
import com.ngngteam.pocketwallet.Extra.ColorPicker.ColorPickerSwatch;
import com.ngngteam.pocketwallet.Model.GridItem;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.BackupDropbox;
import com.ngngteam.pocketwallet.Utils.BackupRestoreDrive;
import com.ngngteam.pocketwallet.Utils.BackupRestoreSD;
import com.ngngteam.pocketwallet.Utils.RestoreDropbox;
import com.ngngteam.pocketwallet.Utils.SharedPrefsManager;
import com.ngngteam.pocketwallet.Utils.Themer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import jxl.WorkbookSettings;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    private final int REQUEST_CODE_RESOLUTION = 1;
    private final int TRANSACTIONS_CODE = 2;
    private final int CATEGORIES_CODE = 3;
    final static private String APP_KEY = "slba7p9039i59nw";
    final static private String APP_SECRET = "srq6a16ada8u517";


    private BackupRestoreDrive drive;
    private DropboxAPI<AndroidAuthSession> api;

    private boolean backup;

    private BackupRestoreDialog dialog;
    private ExportDialog exportDialog;

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

        //when user clicks on "export" preference item
        screen = findPreference(getResources().getString(R.string.pref_key_export));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //TODO export to excel

                exportDialog=new ExportDialog().newInstance();
                exportDialog.show(getFragmentManager(),"ExportDialog");

                return false;
            }
        });

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
        //launch an alert dialog with the about text
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

        //when user clicks on Backup preference item
        //call a dialog with the options that user can perform the backup
        screen = findPreference(getResources().getString(R.string.pref_key_backup));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                backup = true;
                dialog = new BackupRestoreDialog().newInstance(backup);
                dialog.show(getFragmentManager(), "dialog");

                return false;
            }
        });


        //when user clicks on Restore preference item
        //call a dialog with the options that user can perform the restore
        screen = findPreference(getResources().getString(R.string.pref_key_restore));
        screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                backup = false;
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
        } else if (requestCode == CATEGORIES_CODE && resultCode == RESULT_OK) {
            drive.saveIDOfCategoriesDriveFile();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //on resume , update the summaries , and  any preferences launched
        initSummaries(getPreferenceScreen());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (api != null) {
            if (api.getSession().authenticationSuccessful()) {

                Log.i("Dropbox", "Authentication success");
                try {
                    // Required to complete auth, sets the access token on the session
                    api.getSession().finishAuthentication();

                    String accessToken = api.getSession().getOAuth2AccessToken();


                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }

                if (backup) {
                    new BackupDropbox(api, SettingsActivity.this, dialog).execute();
                } else {
                    new RestoreDropbox(api, SettingsActivity.this, dialog).execute();
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
     * Created by Nick Zisis on 04/08/2015.
     * Inner class BackupRestoreDialog implements the basic dialog with backup and restore options for the user.
     * More specific the user got 3 options for backup and restore which are using Drive, DropBox or SD card.
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

            backup = (boolean) getArguments().getBoolean("backup");

            if (backup) dialog.setTitle("Backup Destination");
            else dialog.setTitle("Restore");


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
                            AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
                            AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
                            api = new DropboxAPI<>(session);

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


    public static class ExportDialog extends DialogFragment {

        private Dialog dialog;
        private RecyclerView rv;
        private ArrayList<GridItem> items;

        public ExportDialog() {

        }

        public ExportDialog newInstance() {
            ExportDialog d = new ExportDialog();

            return d;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.export_dialog);


            dialog.setTitle("Spreadsheet export");

            init();
            initUI();
            initListeners();

            return dialog;

        }

        private void initListeners() {
        }

        private void init() {
            items = new ArrayList<>();
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.dropbox), "Add to Dropbox"));
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.drive), "Google Drive"));
            items.add(new GridItem(BitmapFactory.decodeResource(getResources(), R.drawable.sd), "SD card"));
        }

        private void initUI() {
            rv=(RecyclerView) dialog.findViewById(R.id.recycler_view);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));

            rv.setAdapter(new RecycleExportAdapter(getActivity(),items));


        }


    }

}
