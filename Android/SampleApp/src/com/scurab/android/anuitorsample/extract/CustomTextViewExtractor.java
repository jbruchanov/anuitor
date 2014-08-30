package com.scurab.android.anuitorsample.extract;

import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.TextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;

import java.util.HashMap;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomTextViewExtractor extends TextViewExtractor {

    public CustomTextViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View view, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        super.fillValues(view, data, contextData);

        CustomTextView customTextView = (CustomTextView) view;
        data.put("CustomValue", customTextView.getCustomValue());
        data.put("CustomAngle", customTextView.getCustomAngle());
        return data;
    }
}
