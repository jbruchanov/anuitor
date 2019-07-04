package com.scurab.gwt.anuitor.client.util;

public class StringTools {

    /**
     * Return filter expresion, null if null or "". Trimmed lowercase otherwise.
     * 
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
     * 
     * @param value
     * @return
     */
    public static String emptyIfNull(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Get only Capital chars HelloWorld returns HW
     * 
     * @param value
     * @return
     */
    public static String getCapitals(String value) {
        if (value == null) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = value.length(); i < n; i++) {
            char c = value.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Replace mid of string by "..."
     * @param value
     * @param maxLen
     * @return
     */
    public static String ellipsizeMid(String value, int maxLen) {
        if (value == null || value.length() < maxLen || value.length() < 5) {
            return value;
        }       
        
        StringBuilder sb = new StringBuilder(value);
        int mid = value.length() / 2;
        int size = Math.max(1, 3 + (value.length() - maxLen) / 2);
        sb.replace(mid - size, mid + size, "...");
        return sb.toString();
    }
}
