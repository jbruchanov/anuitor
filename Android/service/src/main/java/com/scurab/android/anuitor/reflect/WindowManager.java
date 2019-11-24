package com.scurab.android.anuitor.reflect;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:55
 */
public interface WindowManager {

    @Nullable
    Activity getCurrentActivity();

    @Nullable
    View getCurrentRootView();

    @Nullable
    String[] getViewRootNames();

    @Nullable
    View getRootView(String rootName);

    @Nullable
    View getRootView(int index);
}
