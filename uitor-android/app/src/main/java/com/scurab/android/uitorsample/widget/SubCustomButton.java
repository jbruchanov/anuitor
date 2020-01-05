package com.scurab.android.uitorsample.widget;

import android.content.Context;
import android.util.AttributeSet;

public class SubCustomButton extends CustomButton {

    public SubCustomButton(Context context) {
        super(context);
    }

    public SubCustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubCustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        setScaleX(1.25f);
        setScaleY(1.25f);
    }
}
