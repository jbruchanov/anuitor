package com.scurab.android.anuitor.extract;

import android.view.View;
import android.view.ViewStub;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ViewStubExtractor extends ViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ViewStub vs = (ViewStub) v;
        data.put("LayoutResource", IdsHelper.getValueForId(vs.getLayoutResource()));
        data.put("InflatedId", vs.getInflatedId());
        data.put("InflatedIdS", IdsHelper.getValueForId(vs.getInflatedId()));

        return data;
    }
}
