package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.BottomNavigationView;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class BottomNavigationViewExtractor extends ViewGroupExtractor {

    public BottomNavigationViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        BottomNavigationView view = (BottomNavigationView) v;
        data.put("ItemBackgroundResource", IdsHelper.getNameForId(view.getItemBackgroundResource()));
        data.put("ItemIconTintList", String.valueOf(view.getItemIconTintList()));
        data.put("ItemTextColor", String.valueOf(view.getItemTextColor()));
        data.put("MaxItemCount", view.getMaxItemCount());
        data.put("Menu:", view.getMenu());
        return data;
    }
}
