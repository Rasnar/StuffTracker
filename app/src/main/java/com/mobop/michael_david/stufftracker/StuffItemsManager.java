package com.mobop.michael_david.stufftracker;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manage the StuffItems, stored in an ArrayList.
 */

public class StuffItemsManager {

    private static StuffItemsManager mInstance = null;

    ArrayList<StuffItem> stuffItems = new ArrayList<StuffItem>();

    public StuffItemsManager() {

    }

    public static StuffItemsManager getInstance(){
        if(mInstance == null)
        {
            mInstance = new StuffItemsManager();
        }
        return mInstance;
    }

    public void addStuffItem(StuffItem stuffItem){
        stuffItems.add(stuffItem);
    }

    public StuffItem getItem(int itemIndex) { return stuffItems.get(itemIndex);}

    public Bitmap getImage(int itemIndex) {
        return stuffItems.get(itemIndex).getImage();
    }

    public String getName(int itemIndex) {
        return stuffItems.get(itemIndex).getName();
    }

    public String getDescription(int itemIndex) { return stuffItems.get(itemIndex).getDescription(); }

    public String getCategories(int itemIndex) {
        return stuffItems.get(itemIndex).getCategories();
    }

    public Date getTakenDate(int itemIndex) {
        return stuffItems.get(itemIndex).getTakenDate();
    }

    public Date getReturnDate(int itemIndex) {
        return stuffItems.get(itemIndex).getReturnDate();
    }

    public int getItemsCount() {
        return stuffItems.size();
    }

    public void deleteAllItems( ) { stuffItems.clear();}
}
