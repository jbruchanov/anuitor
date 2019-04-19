package com.scurab.android.anuitorsample.extract;

import com.scurab.android.anuitor.extract2.TextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomTextViewExtractor extends TextViewExtractor {

    @Override
    @NonNull
    protected Map<String, Object> onFillValues(@NotNull Object item, @NotNull Map<String, Object> data, @Nullable Map<String, Object> contextData, int depth) {
        super.onFillValues(item, data, contextData, depth);

        CustomTextView customTextView = (CustomTextView) item;
        data.put("CustomValue", customTextView.getCustomValue());
        data.put("CustomAngle", customTextView.getCustomAngle());
        return data;
    }
}
