package com.scurab.android.anuitor.hierarchy;

import android.util.SparseArray;

import java.lang.reflect.Field;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:52
 */
public class IdsHelper {

    private static final SparseArray<String> mValues;

    static {
        mValues = new SparseArray<String>();
    }

    public static void loadValues(Class<?> Rclass) throws NoSuchFieldException, ClassNotFoundException {
        Class<?> id = getClass(Rclass, "id");
        if (id == null) {
            throw new IllegalStateException("Unable to load ids from R class");
        }
        fillFields(null, id.getFields());
        fillFields("android", getClass(android.R.class, "id").getFields());
    }

    private static void fillFields(String prefix, Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                int value = field.getInt(null);
                mValues.put(value, prefix == null ? name : prefix + "." + name);
            } catch (IllegalAccessException e) {
                //this should never happen if we have setAccessible
                continue;
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
        return mValues.get(id, null);
    }
}
