package com.scurab.android.anuitor.hierarchy;

import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;

import com.scurab.android.anuitor.json.JsonRef;
import com.scurab.android.anuitor.model.Pair;
import com.scurab.android.anuitor.model.Tuple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class preloads everything related with IDs.
 * At the beggining just load values by {@link #loadValues(Class)} and pass class for your application R.class
 *
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:52
 */
public class IdsHelper {

    public enum RefType {
        anim, animator, array, attr,
        bool,
        color,
        dimen, drawable,
        fraction, font,
        id, integer, interpolator,
        layout,
        menu, mipmap,
        navigation,
        plurals,
        raw,
        string, style, styleable,
        transition,
        unknown,
        xml
    }

    static final HashMap<String, SparseArray<String>> VALUES;

    static {
        VALUES = new HashMap<>();
    }

    public static Class RClass;

    /**
     * Load values
     * @param Rclass must be class of your application R.class
     */
    public static void loadValues(Class<?> Rclass) {
        if (!VALUES.isEmpty()) {
            VALUES.clear();
        }
        Class<?>[] clzs = Rclass.getClasses();
        for (Class<?> clz : clzs) {
            fillClass(clz, false);
        }

        clzs = android.R.class.getClasses();
        for (Class<?> clz : clzs) {
            fillClass(clz, true);
        }
        IdsHelper.RClass = Rclass;
    }

    private static void fillClass(Class<?> containerClass, boolean android) {
        SparseArray<String> values = new SparseArray<>();
        String name = containerClass.getSimpleName();
        String v = containerClass.getCanonicalName();
        if (!android) {
            v = v.replace(containerClass.getPackage().getName(), "").substring(1);
        }
        VALUES.put(v, values);
        fillFields(name, values, containerClass.getFields(), android);
    }

    private static void fillFields(String type, SparseArray<String> container, Field[] fields, boolean android) {
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == int.class) {
                String name = field.getName();
                try {
                    int value = field.getInt(null);
                    container.put(value, String.format("%s%s/%s", ("attr".equals(type) ? "?" : "@"), android ? "android:" + type : type, name));
                } catch (Exception e) {
                    //this should never happen if we have setAccessible
                    continue;
                }
            }
        }
    }

    /**
     * Returns name for particular id<br/>
     * If id is not defined {@link android.view.View#getId()} returns -1, this method returns "undefined".
     * If the id is not found (maybe android.internal stuff) null is returned.
     * @param id
     * @return
     */
    public static String getNameForId(int id) {
        if (id == -1) {
            return "View.NO_ID";
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

    /**
     * Get type of particular id. {@link com.scurab.android.anuitor.hierarchy.IdsHelper.RefType#unknown} is returned if not found.
     * @param id
     * @return
     */
    public static RefType getType(int id) {
        for (String type : VALUES.keySet()) {
            SparseArray<String> array = VALUES.get(type);
            String result = array.get(id, null);
            if (result != null) {
                String[] values = type.split("\\.");
                String value = values[values.length - 1];
                RefType refType = RefType.valueOf(value);
                return refType;
            }
        }
        return RefType.unknown;
    }

    /**
     * Convert whole dataset of ids into JSON
     * {@link #toJson(android.content.res.Resources)}
     * @return
     */
    public static String toJson() {
        return toJson(null);
    }

    /**
     * Convert whole dataset of ids into JSON.
     * If @param res is passed, dataset will contain source of xml files for drawables, layouts, colors
     * @param res optional value, can be null
     * @return
     */
    public static String toJson(Resources res) {
        TypedValue tv = new TypedValue();
        CharSequence location = null;
        HashMap<String, List<Pair<Object, Object>>> result = new HashMap<>();
        for (String type : VALUES.keySet()) {
            boolean showValue = res != null && ((type.equals("R.drawable") || type.equals("R.layout") || type.equals("R.color")));
            List<Pair<Object, Object>> list = new ArrayList<Pair<Object, Object>>();
            result.put(type, list);
            SparseArray<String> sa = VALUES.get(type);
            for (int i = 0, n = sa.size(); i < n; i++) {
                int key = sa.keyAt(i);
                String value = sa.get(key);
                if (showValue) {
                    try {
                        res.getValue(key, tv, false);
                        location = tv.string;
                    } catch (Exception e) {
                        //just for sure
                        Log.e("IdsHelper", String.format("Name:%s Err:%s", value, e.getMessage()));
                        e.printStackTrace();
                    }
                }
                list.add(new Tuple<Object, Object, Object>(key, value, showValue ? location : null));
                location = null;
            }
        }
        JsonRef.initJson();
        return JsonRef.JSON.toJson(result);
    }
}
