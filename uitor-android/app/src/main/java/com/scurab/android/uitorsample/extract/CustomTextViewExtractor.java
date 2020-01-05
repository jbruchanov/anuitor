package com.scurab.android.uitorsample.extract;

import com.scurab.android.uitor.extract2.ExtractingContext;
import com.scurab.android.uitor.extract2.TextViewExtractor;
import com.scurab.android.uitorsample.widget.CustomTextView;

import org.jetbrains.annotations.NotNull;

public class CustomTextViewExtractor extends TextViewExtractor {

    @Override
    protected void onFillValues(@NotNull Object item, @NotNull ExtractingContext context) {
        super.onFillValues(item, context);
        CustomTextView customTextView = (CustomTextView) item;
        context.getData().put("CustomValue", customTextView.getCustomValue());
        context.getData().put("CustomAngle", customTextView.getCustomAngle());
    }
}
