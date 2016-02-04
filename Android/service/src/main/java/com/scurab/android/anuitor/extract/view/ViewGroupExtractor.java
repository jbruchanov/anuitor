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
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("ClipChildren", vg.getClipChildren());
        }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("ClipToPadding", vg.getClipToPadding());
            data.put("NestedScrollAxes", vg.getNestedScrollAxes());
            data.put("TouchscreenBlocksFocus", vg.getTouchscreenBlocksFocus());
            data.put("TransitionGroup", vg.isTransitionGroup());
        }
        return data;
    }
}
