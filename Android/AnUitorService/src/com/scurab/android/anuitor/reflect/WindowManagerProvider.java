package com.scurab.android.anuitor.reflect;

import android.os.Build;

/**
 * @author jbruchanov
 * @since 2014-05-28 16:34
 */
public class WindowManagerProvider {

    /**
     * Get {@link com.scurab.android.anuitor.reflect.WindowManager} based on Android version
     * @return
     */
    public static WindowManager getManager() {
        WindowManager manager;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            manager = new WindowManagerGlobalReflector();
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            manager = new WindowManagerGlobalReflectorA17();
        } else {
            manager = new WindowManagerImplReflector();
        }
        return manager;
    }
}