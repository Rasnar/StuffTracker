package com.mobop.michael_david.stufftracker;

import android.graphics.Bitmap;
import java.util.Date;

/**
 * A StuffItem stores all the data concerning an object, identified by an id.
 */
class StuffItem {
    private Bitmap image;
    private String name;
    private String description;
    private String categories;
    private String id;
    private String borrower;
    private Date loanStart;
    private Date loanEnd;

    StuffItem() {}

    StuffItem(Bitmap image, String name, String description, String categories, String id, String borrower, Date loanStart, Date loanEnd) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.borrower = borrower;
        this.loanStart = loanStart;
        this.loanEnd = loanEnd;
        this.id = id;
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

    public String getBorrower() {
        return borrower;
    }
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    Date getLoanStart() {
        return loanStart;
    }
    void setLoanStart(Date loanStart) {
        this.loanStart = loanStart;
    }

    Date getLoanEnd() {
        return loanEnd;
    }
    void setLoanEnd(Date loanEnd) {
        this.loanEnd = loanEnd;
    }

    String getId() {
        return id;
    }
    void setId(String id) {
        this.id = id;
    }
}
