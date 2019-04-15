package com.scurab.android.anuitor.extract.view;

import android.util.Log;

import com.scurab.android.anuitor.extract2.BaseExtractor;
import com.scurab.android.anuitor.reflect.Reflector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ReflectionExtractor extends BaseExtractor {
    private boolean mUseFields;

    /**
     * Fill this with regexp patterns to ignore methods
     */
    public static Pattern[] IGNORE_PATTERNS = new Pattern[]{
            Pattern.compile("add.*", 0),
            Pattern.compile("call.*", 0),
            Pattern.compile("clear.*", 0),
            Pattern.compile("create.*", 0),
            Pattern.compile("dispatch.*", 0),
            Pattern.compile("exec.*", 0),
            Pattern.compile("find.*", 0),
            Pattern.compile("gen.*", 0),
            Pattern.compile("mutate.*", 0),
            Pattern.compile("on.*", 0),
            Pattern.compile("perform.*", 0),
            Pattern.compile("pop.*", 0),
            Pattern.compile("post.*", 0),
            Pattern.compile("request.*", 0),
            Pattern.compile("resolve.*", 0),
            Pattern.compile("select.*", 0),
            Pattern.compile("show.*", 0),
            Pattern.compile("will.*", 0)
    };

    public ReflectionExtractor() {
        this(false);
    }

    public ReflectionExtractor(boolean useFields) {
        mUseFields = useFields;
    }

    @NotNull
    @Override
    protected Map<String, Object> onFillValues(@NotNull Object item, @NotNull Map<String, Object> data, @Nullable Map<String, Object> contextData) {
        return fillValues(item, data, contextData, new HashSet<>(), 0);
    }

    private Map<String, Object> fillValues(Object o, Map<String, Object> data, Map<String, Object> contextData, Set<Object> cycleHandler, int depth) {
        data.put("Type", String.valueOf(o.getClass().getName()));
        data.put("ToString", String.valueOf(o));
        Class clz = o.getClass();
        List<Method> methods = getAllMethods(new ArrayList<Method>(), clz);
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
                    Object value = method.invoke(o, (Object[])null);
                    data.put(name, value);
                } catch (Exception e) {
                    Log.e("Extractor", String.format("Name:%s Exception:%s", name, e.getClass().getSimpleName()));
                }
            }
        }
        if (mUseFields && depth < 2) {
            final List<Field> fields = getAllFields(new ArrayList<Field>(), o.getClass());
            for (Field f : fields) {
                String name = f.getName();
                if (Modifier.isStatic(f.getModifiers()) || name.startsWith("shadow$")) {
                    continue;
                }
                try {
                    f.setAccessible(true);
                    Object value = f.get(o);
                    if (value != null && !cycleHandler.contains(value)) {
                        cycleHandler.add(value);
                        final Class<?> aClass = Reflector.fixAutoboxing(value.getClass());
                        if (aClass.isPrimitive() || aClass == String.class) {
                            data.put(name, value);
                        } else if (value instanceof Object[]) {
                            List<Object> result = new ArrayList<>();
                            for (Object v : (Object[]) value) {
                                if (v != null) {
                                    final Class<?> vClass = Reflector.fixAutoboxing(v.getClass());
                                    if (vClass.isPrimitive() || vClass == String.class) {
                                        result.add(v);
                                    } else {
                                        result.add(fillValues(v, new HashMap<String, Object>(), data, cycleHandler, depth + 1));
                                    }
                                }
                            }
                            data.put(name, result);
                        } else {
                            data.put(name, fillValues(value, new HashMap<String, Object>(), data, cycleHandler, depth + 1));
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
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

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    private static List<Method> getAllMethods(List<Method> methods, Class<?> type) {
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));
        if (type.getSuperclass() != null) {
            methods = getAllMethods(methods, type.getSuperclass());
        }
        return methods;
    }
}
