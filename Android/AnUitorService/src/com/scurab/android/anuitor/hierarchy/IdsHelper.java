package com.scurab.android.anuitor.hierarchy;

import android.util.SparseArray;

import com.scurab.android.anuitor.R;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:52
 */
public class IdsHelper {

    private static final HashMap<String, SparseArray<String>> VALUES;

    static {
        VALUES = new HashMap<String, SparseArray<String>>();
    }

    public static void loadValues(Class<?> Rclass) throws NoSuchFieldException, ClassNotFoundException {
        if (!VALUES.isEmpty()) {
            return;
        }
        Class<?>[] clzs = Rclass.getClasses();
        for (Class<?> clz : clzs) {
            fillClass(clz, false);
        }

        clzs = android.R.class.getClasses();
        for (Class<?> clz : clzs) {
            fillClass(clz, true);
        }
    }

    private static void fillClass(Class<?> containerClass, boolean android) {
        SparseArray<String> values = new SparseArray<String>();
        String name = containerClass.getSimpleName();
        VALUES.put(containerClass.getCanonicalName(), values);
        fillFields(name, values, containerClass.getFields(), android);
    }

    private static void fillFields(String type, SparseArray<String> container, Field[] fields, boolean android) {
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.getType() == int.class) {
                String name = field.getName();
                try {
                    int value = field.getInt(null);
                    container.put(value, String.format("@%s/%s", android ? "android:" + type : type, name));
                } catch (Exception e) {
                    //this should never happen if we have setAccessible
                    continue;
                }
            }
        }
    }

    private static Class<?> getClass(Class<?> parent, String name) {
        Class<?>[] classes = parent.getClasses();
        for (Class<?> aClass : classes) {
            if (name.equals(aClass.getSimpleName())) {
                return aClass;
            }
        }
        return null;
    }

    public static String getValueForId(int id) {
        if (id == -1) {
            return "undefined";
        }
        for (String type : VALUES.keySet()) {
            SparseArray<String> array = VALUES.get(type);
            String result = array.get(id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
