package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.widget.CheckedTextView;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class CheckedTextViewExtractor extends TextViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);
        CheckedTextView ctv = (CheckedTextView) v;
        data.put("IsChecked", ctv.isChecked());
        return data;
    }
}
