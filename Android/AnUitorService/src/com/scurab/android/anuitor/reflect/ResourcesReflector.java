package com.scurab.android.anuitor.reflect;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.TypedValue;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.TransformerException;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class ResourcesReflector extends Reflector<Resources> {

    public ResourcesReflector(Resources mReal) {
        super(mReal);
    }

    /**
     * Convert resource id into xml file.
     * In case of not xml file null is returned.
     * 
     * @param resId
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws TransformerException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public String load(int resId) throws TransformerException, XmlPullParserException, IOException {
        TypedValue value = new TypedValue();
        mReal.getValue(resId, value, true);
        if (!value.string.toString().endsWith(".xml")) {
            return null;
        }
        String type = getType(value);
        XmlResourceParser xmlPullParser = loadXmlResourceParser(value.string.toString(), resId, value.assetCookie, type);
        return transformToString(xmlPullParser);
    }

    private String getType(TypedValue value){
        return value.string.toString().split("/")[1];
    }

    public XmlResourceParser loadXmlResourceParser(String file, int id,
                                                   int assetCookie, String type){
        return callByReflection(file, id, assetCookie, type);
    }
}
