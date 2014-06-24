package com.scurab.android.anuitor.extract;

import com.scurab.android.anuitor.tools.HttpTools;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public abstract class BaseExtractor<T> {

    public abstract HashMap<String, Object> fillValues(T t, HashMap<String, Object> data, HashMap<String, Object> contextData);

    /**
     * Convert int value into hex #AARRGGBB format
     * @param value
     * @return
     */
    public static String getStringColor(int value) {
        return HttpTools.getStringColor(value);
    }
}
