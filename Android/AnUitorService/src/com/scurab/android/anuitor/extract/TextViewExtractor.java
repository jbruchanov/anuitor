package com.scurab.android.anuitor.extract;

import android.os.Build;
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
        values.put("HintTextColor", "#" + Integer.toHexString(tv.getCurrentHintTextColor()).toUpperCase());
        values.put("LinksClickable", tv.getLinksClickable());
        values.put("MovementMethod", String.valueOf(tv.getMovementMethod()));
        values.put("Gravity",Translator.gravity(tv.getGravity()));
        values.put("AutoLinkMask",Translator.linkMask(tv.getAutoLinkMask()));
        values.put("Ellipsize", String.valueOf(tv.getEllipsize()));
        values.put("InputType", Translator.inputType(tv.getInputType()));
        values.put("TextScaleX", tv.getTextScaleX());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            values.put("IsTextSelectable", tv.isTextSelectable());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            values.put("IsSuggestion", tv.isSuggestionsEnabled());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            values.put("LineSpacingExtra", tv.getLineSpacingExtra());
            values.put("LineSpacingMultiplier", tv.getLineSpacingMultiplier());
            values.put("MaxLines", tv.getMaxLines());
            values.put("ShadowColor","#" + Integer.toHexString(tv.getShadowColor()).toUpperCase());
            values.put("ShadowDX", tv.getShadowDx());
            values.put("ShadowDX", tv.getShadowDy());
            values.put("ShadowRadius", tv.getShadowRadius());
            values.put("IsCursorVisible", tv.isCursorVisible());
        }

        return values;
    }
}
