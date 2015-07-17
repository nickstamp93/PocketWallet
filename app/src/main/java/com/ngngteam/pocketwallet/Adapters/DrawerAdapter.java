package com.ngngteam.pocketwallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.R;


/**
 * Created by Vromia on 28/11/2014.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    public DrawerAdapter(Context context, int resource, String activities[]) {
        super(context, resource, activities);
    }

    private class ViewHolder {

        private TextView tvActivity;
        private ImageView ivActivity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String activity = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_item, null);
            holder = new ViewHolder();
            holder.tvActivity = (TextView) convertView.findViewById(R.id.TvDrawer);
            holder.ivActivity = (ImageView) convertView.findViewById(R.id.IvDrawer);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvActivity.setText(activity);
        switch (position) {

            case 0:
                holder.ivActivity.setImageResource(R.drawable.ic_action_database);
                break;
            case 1:
                holder.ivActivity.setImageResource(android.R.drawable.ic_menu_rotate);
                break;
            case 2:
                holder.ivActivity.setImageResource(R.drawable.ic_action_creditcard);
                break;
            case 3:
                holder.ivActivity.setImageResource(R.drawable.ic_action_cart);
                break;
            case 4:
                holder.ivActivity.setImageResource(R.drawable.ic_action_pie_chart);
                break;
            case 5:
                holder.ivActivity.setImageResource(R.drawable.ic_action_bargraph);
                break;
            case 6:
                holder.ivActivity.setImageResource(R.drawable.ic_action_tiles_large);
                break;
            case 7:
                holder.ivActivity.setImageResource(R.drawable.ic_action_gear);
                break;
            case 8:
                holder.ivActivity.setImageResource(R.drawable.ic_action_exit);
                break;

        }
        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String activity = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_item, null);
            holder = new ViewHolder();
            holder.tvActivity = (TextView) convertView.findViewById(R.id.TvDrawer);
            holder.ivActivity = (ImageView) convertView.findViewById(R.id.IvDrawer);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvActivity.setText(activity);
        switch (position) {

            case 0:
                holder.ivActivity.setImageResource(R.drawable.ic_action_database);
                break;
            case 1:
                holder.ivActivity.setImageResource(android.R.drawable.ic_menu_rotate);
                break;
            case 2:
                holder.ivActivity.setImageResource(R.drawable.ic_action_creditcard);
                break;
            case 3:
                holder.ivActivity.setImageResource(R.drawable.ic_action_cart);
                break;
            case 4:
                holder.ivActivity.setImageResource(R.drawable.ic_action_pie_chart);
                break;
            case 5:
                holder.ivActivity.setImageResource(R.drawable.ic_action_bargraph);
                break;
            case 6:
                holder.ivActivity.setImageResource(R.drawable.ic_action_tiles_large);
                break;
            case 7:
                holder.ivActivity.setImageResource(R.drawable.ic_action_gear);
                break;
            case 8:
                holder.ivActivity.setImageResource(R.drawable.ic_action_exit);
                break;


        }
        return convertView;
    }
}
