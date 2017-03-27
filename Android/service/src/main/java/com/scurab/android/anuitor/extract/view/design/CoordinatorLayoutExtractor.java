package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class CoordinatorLayoutExtractor extends ViewGroupExtractor {
    public CoordinatorLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CoordinatorLayout view = (CoordinatorLayout) v;

        data.put("NestedScrollAxes", view.getNestedScrollAxes());
        data.put("StatusBarBackground:", view.getStatusBarBackground());

        return data;
    }
}
