package com.scurab.android.anuitor.extract.view;

import android.view.View;
import android.widget.CompoundButton;

import java.util.HashMap;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class CompoundButtonExtractor extends TextViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data,
                                              HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CompoundButton cb = (CompoundButton) v;

        data.put("IsChecked", cb.isChecked());
        return data;
    }
}
