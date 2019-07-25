package com.scurab.android.anuitorsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

import com.scurab.android.anuitor.hierarchy.ExportField;
import com.scurab.android.anuitor.hierarchy.ExportView;

/**
 * Created by jbruchanov on 01/07/2014.
 */
@ExportView //flag view as ExportView
public class CustomButton extends AppCompatButton {

    @ExportField("CustomValue1") //flag any primitive value by @ExportField and provide name
    private int mCustomValue1 = 1234;
    @ExportField("CustomValue2")
    private String mCustomValue2 = "CustomValue2";

    public CustomButton(Context context) {
        super(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setScaleX(0.5f);
        setScaleY(0.5f);
    }
}
