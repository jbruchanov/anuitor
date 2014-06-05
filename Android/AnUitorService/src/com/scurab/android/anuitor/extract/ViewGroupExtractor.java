package com.scurab.android.anuitor.extract;

import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ViewGroupExtractor extends ViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ViewGroup vg = (ViewGroup)v;
        data.put("ChildCount", vg.getChildCount());
        return data;
    }
}
