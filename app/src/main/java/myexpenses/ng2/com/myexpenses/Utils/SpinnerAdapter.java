package myexpenses.ng2.com.myexpenses.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Vromia on 14/9/2014.
 *  Making calls to findViewById() is really slow in practice, and if your adapter has to call it for each View in
 *  your row for every single row then you will quickly run into performance issues.What the ViewHolder class does is
 *  cache the call to findViewById(). Once your ListView has reached the max amount of rows it can display on a screen,
 *  Android is smart enough to begin recycling those row Views. We check if a View is recycled with if (convertView == null).
 *  f it is not null then we have a recycled View and can just change its values, otherwise we need to create a new row View.
 *  The magic behind this is the setTag() method which lets us attach an arbitrary object onto a View object, which is how we save
 *  the already inflated View for future reuse.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private static class ViewHolder{
        TextView tvfilters;
    }


    public SpinnerAdapter(Context context, ArrayList<String> filters){
        super(context, R.layout.spinner_item,filters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String filter=getItem(position);
        ViewHolder viewHolder;

        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.spinner_item,parent,false);

            viewHolder.tvfilters=(TextView)convertView.findViewById(R.id.tvFilters);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.tvfilters.setText(filter);

        return convertView;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        String filter=getItem(position);
        ViewHolder viewHolder;

        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.spinner_item,parent,false);

            viewHolder.tvfilters=(TextView)convertView.findViewById(R.id.tvFilters);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.tvfilters.setText(filter);

        return convertView;
    }
}
