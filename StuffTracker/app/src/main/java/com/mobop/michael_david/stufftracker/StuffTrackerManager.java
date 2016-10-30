package com.mobop.michael_david.stufftracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by David on 28.10.2016.
 */

public class StuffTrackerManager {

    Context context;
    ArrayList<StuffItem> stuffItems;

    public StuffTrackerManager(Context context) {
        this.context = context;

        stuffItems = new ArrayList<StuffItem>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date1 = cal.getTime();

        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date date2 = cal.getTime();

        stuffItems.add(new StuffItem(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.default_photo), "TEST OBJECT 1",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));

        stuffItems.add(new StuffItem(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.default_photo), "TEST OBJECT 2",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));

        stuffItems.add(new StuffItem(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.default_photo), "TEST OBJECT 3",
                "BLABLABLA BLA BLA BLAB BLAB ABLAB A BLALBA BA BLAB ABALB ABLA BABLABAB ABABA",
                "PC, Tablet, Dinosaur, mommy",
                "01020305060405",
                date1,
                date2));
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
