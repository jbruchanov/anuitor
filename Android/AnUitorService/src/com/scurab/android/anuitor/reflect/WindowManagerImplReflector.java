package com.scurab.android.anuitor.reflect;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;

/**
 * @author jbruchanov
 * @since 2014-05-28 16:34
 *
 * WindowManager used on API<18
 */
public class WindowManagerImplReflector extends Reflector<Object> implements WindowManager {

    public WindowManagerImplReflector() {
        super(getRealInstance());
    }

    private static Object getRealInstance() {
        try {
            Class<?> clz = Class.forName("android.view.WindowManagerImpl");
            Method m = clz.getDeclaredMethod("getDefault");
            m.setAccessible(true);
            return m.invoke(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Problem with calling android.view.WindowManagerGlobal#getInstance()", e);
        }
    }

    @Override
    public Activity getCurrentActivity() {
        ViewRootImplReflector[] roots = getRoots();
        if(roots != null && roots.length > 0){
            Context context = roots[roots.length - 1].getView().getContext();
            if(context instanceof Activity){
                return (Activity) context;
            }
        }
        return null;
    }

    @Override
    public View getCurrentRootView() {
        ViewRootImplReflector[] roots = getRoots();
        return roots != null && roots.length > 0 ? roots[roots.length - 1].getView() : null;
    }

    @Override
    public String[] getViewRootNames() {
        ViewRootImplReflector[] roots = getRoots();
        String[] result = new String[roots != null ? roots.length : 0];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.format("%s_%s", i, roots[i].getView().getClass().getCanonicalName());
        }
        return result;
    }

    @Override
    public View getRootView(String rootName) {
        String[] viewRootNames = getViewRootNames();
        ViewRootImplReflector[] roots = getRoots();
        for (int i = 0; i < viewRootNames.length; i++) {
            if (rootName.equals(viewRootNames[i])) {
                return roots[i].getView();
            }
        }
        return null;
    }

    protected ViewRootImplReflector[] getRoots() {
        Object[] array = getFieldValue("mRoots");
        ViewRootImplReflector[] vrir = new ViewRootImplReflector[array != null ? array.length : 0];
        for (int i = 0; i < vrir.length; i++) {
            vrir[i] = new ViewRootImplReflector(array[i]);
        }
        return vrir;
    }
}