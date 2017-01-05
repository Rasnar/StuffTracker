package com.mobop.michael_david.stufftracker.utils;


import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageUtils {
    /**
     * Gives the rotation of an image.
     * It searches first in the EXIF data ; if the orientation information is not present,
     * it tries to get it from MediaSource.
     *
     * @param context Application context.
     * @param imageUri Uri of the image.
     * @return The rotation in degrees.
     */
    public static int getImageRotation(Context context, Uri imageUri) {
        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if(rotation == ExifInterface.ORIENTATION_UNDEFINED) {
                return getImageRotationFromMediaStore(context, imageUri);
            }
            else return exifToDegrees(rotation);
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Gets the orientation of an image from MediaStore.
     * It doesn't use the EXIF data of the image. The orientation is here provided directly by MediaStore.
     *
     * Started from : http://stackoverflow.com/a/6931373/1975002
     * Really helpful (both MediaStore and EXIF solutions) : http://stackoverflow.com/a/30572852/1975002
     *
     * @param context Application context.
     * @param imageUri (Content)Uri of the image.
     * @return the rotation in degrees (0, 90, 180 or 270). 0 could also be returned if there's no data about rotation (e.g. the Uri provided wasn't a ContentUri).
     */
    public static int getImageRotationFromMediaStore(Context context, Uri imageUri) {
        Cursor cursor = null;
        int rotation = 0;
        try {
            String[] projection = {MediaStore.Images.Media.ORIENTATION};
            cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int orientationColumnIndex = (cursor.getColumnIndexOrThrow(projection[0]));
                rotation = cursor.getInt(orientationColumnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rotation;
    }

    /**
     * Converts an Exif ORIENTATION_ROTATE value to degrees.
     * @param exifOrientation the Exif orientation (usually ExifInterface.ORIENTATION_ROTATE_xxx).
     * @return the corresponding degrees.
     */
    public static int exifToDegrees(int exifOrientation) {
        int orientation;
        switch(exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                break;
            default:
                orientation = 0;
                break;
        }
        return orientation;
    }
}