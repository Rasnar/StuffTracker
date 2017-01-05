package com.mobop.michael_david.stufftracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private StuffItemsManager stuffItemsManager;

    RecyclerViewAdapter(StuffItemsManager stuffItemsManager) {
        this.stuffItemsManager = stuffItemsManager;
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
        holder.itemName.setText(stuffItemsManager.getName(position));
        holder.itemDescription.setText(stuffItemsManager.getDescription(position));
        holder.itemCategories.setText(stuffItemsManager.getCategories(position));

        if (stuffItemsManager.getImage(position) != null) {
            holder.itemImage.setImageBitmap(stuffItemsManager.getImage(position));
        }
    }

    @Override
    public int getItemCount() {
        return stuffItemsManager.getItemsCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        TextView itemCategories;

        ViewHolder(View v) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.item_image);
            itemName = (TextView) v.findViewById(R.id.item_name);
            itemDescription = (TextView) v.findViewById(R.id.item_description);
            itemCategories = (TextView) v.findViewById(R.id.item_categories);
        }
    }

}
