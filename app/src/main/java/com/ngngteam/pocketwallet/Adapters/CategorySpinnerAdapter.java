package com.ngngteam.pocketwallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Extra.LetterImageView;
import com.ngngteam.pocketwallet.Model.SpinnerItem;
import com.ngngteam.pocketwallet.R;

import java.util.ArrayList;

/**
 * Created by Vromia on 14/9/2014.
 * Making calls to findViewById() is really slow in practice, and if your adapter has to call it for each View in
 * your row for every single row then you will quickly run into performance issues.What the ViewHolder class does is
 * cache the call to findViewById(). Once your ListView has reached the max amount of rows it can display on a screen,
 * Android is smart enough to begin recycling those row Views. We check if a View is recycled with if (convertView == null).
 * f it is not null then we have a recycled View and can just change its values, otherwise we need to create a new row View.
 * The magic behind this is the setTag() method which lets us attach an arbitrary object onto a View object, which is how we save
 * the already inflated View for future reuse.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<SpinnerItem> {


    public CategorySpinnerAdapter(Context context, int resourseID, ArrayList<SpinnerItem> spinnerItems) {

        super(context, resourseID, R.layout.spinner_item_categories, spinnerItems);
    }

    private class ViewHolder {
        TextView tvName;
        LetterImageView liv;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SpinnerItem item = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_item_categories, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvSpinnerCategories);
            holder.liv = (LetterImageView) convertView.findViewById(R.id.livSpinner);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        int color = item.getColor();
        char letter = item.getLetter();

        holder.tvName.setText(item.getName());
        holder.tvName.setTextColor(color);
        holder.liv.setLetter(letter);
        holder.liv.setmBackgroundPaint(color);

        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SpinnerItem item = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_item_categories, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvSpinnerCategories);
            holder.liv = (LetterImageView) convertView.findViewById(R.id.livSpinner);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        int color = item.getColor();
        char letter = item.getLetter();

        holder.tvName.setText(item.getName());
        holder.tvName.setTextColor(color);
        holder.liv.setLetter(letter);
        holder.liv.setmBackgroundPaint(color);

        return convertView;
    }

}
