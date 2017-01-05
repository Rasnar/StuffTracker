package com.mobop.michael_david.stufftracker;

import java.util.ArrayList;

/**
 * Manager for the StuffItems
 * It's a singleton giving access to the StuffItems, referenced in an ArrayList.
 */

class StuffItemsManager {

    private static StuffItemsManager mInstance = null;

    private ArrayList<StuffItem> stuffItems = new ArrayList<>();

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
