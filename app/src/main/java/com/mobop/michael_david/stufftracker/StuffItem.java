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
    private String nfcTagId;
    private Date  takenDate;
    private Date  returnDate;

    StuffItem() {}

    StuffItem(Bitmap image, String name, String description, String categories, String nfcTagId, Date takenDate, Date returnDate) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.takenDate = takenDate;
        this.returnDate = returnDate;
        this.nfcTagId = nfcTagId;
    }

    String getName() {
        return name;
    }
    void setName(String name) {
        this.name = name;
    }

    Bitmap getImage() {
        return image;
    }
    void setImage(Bitmap image) {
        this.image = image;
    }

    String getCategories() {
        return categories;
    }
    void setCategories(String categories) {
        this.categories = categories;
    }

    String getDescription() {
        return description;
    }
    void setDescription(String description) {
        this.description = description;
    }

    Date getReturnDate() {
        return returnDate;
    }
    void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    Date getTakenDate() {
        return takenDate;
    }
    void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }

    String getNfcTagId() {
        return nfcTagId;
    }
    void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }
}
