package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.widget.ListView;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ListViewExtractor extends AdapterViewExtractor  {

    public ListViewExtractor(Translator mTranslator) {
        super(mTranslator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ListView lv = (ListView)v;

        return data;
    }
}
