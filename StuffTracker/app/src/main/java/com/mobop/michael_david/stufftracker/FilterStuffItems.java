package com.mobop.michael_david.stufftracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by David on 30.10.2016.
 */

public class FilterStuffItems implements Parcelable {
    private String name;
    private String brand;
    private String model;
    private String nfcId;

    ArrayList<String> categories;

    public FilterStuffItems(String name, String brand, String model, String nfcId, ArrayList<String> categories) {
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.nfcId = nfcId;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getNfcId() {
        return nfcId;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(nfcId);
        dest.writeStringList(categories);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public FilterStuffItems createFromParcel(Parcel in) {
            return new FilterStuffItems(in);
        }

        public FilterStuffItems[] newArray(int size) {
            return new FilterStuffItems[size];
        }
    };

    // "De-parcel object
    public FilterStuffItems(Parcel in) {
        name = in.readString();
        brand = in.readString();
        model = in.readString();
        nfcId = in.readString();
        categories = in.createStringArrayList();
    }
}
