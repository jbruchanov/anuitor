package com.scurab.android.anuitor.tools;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by jbruchanov on 27/05/2014.
 */
public class HttpTools {

    public static HashMap<String, String> parseQueryString(String query) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (TextUtils.isEmpty(query)) {
            return result;
        }

        String[] items = query.split("&");
        for (String item : items) {
            String[] kv = item.split("=");
            String key = kv[0];
            String value = kv[1];
            result.put(key, value);
        }
        return result;
    }

    /**
     * Convert int value into hex #AARRGGBB format
     * @param value
     * @return
     */
    public static String getStringColor(int value) {
        return String.format("#%08X", (0xFFFFFFFF & value));
    }
}
