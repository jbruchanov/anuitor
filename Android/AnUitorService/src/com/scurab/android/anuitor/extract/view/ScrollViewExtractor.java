package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.widget.ScrollView;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ScrollViewExtractor extends ViewGroupExtractor {

    public ScrollViewExtractor(Translator translator) {
        super(translator);
    }

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
