package com.mobop.michael_david.stufftracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private StuffTrackerManager stuffTrackerManager;

    public RecyclerViewAdapter(StuffTrackerManager slm) {
        this.stuffTrackerManager = slm;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(stuffTrackerManager.getName(position));
        holder.itemDescription.setText(stuffTrackerManager.getDescription(position));
        holder.itemCategories.setText(stuffTrackerManager.getCategories(position));

        if (stuffTrackerManager.getImage(position) != null) {
            holder.itemImage.setImageBitmap(stuffTrackerManager.getImage(position));
        }
    }

    @Override
    public int getItemCount() {
        return stuffTrackerManager.getItemsCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImage;
        public TextView itemName;
        public TextView itemDescription;
        public TextView itemCategories;

        public ViewHolder(View v) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemDescription = (TextView) v.findViewById(R.id.item_description);
            itemCategories = (TextView) v.findViewById(R.id.item_categories);
        }
    }

}
