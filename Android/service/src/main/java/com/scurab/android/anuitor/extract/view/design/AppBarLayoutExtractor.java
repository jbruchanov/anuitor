package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.LinearLayoutExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class AppBarLayoutExtractor extends LinearLayoutExtractor {

    public AppBarLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        AppBarLayout view = (AppBarLayout) v;
        data.put("TotalScrollRange", view.getTotalScrollRange());

        return data;
    }
}
