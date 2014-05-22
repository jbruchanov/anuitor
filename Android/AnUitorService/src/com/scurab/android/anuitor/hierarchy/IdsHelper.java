package com.scurab.android.anuitor.hierarchy;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.scurab.android.anuitor.R;
import com.scurab.android.anuitor.model.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        String v = containerClass.getCanonicalName();
        if(!android) {
            v = v.replace(containerClass.getPackage().getName(), "").substring(1);
        }
        VALUES.put(v, values);
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

    public static String toJson() {
        HashMap<String, List<Pair<Object, Object>>> result = new HashMap<String, List<Pair<Object, Object>>>();
        for (String type : VALUES.keySet()) {
            List<Pair<Object, Object>> list = new ArrayList<Pair<Object, Object>>();
            result.put(type, list);
            SparseArray<String> sa = VALUES.get(type);
            for (int i = 0, n = sa.size(); i < n; i++) {
                int key = sa.keyAt(i);
                String value = sa.get(key);
                list.add(new Pair<Object, Object>(key, value));
            }
        }
        return new Gson().toJson(result);
    }
}
