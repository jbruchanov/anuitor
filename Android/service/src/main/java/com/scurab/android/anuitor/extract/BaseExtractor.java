package com.scurab.android.anuitor.extract;

import com.scurab.android.anuitor.tools.HttpTools;

import java.util.HashMap;

/**
 * Created by jbruchanov on 24/06/2014.
 */
public abstract class BaseExtractor<T> {

    private Translator mTranslator;

    public BaseExtractor(Translator translator) {
        mTranslator = translator;
    }

    public HashMap<String, Object> fillValues(T t, HashMap<String, Object> data, HashMap<String, Object> contextData){
        data.put("Inheritance", getInheritance(t));
        return data;
    }

    public static String getInheritance(Object object) {
        if (object == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        Class<?> clz = object.getClass();
        while (clz != null) {
            sb.append(clz.getName()).append("\n");
            clz = clz.getSuperclass();
        }
        final int len = sb.length();
        if (len > 0) {
            sb.setLength(len - 1);
        }
        return sb.toString();
    }

    /**
     * Convert int value into hex #AARRGGBB format
     * @param value
     * @return
     */
    public static String getStringColor(int value) {
        return HttpTools.getStringColor(value);
    }

    /**
     * Get binary string separated by '-' for every 4 bits
     * @param value
     * @return
     */
    public static String getBinaryString(int value) {
        if (value == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder(Integer.toBinaryString(value));
        while (sb.length() < 32) {
            sb.insert(0, "0");
        }
        for (int i = 1; i < 8; i++) {
            sb.insert((4 * i) + (i - 1), "-");
        }
        return sb.toString();
    }
    /**
     *
     * @return
     */
    public Translator getTranslator() {
        return mTranslator;
    }

    protected void appendClassName(String name, Object object, HashMap<String, Object> data) {
        if (object != null) {
            data.put(name, object.getClass().getName());
        }
    }
}
