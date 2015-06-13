package com.ngngteam.pocketwallet.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.ngngteam.pocketwallet.R.color.green;

/**
 * Created by Nikos on 7/26/2014.
 */
public class HistoryListViewAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private String currency;
    private boolean expense;
    private Context context;
    private CategoryDatabase cdb;

    public HistoryListViewAdapter(Context context, Cursor c) {
        super(context, c);
        currency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.pref_key_currency), context.getResources().getString(R.string.pref_currency_default_value));
        cdb = new CategoryDatabase(context);
        this.context=context;
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
        View view=inflater.inflate(R.layout.list_history_item,parent,false);

        TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        TextView tvNotes = (TextView) view.findViewById(R.id.tvNotes);
        LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistory);

        if(expense){
            tvPrice.setText(cursor.getString(3) + " " + currency);
            tvPrice.setTextColor(context.getResources().getColor(R.color.red));

            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);

            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_date = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                item_date.setTime(format.parse(date));

                boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                if(isToday){
                    tvDate.setText(context.getString(R.string.text_today));
                }else if (isYesterday){
                    tvDate.setText(context.getString(R.string.text_yesterday));
                }else{
                    String dateTokens[] = date.split("-");
                    String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

                    Date d = fmt.parse(reformedDate);
                    SimpleDateFormat fmtOut = new SimpleDateFormat("EEE d MMMM");
                    tvDate.setText(fmtOut.format(d));
                }


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
        }else{
            tvPrice.setText(cursor.getString(1) + " " + currency);
            tvPrice.setTextColor(context.getResources().getColor(R.color.green));
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_date = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                item_date.setTime(format.parse(date));

                boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                if(isToday){
                    tvDate.setText(context.getString(R.string.text_today));
                }else if (isYesterday){
                    tvDate.setText(context.getString(R.string.text_yesterday));
                }else{
                    String dateTokens[] = date.split("-");
                    String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

                    Date d = fmt.parse(reformedDate);
                    SimpleDateFormat fmtOut = new SimpleDateFormat("EEE d MMMM");
                    tvDate.setText(fmtOut.format(d));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvCategory.setText(cursor.getString(2));

            int color = cdb.getColorFromCategory(cursor.getString(2), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(2), expense);

            tvCategory.setTextColor(color);

            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);

            tvNotes.setText(cursor.getString(4));

        }



        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        TextView tvNotes = (TextView) view.findViewById(R.id.tvNotes);
        LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistory);

        if(expense){
            tvPrice.setText(cursor.getString(3) + " " + currency);
            tvPrice.setTextColor(context.getResources().getColor(R.color.red));

            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);

            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_date = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                item_date.setTime(format.parse(date));

                boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                if(isToday){
                    tvDate.setText(context.getString(R.string.text_today));
                }else if (isYesterday){
                    tvDate.setText(context.getString(R.string.text_yesterday));
                }else{
                    String dateTokens[] = date.split("-");
                    String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

                    Date d = fmt.parse(reformedDate);
                    SimpleDateFormat fmtOut = new SimpleDateFormat("EEE d MMMM");
                    tvDate.setText(fmtOut.format(d));
                }


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
        }else{
            tvPrice.setText(cursor.getString(1) + " " + currency);
            tvPrice.setTextColor(context.getResources().getColor(R.color.green));
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            try {
                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                Calendar item_date = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                item_date.setTime(format.parse(date));

                boolean isToday = today.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                boolean isYesterday = yesterday.get(Calendar.YEAR) == item_date.get(Calendar.YEAR) &&
                        yesterday.get(Calendar.DAY_OF_YEAR) == item_date.get(Calendar.DAY_OF_YEAR);
                if(isToday){
                    tvDate.setText(context.getString(R.string.text_today));
                }else if (isYesterday){
                    tvDate.setText(context.getString(R.string.text_yesterday));
                }else{
                    String dateTokens[] = date.split("-");
                    String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];

                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

                    Date d = fmt.parse(reformedDate);
                    SimpleDateFormat fmtOut = new SimpleDateFormat("EEE d MMMM");
                    tvDate.setText(fmtOut.format(d));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvCategory.setText(cursor.getString(2));

            int color = cdb.getColorFromCategory(cursor.getString(2), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(2), expense);

            tvCategory.setTextColor(color);

            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);

            tvNotes.setText(cursor.getString(4));

        }

    }


}
