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
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        NavigationView view = (NavigationView) v;
        data.put("HeaderCount", view.getHeaderCount());
        data.put("ItemBackground:", view.getItemBackground());
        data.put("ItemIconTintList", String.valueOf(view.getItemIconTintList()));
        data.put("ItemTextColor", String.valueOf(view.getItemTextColor()));
        data.put("Menu:", view.getMenu());
        return data;
    }
}
