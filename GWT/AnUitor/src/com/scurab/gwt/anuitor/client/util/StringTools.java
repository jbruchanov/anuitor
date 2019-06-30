package com.scurab.gwt.anuitor.client.util;

public class StringTools {
    
    /**
     * Return filter expresion, null if null or "".
     * Trimmed lowercase otherwise.
     * @param value
     * @return
     */
    public static String filterExpression(String value) {
        if (value == null) {
            return value;
        }
        value = value.trim().toLowerCase();
        return value.isEmpty() ? null : value;
    }

    /**
     * Return "" if object is value, otherwise toString()
     * @param value
     * @return
     */
    public static String emptyIfNull(Object value) {
        return value == null ? "" : value.toString();
    }
}
