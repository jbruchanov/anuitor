package com.scurab.android.anuitorsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class SubCustomTextView extends CustomTextView {

    private String mCustomValue = "CustomValue";

    public SubCustomTextView(Context context) {
        super(context);
    }

    public SubCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
