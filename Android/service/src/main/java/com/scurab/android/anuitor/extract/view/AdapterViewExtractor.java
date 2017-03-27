package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class AdapterViewExtractor extends ViewGroupExtractor  {

    public AdapterViewExtractor(Translator mTranslator) {
        super(mTranslator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        AdapterView<?> lv = (AdapterView<?>)v;

        Adapter adapter = lv.getAdapter();
        data.put("Adapter:", String.valueOf(adapter));
        data.put("AdapterItemsCount", adapter != null ? adapter.getCount() : 0);
        data.put("EmptyViewId", lv.getEmptyView() != null ? IdsHelper.getNameForId(lv.getEmptyView().getId()) : 0);
        data.put("PositionFirstVisible", lv.getFirstVisiblePosition());
        data.put("PositionLastVisible", lv.getLastVisiblePosition());
        data.put("SelectedItem", String.valueOf(lv.getSelectedItem()));
        data.put("SelectedItemId", lv.getSelectedItemId());
        data.put("HasOnItemClickListener", lv.getOnItemClickListener() != null);
        data.put("HasOnItemLongClickListener", lv.getOnItemLongClickListener() != null);
        data.put("HasOnItemSelectedListener", lv.getOnItemSelectedListener() != null);
        return data;
    }
}
