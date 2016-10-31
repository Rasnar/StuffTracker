package com.mobop.michael_david.stufftracker;

/**
 * Utility classes for String manipulations.
 */

public class StringUtils {
    /**
     * Converts bytes to their corresponding hexadecimal values.
     * @param in array of bytes.
     * @return the resulting value as a String.
     */
    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
