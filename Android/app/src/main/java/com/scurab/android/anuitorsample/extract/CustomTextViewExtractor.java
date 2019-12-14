package com.scurab.android.anuitorsample.extract;

import com.scurab.android.uitor.extract2.ExtractingContext;
import com.scurab.android.uitor.extract2.TextViewExtractor;
import com.scurab.android.anuitorsample.widget.CustomTextView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomTextViewExtractor extends TextViewExtractor {

    @Override
    protected void onFillValues(@NotNull Object item, @NotNull ExtractingContext context) {
        super.onFillValues(item, context);
        CustomTextView customTextView = (CustomTextView) item;
        context.getData().put("CustomValue", customTextView.getCustomValue());
        context.getData().put("CustomAngle", customTextView.getCustomAngle());
    }
}
