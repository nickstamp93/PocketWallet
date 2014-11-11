package myexpenses.ng2.com.myexpenses.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.Data.CategoryDatabase;
import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 7/26/2014.
 */
public class HistoryListViewAdapter extends CursorAdapter {

    LayoutInflater inflater;
    SharedPrefsManager manager;
    String currency;
    private boolean expense;
    private CategoryDatabase cdb;

    public HistoryListViewAdapter(Context context, Cursor c) {
        super(context, c);
        manager = new SharedPrefsManager(context);
        currency = manager.getPrefsCurrency();
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
            // ImageView ivCategory = (ImageView) view.findViewById(R.id.ivCategory);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/font_exo2.otf");

            tvPrice.setTypeface(typeface);
            tvDate.setTypeface(typeface);
            tvCategory.setTypeface(typeface);
            tvNotes.setTypeface(typeface);


            tvPrice.setText(cursor.getString(3) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            tvDate.setText(reformedDate);


            tvCategory.setText(cursor.getString(1));

            int color = cdb.getColorFromCategory(cursor.getString(1), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(1), expense);


            tvCategory.setTextColor(color);

            //liv.setOval(true);
            liv.setLetter(letter);
            liv.setmBackgroundPaint(color);


      /*
      //take from cursor the blob and then convert it to bitmap and finally add it to ImageView ivCategory (Cursor.getBlob(columnIndex))
        byte[] image=cursor.getBlob(5);
        if(image==null){
            ivCategory.setImageResource(R.drawable.food);
        }else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            ivCategory.setImageBitmap(bitmap);
        }
       */
/*
            if (cursor.getString(1).equals("Food")) {
                ivCategory.setImageResource(R.drawable.food);
            } else if (cursor.getString(1).equals("Personal")) {
                ivCategory.setImageResource(R.drawable.personal);
            } else if (cursor.getString(1).equals("Clothing")) {
                ivCategory.setImageResource(R.drawable.clothing);
            } else if (cursor.getString(1).equals("Drinks")) {
                ivCategory.setImageResource(R.drawable.drinks);
            }
            */

            tvNotes.setText(cursor.getString(4));
        } else {
            view = inflater.inflate(R.layout.list_income_item_history, parent, false);

            TextView tvIncome = (TextView) view.findViewById(R.id.tvHIncome);
            TextView tvDate = (TextView) view.findViewById(R.id.tvHDate);
            TextView tvSource = (TextView) view.findViewById(R.id.tvHSource);
            LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistoryincome);


            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/font_exo2.otf");
            tvIncome.setTypeface(typeface);
            tvDate.setTypeface(typeface);
            tvSource.setTypeface(typeface);

            tvIncome.setText(cursor.getString(1) + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            tvDate.setText(reformedDate);

            tvSource.setText(cursor.getString(2));

            int color = cdb.getColorFromCategory(cursor.getString(2), expense);
            char letter = cdb.getLetterFromCategory(cursor.getString(2), expense);

            tvSource.setTextColor(color);

            //liv.setOval(true);
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
            // ImageView ivCategory = (ImageView) view.findViewById(R.id.ivCategory);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/font_exo2.otf");
            tvPrice.setTypeface(typeface);
            tvDate.setTypeface(typeface);
            tvCategory.setTypeface(typeface);
            tvNotes.setTypeface(typeface);

            tvPrice.setText(cursor.getString(3) + " " + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(2);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            tvDate.setText(reformedDate);

            tvCategory.setText(cursor.getString(1));

        /*
        byte[] image=cursor.getBlob(5);
        if(image==null){
            ivCategory.setImageResource(R.drawable.food);
        }else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            ivCategory.setImageBitmap(bitmap);
        }*/
            /*

            if (cursor.getString(1).equals("Food")) {
                ivCategory.setImageResource(R.drawable.food);
            } else if (cursor.getString(1).equals("Personal")) {
                ivCategory.setImageResource(R.drawable.personal);
            } else if (cursor.getString(1).equals("Clothing")) {
                ivCategory.setImageResource(R.drawable.clothing);
            } else if (cursor.getString(1).equals("Drinks")) {
                ivCategory.setImageResource(R.drawable.drinks);
            }
            */
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

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/font_exo2.otf");
            tvIncome.setTypeface(typeface);
            tvDate.setTypeface(typeface);
            tvSource.setTypeface(typeface);

            tvIncome.setText(cursor.getString(1) + currency);
            //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
            //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
            String date = cursor.getString(3);
            String dateTokens[] = date.split("-");
            String reformedDate = dateTokens[2] + "-" + dateTokens[1] + "-" + dateTokens[0];
            tvDate.setText(reformedDate);
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
