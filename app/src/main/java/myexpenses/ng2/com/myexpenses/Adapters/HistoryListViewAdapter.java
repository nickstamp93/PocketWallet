package myexpenses.ng2.com.myexpenses.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.Extra.LetterImageView;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 7/26/2014.
 */
public class HistoryListViewAdapter extends CursorAdapter {

    LayoutInflater inflater;
    String currency;
    private boolean expense;
    private CategoryDatabase cdb;

    public HistoryListViewAdapter(Context context, Cursor c) {
        super(context, c);
        currency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.pref_key_currency), context.getResources().getString(R.string.pref_currency_default_value));
        cdb = new CategoryDatabase(context);




    }

    public void setTheView(boolean expense) {
        this.expense = expense;
    }

    public void closeCDB() {
        cdb.close();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (expense) {
            view = inflater.inflate(R.layout.list_expense_item_history, parent, false);


            TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            TextView tvNotes = (TextView) view.findViewById(R.id.tvNotes);
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistory);

            tvPrice.setText(cursor.getString(3) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date d = fmt.parse(reformedDate);
                tvDate.setText(fmtOut.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }




            tvCategory.setText(cursor.getString(1));

            int color = cdb.getColorFromCategory(cursor.getString(1), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(1), expense);


            tvCategory.setTextColor(color);

            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);


            tvNotes.setText(cursor.getString(4));
        } else {
            view = inflater.inflate(R.layout.list_income_item_history, parent, false);

            TextView tvIncome = (TextView) view.findViewById(R.id.tvHIncome);
            TextView tvDate = (TextView) view.findViewById(R.id.tvHDate);
            TextView tvSource = (TextView) view.findViewById(R.id.tvHSource);
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistoryincome);

            tvIncome.setText(cursor.getString(1) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date d = fmt.parse(reformedDate);
                tvDate.setText(fmtOut.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvSource.setText(cursor.getString(2));

            int color = cdb.getColorFromCategory(cursor.getString(2), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(2), expense);

            tvSource.setTextColor(color);

            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);

        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (expense) {

            TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
            TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            TextView tvNotes = (TextView) view.findViewById(R.id.tvNotes);
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistory);

            tvPrice.setText(cursor.getString(3) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date d = fmt.parse(reformedDate);
                tvDate.setText(fmtOut.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvCategory.setText(cursor.getString(1));

            tvNotes.setText(cursor.getString(4));


            int color = cdb.getColorFromCategory(cursor.getString(1), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(1), expense);

            tvCategory.setTextColor(color);

            liv.setOval(true);
            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);
        } else {
            TextView tvIncome = (TextView) view.findViewById(R.id.tvHIncome);
            TextView tvDate = (TextView) view.findViewById(R.id.tvHDate);
            TextView tvSource = (TextView) view.findViewById(R.id.tvHSource);
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistoryincome);


            tvIncome.setText(cursor.getString(1) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date d = fmt.parse(reformedDate);
                tvDate.setText(fmtOut.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvSource.setText(cursor.getString(2));

            int color = cdb.getColorFromCategory(cursor.getString(2), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(2), expense);

            tvSource.setTextColor(color);

            liv.setOval(true);
            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);
        }
    }


}
