package com.scurab.android.anuitor.extract.view;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:10
 */
public class TextViewExtractor extends ViewExtractor {

    public TextViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        HashMap<String, Object> values = super.fillValues(v, data, parentData);
        TextView tv = (TextView)v;
        Translator translator = getTranslator();

        values.put("Text", String.valueOf(tv.getText()));
        values.put("TextSize", tv.getTextSize());
        values.put("TextColor", getStringColor(tv.getCurrentTextColor()));
        values.put("HintTextColor", getStringColor(tv.getCurrentHintTextColor()));
        values.put("LinksClickable", tv.getLinksClickable());
        values.put("MovementMethod", String.valueOf(tv.getMovementMethod()));
        values.put("Gravity", translator.gravity(tv.getGravity()));
        values.put("AutoLinkMask", translator.linkMask(tv.getAutoLinkMask()));
        values.put("Ellipsize", String.valueOf(tv.getEllipsize()));
        values.put("InputType", translator.inputType(tv.getInputType()));
        values.put("TextScaleX", tv.getTextScaleX());
        values.put("CompoundDrawablePadding", tv.getCompoundDrawablePadding());
        values.put("CompoundPaddingLeft", tv.getCompoundPaddingLeft());
        values.put("CompoundPaddingRight", tv.getCompoundPaddingRight());
        values.put("CompoundPaddingTop", tv.getCompoundPaddingTop());
        values.put("CompoundPaddingBottom", tv.getCompoundPaddingBottom());
        Drawable[] compoundDrawables = tv.getCompoundDrawables();
        if (compoundDrawables != null && compoundDrawables.length >= 4) {
            values.put("CompoundDrawableLeft:", String.valueOf(tv.getCompoundDrawables()[0]));
            values.put("CompoundDrawableTop:", String.valueOf(tv.getCompoundDrawables()[1]));
            values.put("CompoundDrawableRight:", String.valueOf(tv.getCompoundDrawables()[2]));
            values.put("CompoundDrawableBottom:", String.valueOf(tv.getCompoundDrawables()[3]));
        }

        values.put("Paint:", tv.getPaint());

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
            values.put("ShadowColor", getStringColor(tv.getShadowColor()));
            values.put("ShadowDX", tv.getShadowDx());
            values.put("ShadowDY", tv.getShadowDy());
            values.put("ShadowRadius", tv.getShadowRadius());
            values.put("IsCursorVisible", tv.isCursorVisible());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Drawable[] compoundDrawablesRelative = tv.getCompoundDrawablesRelative();
            if (compoundDrawablesRelative != null && compoundDrawablesRelative.length >= 4) {
                values.put("CompoundDrawableRelativeStart:", String.valueOf(tv.getCompoundDrawablesRelative()[0]));
                values.put("CompoundDrawableRelativeTop:", String.valueOf(tv.getCompoundDrawablesRelative()[1]));
                values.put("CompoundDrawableRelativeEnd:", String.valueOf(tv.getCompoundDrawablesRelative()[2]));
                values.put("CompoundDrawableRelativeBottom:", String.valueOf(tv.getCompoundDrawablesRelative()[3]));
            }
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            values.put("FontFeatureSettings", tv.getFontFeatureSettings());
            values.put("LetterSpacing", tv.getLetterSpacing());
            values.put("ShowSoftInputOnFocus", tv.getShowSoftInputOnFocus());
        }

        return values;
    }
}
