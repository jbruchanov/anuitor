package com.scurab.android.anuitor.extract.view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ViewPagerExtractor extends ViewGroupExtractor {

    public ViewPagerExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ViewPager vp = (ViewPager)v;
        PagerAdapter adapter = vp.getAdapter();
        data.put("Adapter:", String.valueOf(adapter));
        data.put("AdapterItemsCount", adapter != null ? adapter.getCount() : 0);
        data.put("CurrentItem", vp.getCurrentItem());
        data.put("OffscreenPageLimit", vp.getOffscreenPageLimit());
        data.put("PageMargin", vp.getPageMargin());

        return data;
    }
}
