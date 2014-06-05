package com.scurab.android.anuitor.extract;

import android.view.View;
import android.widget.ScrollView;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ScrollViewExtractor extends ViewGroupExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ScrollView sv = (ScrollView) v;
        data.put("MaxScrollAmount", sv.getMaxScrollAmount());
        data.put("IsFillViewport", sv.isFillViewport());
        data.put("IsSmoothScrolling", sv.isSmoothScrollingEnabled());
        data.put("DelayChildPressedState", sv.shouldDelayChildPressedState());

        return data;
    }
}
