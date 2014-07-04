package com.scurab.android.anuitor.reflect;

import com.scurab.android.anuitor.tools.DOM2XmlPullBuilder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.xml.transform.TransformerException;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public abstract class Reflector<T> {

    protected final T mReal;
    protected final Class<?> mClass;

    public Reflector(T real) {
        mReal = real;
        mClass = mReal.getClass();
    }

    protected <T> T callByReflection(Object... params) {
        String methodName = getCalleeMethod();

        Class<?>[] clzs = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            clzs[i] = params[i].getClass();
        }
        fixAutoboxing(clzs);

        Class<?> clz = mClass;
        while (clz != null) {
            try {
                Method m = clz.getDeclaredMethod(methodName, clzs);
                m.setAccessible(true);
                return (T) m.invoke(mReal, params);
            } catch (Exception e) {
                clz = clz.getSuperclass();
            }
        }
        throw new RuntimeException("Unable to find method: " + methodName + "(" + Arrays.toString(clzs) + ")");
    }

    protected String getCalleeMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[4].getMethodName();//getThreadStackTrace, getCalleeMethod, callByReflection, our method
    }

    protected <T> T getFieldValue(String fieldName) {
        Class<?> clz = mClass;
        while (clz != null) {
            try {
                Field f = clz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return (T) f.get(mReal);
            } catch (Exception e) {
                clz = clz.getSuperclass();
            }
        }
        throw new RuntimeException("Unable to find field:" + fieldName);
    }

    //FIXME: naive, if there is nonPrimitive as param, it will fail
    protected void fixAutoboxing(Class<?>[] params) {
        for (int i = 0; params != null && i < params.length; i++) {
            Class<?> clz = params[i];
            if (clz == Integer.class) {
                params[i] = int.class;
            } else if (clz == Boolean.class) {
                params[i] = boolean.class;
            } else if (clz == Short.class) {
                params[i] = short.class;
            } else if (clz == Character.class) {
                params[i] = char.class;
            } else if (clz == Byte.class) {
                params[i] = byte.class;
            } else if (clz == Long.class) {
                params[i] = long.class;
            } else if (clz == Double.class) {
                params[i] = double.class;
            } else if (clz == Float.class) {
                params[i] = float.class;
            }
        }
    }

    protected String transformToString(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException, TransformerException {
        return DOM2XmlPullBuilder.transform(xmlPullParser);
    }
}
