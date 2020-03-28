package com.hyperion.methodmonitor.business;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static DateFormat sDataFormat;

    public static String format(long time) {
        if (sDataFormat == null) {
            sDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return sDataFormat.format(new Date(time));
    }

    public static long parse(String time) {
        if (TextUtils.isEmpty(time)) {
            return -1;
        }
        try {
            return Long.parseLong(time.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
