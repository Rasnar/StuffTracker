package com.mobop.michael_david.stufftracker;

import android.graphics.Bitmap;
import java.util.Date;

/**
 * A StuffItem stores all the data concerning an object, identified by a NFC tag.
 */
class StuffItem {
    private Bitmap image;
    private String name;
    private String description;
    private String categories;
    private String nfcId;
    private Date  takenDate;
    private Date  returnDate;

    StuffItem() {}

    StuffItem(Bitmap image, String name, String description, String categories, String nfcId, Date takenDate, Date returnDate) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.nfcId = nfcId;
    }

    String getName() {
        return name;
    }

    Bitmap getImage() {
        return image;
    }

    String getCategories() {
        return categories;
    }

    String getDescription() {
        return description;
    }

    Date getReturnDate() {
        return returnDate;
    }

    Date getTakenDate() {
        return takenDate;
    }

    String getNfcId() {
        return nfcId;
    }
}
