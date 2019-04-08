package com.scurab.android.anuitorsample.extract;

import com.scurab.android.anuitor.extract2.TextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomTextViewExtractor extends TextViewExtractor {

    @Override
    @NonNull
    protected Map<String, Object> onFillValues(Object item, Map<String, Object> data, Map<String, Object> contextData) {
        super.onFillValues(item, data, contextData);

        CustomTextView customTextView = (CustomTextView) item;
        data.put("CustomValue", customTextView.getCustomValue());
        data.put("CustomAngle", customTextView.getCustomAngle());
        return data;
    }
}
