package com.scurab.android.anuitor.reflect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * @author jbruchanov
 * @since 2014-05-28 16:34
 */
@TargetApi(value = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class WindowManagerGlobalReflectorA17 extends Reflector<Object> implements WindowManager {

    public WindowManagerGlobalReflectorA17() {
        super(getRealInstance());
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

    @Override
    public Activity getCurrentActivity() {
        View currentRootView = getCurrentRootView();
        if (currentRootView != null) {
            Context context = currentRootView.getContext();
            return context instanceof Activity ? ((Activity) context) : null;
        }
        return null;
    }

    @Override
    public View getCurrentRootView() {
        View[] rootViews = getRootViews();
        return rootViews != null && rootViews.length > 0 ? rootViews[rootViews.length - 1] : null;
    }

    @Override
    public String[] getViewRootNames() {
        View[] mViews = getRootViews();
        String[] mRoots = new String[mViews.length];
        for (int i = 0; i < mRoots.length; i++) {
            mRoots[i] = mViews[i].toString();
        }
        return mRoots;
    }

    @Override
    public View getRootView(String rootName) {
        View[] mViews = getRootViews();
        for (int i = 0; i < mViews.length; i++) {
            if (rootName.equals(mViews[i].toString())) {
                return mViews[i];
            }
        }
        return null;
    }

    private View[] getRootViews() {
        return getFieldValue("mViews");
    }
}