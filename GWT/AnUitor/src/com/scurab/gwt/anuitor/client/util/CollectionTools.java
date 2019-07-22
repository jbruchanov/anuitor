package com.scurab.gwt.anuitor.client.util;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

@SuppressWarnings("unchecked")
public class CollectionTools {

    public interface Convertor<T> {
        T convert(JSONValue item);
    }

    /**
     * Convert JSONArray into Set<Int>
     * 
     * @param array
     * @return
     */
    public static <T> Set<T> jsonArrayAsIntegerSet(JSONArray array) {
        return (Set<T>) jsonArrayAsSet(array, new Convertor<Integer>() {
            @Override
            public Integer convert(JSONValue item) {
                return (int) item.isNumber().doubleValue();
            }
        });
    }

    /**
     * Convert JSONArray into Set<T>
     * 
     * @param array
     * @param convertor from a JSONObject to particular value
     * @return
     */
    public static <T> Set<T> jsonArrayAsSet(JSONArray array, Convertor<T> convertor) {
        if (array == null) {
            return null;
        }
        Set<Object> s = new HashSet<Object>();
        for (int i = 0, n = array.size(); i < n; i++) {
            Object item = convertor.convert(array.get(i));
            s.add(item);
        }
        return (Set<T>) s;
    }
}
