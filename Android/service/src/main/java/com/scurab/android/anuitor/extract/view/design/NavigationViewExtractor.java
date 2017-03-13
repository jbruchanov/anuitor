package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.NavigationView;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class NavigationViewExtractor extends ViewGroupExtractor {

    public NavigationViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        NavigationView view = (NavigationView) v;
        result.put("HeaderCount", view.getHeaderCount());
        result.put("ItemBackground:", view.getItemBackground());
        result.put("ItemIconTintList", String.valueOf(view.getItemIconTintList()));
        result.put("ItemTextColor", String.valueOf(view.getItemTextColor()));
        result.put("Menu:", view.getMenu());
        return result;
    }
}
