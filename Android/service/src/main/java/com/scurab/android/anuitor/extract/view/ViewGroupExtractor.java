package com.scurab.android.anuitor.extract.view;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewGroup;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ViewGroupExtractor extends ViewExtractor {

    public ViewGroupExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ViewGroup vg = (ViewGroup) v;
        data.put("ChildCount", vg.getChildCount());

        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            data.put("LayoutTransition", String.valueOf(vg.getLayoutTransition()));
            data.put("IsMotionEventSplittingEnabled", vg.isMotionEventSplittingEnabled());
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            data.put("ShouldDelayChildPressedState", vg.shouldDelayChildPressedState());
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("ClipChildren", vg.getClipChildren());
            data.put("LayoutMode", getTranslator().layoutMode(vg.getLayoutMode()));
        }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("ClipToPadding", vg.getClipToPadding());
            data.put("NestedScrollAxes", vg.getNestedScrollAxes());
            data.put("TouchscreenBlocksFocus", vg.getTouchscreenBlocksFocus());
            data.put("TransitionGroup", vg.isTransitionGroup());
        }

        data.put("DescendantFocusability", vg.getDescendantFocusability());
        data.put("FocusedChild", String.valueOf(vg.getFocusedChild()));
        data.put("LayoutAnimation", String.valueOf(vg.getLayoutAnimation()));
        data.put("LayoutAnimationListener", String.valueOf(vg.getLayoutAnimationListener()));
        data.put("PersistentDrawingCache", vg.getPersistentDrawingCache());

        data.put("IsAlwaysDrawnWithCacheEnabled", vg.isAlwaysDrawnWithCacheEnabled());
        data.put("IsAnimationCacheEnabled", vg.isAnimationCacheEnabled());

        return data;
    }
}
