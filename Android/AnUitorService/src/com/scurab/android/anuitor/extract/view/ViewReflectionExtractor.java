package com.scurab.android.anuitor.extract.view;

import android.util.Log;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.ExportView;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ViewReflectionExtractor extends ViewExtractor {
    public static final String GET = "get";

    /**
     * Fill this with regexp patterns to ignore methods
     */
    public static Pattern[] IGNORE_PATTERNS = new Pattern[]{
            Pattern.compile("add.*", 0),
            Pattern.compile("call.*", 0),
            Pattern.compile("gen.*", 0),
            Pattern.compile("on.*", 0),
            Pattern.compile("perform.*", 0),
            Pattern.compile("request.*", 0),
            Pattern.compile("show.*", 0),
            Pattern.compile("will.*", 0)
    };

    public ViewReflectionExtractor(Translator translator) {
        super(translator);
    }

    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        data.put("Type", String.valueOf(v.getClass().getCanonicalName()));

        Class clz = v.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (ignoreMethod(name)) {
                continue;
            }

            Class<?> returnType = method.getReturnType();
            if (method.getParameterTypes().length == 0
                    && returnType.isPrimitive()
                    && !returnType.equals(Void.TYPE)) {

                method.setAccessible(true);
                try {
                    Object value = method.invoke(v, null);
                    data.put(name, value);
                } catch (Exception e) {
                    Log.e("Extractor", String.format("Name:%s Exception:%s", name, e.getClass().getSimpleName()));
                }
            }
        }

        fillScale(v, data, parentData);
        fillLocationValues(v, data, parentData);

        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    public static boolean ignoreMethod(String methodName) {
        for (Pattern s : IGNORE_PATTERNS) {
            if (s.matcher(methodName).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
