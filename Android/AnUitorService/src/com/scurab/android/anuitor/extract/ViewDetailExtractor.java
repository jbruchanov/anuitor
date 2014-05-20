package com.scurab.android.anuitor.extract;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.scurab.android.anuitor.model.ViewNode;

import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 13:25
 */
public final class ViewDetailExtractor {

    private static final HashMap<Class<?>, ViewExtractor> MAP;

    static {
        MAP = new HashMap<Class<?>, ViewExtractor>();
        registerExtractor(TextView.class, new TextViewExtractor());
        registerExtractor(View.class, new ViewExtractor());
    }

    public static void registerExtractor(Class<?> clz, ViewExtractor extractor) {
        MAP.put(clz, extractor);
    }

    public static void unregisterExtractor(Class<?> clz){
        MAP.remove(clz);
    }

    public static ViewNode parse(View rootView, boolean lazy) {
        ViewNode vn = new ViewNode(rootView.getId(), 0 , lazy
                                                         ? null
                                                         : getExtractor(rootView).fillValues(rootView, new HashMap<String, Object>(), null));


        //TODO:version problem
        parse(rootView, vn, 1, lazy, vn.getData());
        return vn;
    }

    private static void parse(View rootView, ViewNode root, int level, boolean lazy, HashMap<String, Object> parentData) {
        if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0, n = group.getChildCount(); i < n; i++) {
                View child = group.getChildAt(i);
                ViewNode vn = new ViewNode(child.getId(), level, lazy
                                                          ? null
                                                          : getExtractor(child).fillValues(child, new HashMap<String, Object>(), parentData));
                root.addChild(vn);
                parse(child, vn, level+1, lazy, vn.getData());
            }
        }
    }

    static ViewExtractor getExtractor(View v) {
        Class<?> clz = v.getClass();
        ViewExtractor ve = MAP.get(clz);
        while (ve == null && clz != Object.class) {//object just for sure that View is unregistered
            clz = clz.getSuperclass();
            ve = MAP.get(clz);
        }
        if (ve == null) {
            ve = MAP.get(View.class);
        }
        return ve;
    }
}
