package com.mobop.michael_david.stufftracker;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter to insert items to Recycler View
 */
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private StuffItemsManager stuffItemsManager;

    RecyclerViewAdapter(StuffItemsManager stuffItemsManager) {
        this.stuffItemsManager = stuffItemsManager;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StuffItem stuffItem = stuffItemsManager.getItem(position);
        holder.itemName.setText(stuffItem.getName());
        holder.itemId.setText(stuffItem.getId());
        holder.itemCategories.setText(stuffItem.getCategories());
        holder.itemDescription.setText(stuffItem.getDescription());

        if(stuffItem.getBorrower().equals("")){
            // Color when not borrowed

            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Date todayDate = new Date();
            try {
                todayDate =  simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date loanEndDate = stuffItem.getLoanEnd();

            if (todayDate.compareTo(loanEndDate) < 0) {
                // Color when borrower
                holder.itemView.setBackgroundColor(Color.parseColor("#ffebee"));
            } else {
                // Color when borrower and date expired
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFDE7"));
            }

        }

        if (stuffItem.getImage() != null) {
            holder.itemImage.setImageBitmap(stuffItem.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return stuffItemsManager.getItemsCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName;
        TextView itemId;
        TextView itemCategories;
        TextView itemDescription;

        ViewHolder(View v) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemId = (TextView) v.findViewById(R.id.item_id);
            itemCategories = (TextView) v.findViewById(R.id.item_categories);
            itemDescription = (TextView) v.findViewById(R.id.item_description);
        }
    }

}
