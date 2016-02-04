package com.scurab.android.anuitor.extract.view;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.LinearLayout;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class LinearLayoutExtractor extends ViewExtractor {

    public LinearLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        LinearLayout ll = (LinearLayout)v;
        data.put("BaselineAlignedChildIndex", ll.getBaselineAlignedChildIndex());
        data.put("IsBaseAligned", ll.isBaselineAligned());

        if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            data.put("MeasureWithLargestChildEnabled", ll.isMeasureWithLargestChildEnabled());
        }

        if(VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            data.put("DividerPadding", ll.getDividerPadding());
        }

        if(VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            data.put("DividerDrawable", String.valueOf(ll.getDividerDrawable()));
            data.put("Dividers", getTranslator().showDividers(ll.getShowDividers()));
        }

        data.put("WeightSum", ll.getWeightSum());
        data.put("Orientation", getTranslator().orientation(ll.getOrientation()));

        return data;
    }
}
