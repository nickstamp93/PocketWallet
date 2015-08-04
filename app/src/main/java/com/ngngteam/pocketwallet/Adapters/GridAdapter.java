package com.ngngteam.pocketwallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Model.GridItem;
import com.ngngteam.pocketwallet.R;

import java.util.ArrayList;

/**
 * Created by Vromia on 04/08/2015.
 */
public class GridAdapter extends ArrayAdapter<GridItem> {

    private int layoutResourceId;
    private Context context;
    private ArrayList<GridItem> data;



    public GridAdapter(Context context, int layoutResourceId, ArrayList<GridItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.txtTitle = (TextView) row.findViewById(R.id.tvGridItem);
            holder.imageItem = (ImageView) row.findViewById(R.id.ivGridItem);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        GridItem item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.imageItem.setImageBitmap(item.getImage());

        return row;

    }



    class Holder {
        TextView txtTitle;
        ImageView imageItem;

    }



}
