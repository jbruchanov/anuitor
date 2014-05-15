package com.scurab.android.anuitor.extract;

import android.view.View;
import com.scurab.android.anuitor.hierarchy.ExportField;
import com.scurab.android.anuitor.hierarchy.ExportView;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ViewExtractor {
    private static final int[] position = new int[2];

    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        data.put("Type", String.valueOf(v.getClass().getCanonicalName()));

        data.put("Left", v.getLeft());
        data.put("Top", v.getTop());
        data.put("Right", v.getRight());
        data.put("Bottom", v.getBottom());
        data.put("Width", v.getWidth());
        data.put("Height", v.getHeight());
        data.put("PaddingLeft", v.getPaddingLeft());
        data.put("PaddingTop", v.getPaddingTop());
        data.put("PaddingRight", v.getPaddingRight());
        data.put("PaddingBottom", v.getPaddingBottom());
        data.put("Visibility", v.getVisibility());

        data.put("ScaleX", v.getScaleX());
        data.put("ScaleY", v.getScaleY());

        float fx = 1f;
        float fy = 1f;
        if (parentData != null) {
            Float ofx = (Float) parentData.get("_ScaleX");
            Float ofy = (Float) parentData.get("_ScaleY");
            if (ofx != null || ofy != null) {
                fx = ofx;
                fy = ofy;
            }
        }

        data.put("_ScaleX", v.getScaleX() * fx);
        data.put("_ScaleY", v.getScaleY() * fy);

        v.getLocationOnScreen(position);
        data.put("LocationScreenX", position[0]);
        data.put("LocationScreenY", position[1]);
        position[0] = position[1] = 0;

        v.getLocationInWindow(position);
        data.put("LocationWindowX", position[0]);
        data.put("LocationWindowY", position[1]);

        String s = v.getClass().getSimpleName();
        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    public static HashMap<String, Object> fillAnnotatedValues(View v, HashMap<String, Object> data) {
        Field[] fields = v.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ExportField annotation = field.getAnnotation(ExportField.class);
            if (annotation != null) {
                try {
                    Object o = field.get(v);
                    data.put(annotation.value(), o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
