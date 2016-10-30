package com.mobop.michael_david.stufftracker;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by David on 28.10.2016.
 */
public class StuffItem {
    private Bitmap image;
    private String name;
    private String description;
    private String categories;
    private String nfcId;
    private Date  takenDate;
    private Date  returnDate;

    public StuffItem(Bitmap image, String name, String description, String categories, String nfcId, Date takenDate, Date returnDate) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.nfcId = nfcId;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getCategories() {
        return categories;
    }

    public String getDescription() {
        return description;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Date getTakenDate() {
        return takenDate;
    }

    public String getNfcId() {
        return nfcId;
    }
}
