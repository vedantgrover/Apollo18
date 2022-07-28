package com.freyr.apollo18.util.textFormatters;

import java.text.DecimalFormat;

/**
 * This method is used to create string appearances of long numbers
 */
public class NumberFormatter {

    /**
     * Transforms a number into a presentable format with commas
     *
     * @param num The long number
     * @return The formatted string
     */
    public static String format(double num) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(num);
    }
}
