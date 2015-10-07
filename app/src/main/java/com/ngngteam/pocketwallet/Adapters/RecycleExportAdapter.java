package com.ngngteam.pocketwallet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Model.GridItem;
import com.ngngteam.pocketwallet.R;

import java.util.ArrayList;

/**
 * Created by Zisis Nick on 05-Oct-15.
 */
public class RecycleExportAdapter extends RecyclerView.Adapter<RecycleExportAdapter.CustomViewHolder> {

    private ArrayList<GridItem> items;
    private Context context;
    private OnItemClickListener listener;

    public RecycleExportAdapter(Context context, ArrayList<GridItem> items,OnItemClickListener listener){

        this.context=context;
        this.items=items;
        this.listener=listener;

    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        protected ImageView ivExport;
        protected TextView tvExport;
        private OnItemClickListener listener;


        public CustomViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivExport=(ImageView) itemView.findViewById(R.id.ivExport);
            tvExport=(TextView) itemView.findViewById(R.id.tvExport);
            this.listener=listener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

        }




    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.export_dialog_item,null);

        CustomViewHolder holder=new CustomViewHolder(view,listener);

        return holder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        GridItem item=items.get(position);

        holder.tvExport.setText(item.getTitle());
        holder.ivExport.setImageBitmap(item.getImage());

    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


}
