package com.scurab.android.uitor.reflect;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.ViewOverlay;

import java.util.List;

public class ViewOverlayReflector extends Reflector<ViewOverlay> {


    private final OverlayViewGroupReflector mViewGroupReflector;

    public ViewOverlayReflector(ViewOverlay real) {
        super(real);
        mViewGroupReflector = new OverlayViewGroupReflector(getOverlayViewGroup());
    }

    public boolean isEmpty() {
        return callByReflection("isEmpty");
    }

    public int getChildCount() {
        final ViewGroup overlayViewGroup = getOverlayViewGroup();
        return overlayViewGroup != null ? overlayViewGroup.getChildCount() : 0;
    }

    public ViewGroup getOverlayViewGroup() {
        return getFieldValue("mOverlayViewGroup");
    }

    public List<Drawable> getOverlayDrawables() {
        return mViewGroupReflector.getOverlayDrawables();
    }

    public int getOverlayDrawablesCount() {
        final List<Drawable> overlayDrawables = getOverlayDrawables();
        return overlayDrawables != null ? overlayDrawables.size() : 0;
    }

    private static class OverlayViewGroupReflector extends Reflector<Object> {

        public OverlayViewGroupReflector(Object real) {
            super(real);
        }

        private List<Drawable> getOverlayDrawables() {
            return getFieldValue("mDrawables");
        }
    }
}
