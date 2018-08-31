package com.talkable.sdk.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
    private static DateFormat format;
    private static String formatString;

    static {
        formatString = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        format = new SimpleDateFormat(formatString);
    }

    public static String toIso8601(Date date) {
        if (date == null) {
            return null;
        }
        return format.format(date);
    }

    public static Date parseIso8601(String input) {
        try {
            return format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFormatString() {
        return formatString;
    }
}
