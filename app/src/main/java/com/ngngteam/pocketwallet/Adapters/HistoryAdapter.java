package com.ngngteam.pocketwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Data.CategoryDatabase;
import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.Model.ChildItem;
import com.ngngteam.pocketwallet.Model.ParentItem;
import com.ngngteam.pocketwallet.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.preference.PreferenceManager;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Vromia on 13/6/2015.
 */
public class HistoryAdapter extends BaseExpandableListAdapter {


    private Context context;
    private ArrayList<ParentItem> groupItems;
    private LayoutInflater inflater;
    private String currency;
    private CategoryDatabase cdb;
    private boolean expense;

    public HistoryAdapter(Context context,ArrayList<ParentItem> groupItems,boolean expense){
        this.context=context;
        currency = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.pref_key_currency), context.getResources().getString(R.string.pref_currency_default_value));
        this.groupItems=groupItems;
        cdb = new CategoryDatabase(this.context);
        this.expense=expense;
    }

    @Override
    public int getGroupCount() {
        return groupItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupItems.get(groupPosition).getChildItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupItems.get(groupPosition).getChildItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        ParentItem parent=(ParentItem) getGroup(groupPosition);
        if(view==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             view=inflater.inflate(R.layout.group_item,viewGroup,false);

        }

        AutofitTextView tvGroupName=(AutofitTextView) view.findViewById(R.id.tvGroupName);
        AutofitTextView tvGroupAmount=(AutofitTextView) view.findViewById(R.id.tvGroupAmount);

        tvGroupName.setText(parent.getMonth());
        tvGroupAmount.setText(parent.getTotalAmount()+" "+currency);
        if(expense){
            tvGroupAmount.setTextColor(context.getResources().getColor(R.color.red));
        }else{
            tvGroupAmount.setTextColor(context.getResources().getColor(R.color.green));
        }


        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        ChildItem childItem=(ChildItem) getChild(groupPosition,childPosition);
        if(view==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_history_item,viewGroup,false);
        }
        TextView tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        AutofitTextView tvCategory = (AutofitTextView) view.findViewById(R.id.tvCategory);
        TextView tvNotes = (TextView) view.findViewById(R.id.tvNotes);
        LetterImageView liv = (LetterImageView) view.findViewById(R.id.livhistory);

        tvPrice.setText(childItem.getPrice() + " " + currency);
        tvPrice.setTextColor(context.getResources().getColor(R.color.red));

        //We take the date from the cursor we reformed it and we add it to TextView tvDate. We do that cause the format of
        //date in MoneyDatabase is YYYY-MM-DD and we want the user to see it like DD-MM-YYYY
        String date = childItem.getDate();

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


        tvCategory.setText(childItem.getCategory());

        int color = cdb.getColorFromCategory(childItem.getCategory(), expense);
        char letter = cdb.getLetterFromCategory(childItem.getCategory(), expense);

        tvCategory.setTextColor(color);

        liv.setLetter(letter);
        liv.setmBackgroundPaint(color);


        tvNotes.setText(childItem.getNotes());


        return view;
    }

    public void closeCDB() {
        cdb.close();
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void setUpdateGroupItems(ArrayList<ParentItem> newGroups){
        this.groupItems=newGroups;
    }

}
