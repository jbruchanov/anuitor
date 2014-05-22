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
        String methodName = getCalleeMethod();//remove _

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
            }
        }
    }

    protected String transformToString(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException, TransformerException {
        int type;
        while ((type = xmlPullParser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty loop
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }


        DOM2XmlPullBuilder dom2XmlPullBuilder = new DOM2XmlPullBuilder();
        Element element = dom2XmlPullBuilder.parseSubTree(xmlPullParser);
        StringWriter buffer = new StringWriter();

        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(element), new StreamResult(buffer));

        return buffer.toString();
    }
}
