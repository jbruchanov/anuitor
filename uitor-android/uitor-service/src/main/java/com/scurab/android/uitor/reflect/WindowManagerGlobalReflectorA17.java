package com.scurab.android.uitor.reflect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.scurab.android.uitor.extract2.ExtractorExtMethodsKt;

import java.lang.reflect.Method;

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
            return m.invoke(null, (Object[])null);
        } catch (Exception e) {
            throw new RuntimeException("Problem with calling android.view.WindowManagerGlobal#getInstance()", e);
        }
    }

    @Override
    public Activity getCurrentActivity() {
        View currentRootView = getCurrentRootView();
        if (currentRootView != null) {
            return ExtractorExtMethodsKt.getActivity(currentRootView);
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

    @Override
    public View getRootView(int index) {
        return WindowManagerProvider.getRootView(this, index);
    }
}