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

    public int getItemsCount() {
        return stuffItems.size();
    }

    public void deleteAllItems( ) { stuffItems.clear();}
}
