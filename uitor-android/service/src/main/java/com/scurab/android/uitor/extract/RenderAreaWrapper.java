package com.scurab.android.uitor.extract;

import android.graphics.Rect;
import android.view.View;

public interface RenderAreaWrapper<T extends View> {

    void getRenderArea(T view, Rect outRect);
}
