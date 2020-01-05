package com.scurab.android.uitor.reflect;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;

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
