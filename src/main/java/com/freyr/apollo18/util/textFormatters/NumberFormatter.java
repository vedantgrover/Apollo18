package com.freyr.apollo18.util.textFormatters;

import java.text.DecimalFormat;

/**
 * This method is used to create string appearances of long numbers
 */
public class NumberFormatter {

    /**
     * Transforms a number into a presentable formatLongNumber with commas
     *
     * @param num The long number
     * @return The formatted string
     */
    public static String formatLongNumber(double num) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(num);
    }

    public static String formatDoubleToString(double num) {
        DecimalFormat df = new DecimalFormat("0.#");
        return df.format(num);
    }
}
