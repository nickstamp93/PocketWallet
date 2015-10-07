package com.ngngteam.pocketwallet.Utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.ngngteam.pocketwallet.Activities.SettingsActivity;
import com.ngngteam.pocketwallet.Data.MoneyDatabase;
import com.ngngteam.pocketwallet.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private Context context;
    private DropboxAPI<AndroidAuthSession> api;


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


    private void init() {
        db = new MoneyDatabase(context);
        expenseCursor = db.getAllExpenses();
        incomeCursor = db.getAllIncomes();

        currency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.pref_key_currency), context.getResources().getString(R.string.pref_currency_default_value));

    }

    public void exportExcelToSD() {

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath());

        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        File file = new File(directory, filename);
        writeExcel(file);

    }


    public void exportToDropBox() {
        new UploadExcelToDropBox().execute();
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


    public class UploadExcelToDropBox extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            exportExcelToSD();
            File sdCard = Environment.getExternalStorageDirectory();
            File file=new File(sdCard.getAbsolutePath()+"/"+filename);

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

            api.getSession().unlink();
            Toast.makeText(context, "Export to DropBox was done successfully ", Toast.LENGTH_LONG).show();
        }
    }


}
