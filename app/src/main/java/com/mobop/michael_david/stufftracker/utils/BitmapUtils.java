package com.mobop.michael_david.stufftracker.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    /**
     * Convert a Bitmap to a ByteArray.
     * @param bitmap the bitmap to convert.
     * @return the resulting ByteArray.
     */
    public static byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream); // Choosed PNG as it's lossless.
        return stream.toByteArray();
    }

    /**
     * Convert a ByteArray to a Bitmap
     * @param image the byteArray to convert.
     * @return the resulting Bitmap
     */
    public static Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    /** Rotates a Bitmap by an angle.
     *
     * @param angle Rotation angle in degrees (clockwise).
     * @param bitmapSrc The Bitmap to rotate.
     * @return The rotated Bitmap.
     */
    public static Bitmap getRotatedImage(int angle, Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmapSrc, 0,0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }


    /** Methods to decode and resize, if necessary, a selected image, avoiding OutOfMemoryError.
     * Source : http://stackoverflow.com/a/10127787/1975002
     */
    public static Bitmap decodeAndResizeBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions while avoiding memory allocation.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqHeight) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both height
            // and width larger than the requested height/width.
            //TODO : improve with http://stackoverflow.com/a/3549021/1975002
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize > reqWidth)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /** End of Methods to resize an image */
}