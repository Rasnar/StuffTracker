package com.mobop.michael_david.stufftracker;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by David on 28.10.2016.
 */

public class StuffTrackerManager {

    private static StuffTrackerManager mInstance = null;

    Context context;
    ArrayList<StuffItem> stuffItems = new ArrayList<StuffItem>();

    public StuffTrackerManager() {

    }

    public static StuffTrackerManager getInstance(){
        if(mInstance == null)
        {
            mInstance = new StuffTrackerManager();
        }
        return mInstance;
    }

    public void addStuffItem(StuffItem stuffItem){
        stuffItems.add(stuffItem);
    }

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

}
