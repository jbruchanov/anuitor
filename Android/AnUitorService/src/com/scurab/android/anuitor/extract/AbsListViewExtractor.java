package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class AbsListViewExtractor extends AdapterViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data,
                                              HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        AbsListView lv = (AbsListView) v;

        //http://developer.android.com/reference/android/widget/AbsListView.html#getChoiceMode() API Level 1 ?!
        data.put("ChoiceMode", Translator.choiceMode(lv.getChoiceMode()));


        data.put("TextFilter", lv.getTextFilter());
        data.put("HasTextFilter", lv.hasTextFilter());
        data.put("IsTextFilterEnabled", lv.isTextFilterEnabled());
        data.put("VerticalScrollbarWidth", lv.getVerticalScrollbarWidth());
        data.put("IsFastScrollEnabled", lv.isFastScrollEnabled());
        data.put("IsScrollingCacheEnabled", lv.isScrollingCacheEnabled());
        data.put("IsSmoothScrollbarEnabled", lv.isSmoothScrollbarEnabled());
        data.put("IsStackFromBottom", lv.isStackFromBottom());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            data.put("CheckedItemCount", lv.getCheckedItemCount());
            SparseBooleanArray checkedItemPositions = lv.getCheckedItemPositions();
            data.put("CheckedItemPositions", checkedItemPositions != null ? checkedItemPositions.toString() : null);
            data.put("IsFastScrollAlwaysVisible", lv.isFastScrollAlwaysVisible());
        }
        return data;
    }
}
