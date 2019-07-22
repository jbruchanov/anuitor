package com.scurab.android.anuitor.reflect;

import android.app.Activity;
import android.view.View;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:55
 */
public interface WindowManager {

    Activity getCurrentActivity();

    View getCurrentRootView();

    String[] getViewRootNames();

    View getRootView(String rootName);

    View getRootView(int index);
}
