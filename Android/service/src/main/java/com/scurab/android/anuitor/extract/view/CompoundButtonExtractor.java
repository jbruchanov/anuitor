package com.scurab.android.anuitor.extract.view;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.CompoundButton;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 06/06/2014.
 */
public class CompoundButtonExtractor extends TextViewExtractor {

    public CompoundButtonExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data,
                                              HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CompoundButton cb = (CompoundButton) v;

        data.put("IsChecked", cb.isChecked());
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("ButtonTintMode", String.valueOf(cb.getButtonTintMode()));
        }
        return data;
    }
}
