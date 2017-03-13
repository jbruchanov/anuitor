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
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        CoordinatorLayout view = (CoordinatorLayout) v;

        result.put("NestedScrollAxes", view.getNestedScrollAxes());
        result.put("StatusBarBackground:", view.getStatusBarBackground());

        return result;
    }
}
