package com.freyr.apollo18.util.textFormatters;

import java.text.DecimalFormat;

public class NumberFormatter {

    public static String format(double num) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(num);
    }
}
