package com.scurab.android.anuitor.extract;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:10
 */
public class TextViewExtractor extends ViewExtractor {
    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        HashMap<String, Object> values = super.fillValues(v, data, parentData);
        TextView tv = (TextView)v;

        values.put("Text", String.valueOf(tv.getText()));
        values.put("TextSize", tv.getTextSize());
        values.put("TextColor", "#" + Integer.toHexString(tv.getCurrentTextColor()).toUpperCase());
        values.put("Gravity",getGravity(tv));
        return values;
    }

    private String getGravity(TextView tv) {
        int gravity = tv.getGravity();
        StringBuilder sb = new StringBuilder();
        if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
            sb.append("Center");
        } else {
            if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
                sb.append("CenterVertical|");
            }
            if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
                sb.append("CenterHorizontal|");
            }
            if ((gravity & Gravity.TOP) == Gravity.TOP) {
                sb.append("Top|");
            }
            if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                sb.append("Left|");
            }
            if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                sb.append("Right|");
            }
            if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                sb.append("Bottom|");
            }
            int len = sb.length();
            if (len > 0) {
                sb.setLength(len - 1);
            }
        }
        return sb.toString();
    }
}
