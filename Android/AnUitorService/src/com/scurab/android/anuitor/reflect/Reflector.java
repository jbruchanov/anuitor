package com.scurab.android.anuitor.reflect;

import com.scurab.android.anuitor.tools.DOM2XmlPullBuilder;
import android.content.res.Resources;

import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public abstract class Reflector<T> {

    protected final T mReal;

    protected Reflector(T mReal) {
        this.mReal = mReal;
    }

    protected <T> T callByReflection(Object... objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = getCalleeMethod();

        Class<?>[] clzs = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            clzs[i] = objects[i].getClass();
        }
        fixAutoboxing(clzs);

        Method m = Resources.class.getDeclaredMethod(methodName, clzs);
        m.setAccessible(true);
        return (T) m.invoke(mReal, objects);
    }

    protected String getCalleeMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[4].getMethodName();//getThreadStackTrace, getCalleeMethod, callByReflection, our method
    }

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
