package com.scurab.android.uitor.extract;

import android.graphics.Rect;
import android.view.View;

/**
 * Iface to define correct render area of view.
 * User if your view is rendering outside predefined bounds.
 * Mostlikely you view's parent has disabled child clipping {@link android.view.ViewGroup#setClipChildren(boolean)}
 *
 * @param <T>
 */
public interface RenderAreaWrapper<T extends View> {

    /**
     * Define relative offset of the view's rendering area
     * @param view
     * @param outRect output argument
     */
    void getRenderArea(T view, Rect outRect);
}
