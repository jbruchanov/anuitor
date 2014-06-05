package com.scurab.android.anuitor.extract;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class AbsListViewExtractor extends ViewGroupExtractor  {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        AbsListView lv = (AbsListView)v;

        ListAdapter adapter = lv.getAdapter();
        data.put("Adapter", String.valueOf(adapter));
        data.put("AdapterItemsCount", adapter != null ? adapter.getCount() : 0);

        return data;
    }
}
