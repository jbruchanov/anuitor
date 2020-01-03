package com.scurab.android.uitorsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.RotateAnimation;

/**
 * Created by jbruchanov on 01/07/2014.
 */
public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mAngle;

    private String mCustomValue = "CustomValue";

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAngle = 12;
        RotateAnimation ra = new RotateAnimation(0, mAngle);
        ra.setDuration(1);
        ra.setFillAfter(true);
        ra.setFillEnabled(true);
        setAnimation(ra);
    }

    public int getCustomAngle() {
        return mAngle;
    }

    public String getCustomValue() {
        return mCustomValue;
    }
}
