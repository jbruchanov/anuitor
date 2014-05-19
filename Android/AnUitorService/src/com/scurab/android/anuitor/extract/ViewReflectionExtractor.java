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
public class ViewReflectionExtractor extends ViewExtractor {
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

        ViewExtractor.fillScale(v, data, parentData);
        ViewExtractor.fillLocationValues(v, data, parentData);

        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    private boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
