package com.scurab.android.anuitor.extract;

import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:10
 */
public class TextViewExtractor extends ViewExtractor {
    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        HashMap<String, Object> values = super.fillValues(v, data, parentData);
        TextView tv = (TextView)v;

        values.put("Text", String.valueOf(tv.getText()));
        values.put("TextSize", tv.getTextSize());
        values.put("TextColor", "#" + Integer.toHexString(tv.getCurrentTextColor()).toUpperCase());
        values.put("Gravity",Translator.gravity(tv.getGravity()));
        return values;
    }
}
