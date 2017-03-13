package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.TabLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class TabLayoutExtractor extends ViewGroupExtractor {

    public TabLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        TabLayout view = (TabLayout) v;

        result.put("SelectedTabPosition", view.getSelectedTabPosition());
        result.put("TabCount", view.getTabCount());
        result.put("TabGravity", getTranslator().gravity(view.getTabGravity()));
        result.put("TabMode", getTranslator().tabMode(view.getTabMode()));
        result.put("TabTextColors", String.valueOf(view.getTabTextColors()));
        result.put("ShouldDelayChildPressedState", view.shouldDelayChildPressedState());

        return result;
    }
}
