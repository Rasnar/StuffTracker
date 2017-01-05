package com.mobop.michael_david.stufftracker;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manager for the StuffItems
 * It's a singleton giving access to the StuffItems, referenced in an ArrayList.
 */

class StuffItemsManager {

    private static StuffItemsManager mInstance = null;

    ArrayList<StuffItem> stuffItems = new ArrayList<StuffItem>();

    private StuffItemsManager() {

    }

    static StuffItemsManager getInstance(){
        if(mInstance == null)
        {
            mInstance = new StuffItemsManager();
        }
        return mInstance;
    }

    void addStuffItem(StuffItem stuffItem){
        stuffItems.add(stuffItem);
    }

    StuffItem getItem(int itemIndex) { return stuffItems.get(itemIndex);}

    int getItemsCount() {
        return stuffItems.size();
    }

    void deleteAllItems( ) { stuffItems.clear();}
}
