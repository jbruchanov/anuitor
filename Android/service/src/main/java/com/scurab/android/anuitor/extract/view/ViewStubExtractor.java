package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.view.ViewStub;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ViewStubExtractor extends ViewExtractor {

    public ViewStubExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ViewStub vs = (ViewStub) v;
        data.put("LayoutResource", IdsHelper.getNameForId(vs.getLayoutResource()));
        data.put("InflatedId", vs.getInflatedId());
        data.put("InflatedIdS", IdsHelper.getNameForId(vs.getInflatedId()));

        return data;
    }
}
