package com.scurab.android.anuitor.extract.view;

import android.support.v7.widget.RecyclerView;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class RecyclerViewExtractor extends BaseExtractor<RecyclerView> {

    public RecyclerViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(RecyclerView recyclerView, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        return null;
    }
}
