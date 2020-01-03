package com.scurab.android.uitor.extract;

/**
 * Created by JBruchanov on 04/02/2016.
 */

import android.graphics.Rect;
import android.view.View;

public interface RenderAreaWrapper<T extends View> {

    void getRenderArea(T view, Rect outRect);
}
