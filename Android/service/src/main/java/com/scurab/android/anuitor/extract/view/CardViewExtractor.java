package com.scurab.android.anuitor.extract.view;

import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class CardViewExtractor extends ViewGroupExtractor {

    public CardViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        return super.fillValues(v, data, parentData);
    }
}
