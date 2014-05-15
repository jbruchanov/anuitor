package com.scurab.android.anuitor.extract;

import android.util.Log;
import android.view.View;
import com.scurab.android.anuitor.hierarchy.ExportField;
import com.scurab.android.anuitor.hierarchy.ExportView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ViewReflectionExtractor extends ViewExtractor{
    private static final int[] position = new int[2];
    public static final String GET = "get";

    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        data.put("Type", String.valueOf(v.getClass().getCanonicalName()));

        Class clz = v.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            //TODO: add some excl mechanic to avoid calling performClick() etc...
            String name = method.getName();
            Class<?> returnType = method.getReturnType();

            if (method.getParameterTypes().length == 0
                    && returnType.isPrimitive()
                    && !returnType.equals(Void.TYPE)) {
                method.setAccessible(true);
                if (name.startsWith(GET)) {
                    name = name.substring(GET.length());
                }
                try {
                    Object value = method.invoke(v, null);
                    data.put(name, value);
                } catch (Exception e) {
                    Log.e("Extractor", String.format("Name:%s Exception:%s", name, e.getClass().getSimpleName()));
                }
            }
        }

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

        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    private boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
