package com.scurab.android.anuitor.reflect;

import android.view.View;
import java.lang.reflect.Method;

/**
 * @author jbruchanov
 * @since 2014-05-28 16:34
 */
public class WindowManagerGlobal extends Reflector<Object> {

    public WindowManagerGlobal() {
        super(getRealInstance());
    }

    public String[] getViewRootNames() {
        return callByReflection();
    }

    public View getRootView(String name) {
        return callByReflection(name);
    }

    private static Object getRealInstance() {
        try {
            Class<?> clz = Class.forName("android.view.WindowManagerGlobal");
            Method m = clz.getDeclaredMethod("getInstance");
            m.setAccessible(true);
            return m.invoke(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Problem with calling android.view.WindowManagerGlobal#getInstance()", e);
        }
    }
}
