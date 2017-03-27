package com.scurab.android.anuitor.extract.view;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.TextView;

import com.scurab.android.anuitor.extract.Translator;

import java.util.Arrays;
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
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        HashMap<String, Object> values = super.fillValues(v, data, parentData);
        TextView tv = (TextView) v;
        Translator translator = getTranslator();

        values.put("Text", escapeString(tv.getText().toString()));
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
            data.put("CustomSelectionActionModeCallback", String.valueOf(tv.getCustomSelectionActionModeCallback()));
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

            data.put("HighlightColor", getStringColor(tv.getHighlightColor()));
            data.put("IncludeFontPadding", tv.getIncludeFontPadding());
            data.put("MarqueeRepeatLimit", tv.getMarqueeRepeatLimit());
            data.put("MaxEms", tv.getMaxEms());
            data.put("MaxHeight", tv.getMaxHeight());
            data.put("MaxWidth", tv.getMaxWidth());
            data.put("MinEms", tv.getMinEms());
            data.put("MinHeight", tv.getMinHeight());
            data.put("MinLines", tv.getMinLines());
            data.put("MinWidth", tv.getMinWidth());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Drawable[] compoundDrawablesRelative = tv.getCompoundDrawablesRelative();
            if (compoundDrawablesRelative != null && compoundDrawablesRelative.length >= 4) {
                values.put("CompoundDrawableRelativeStart:", String.valueOf(tv.getCompoundDrawablesRelative()[0]));
                values.put("CompoundDrawableRelativeTop:", String.valueOf(tv.getCompoundDrawablesRelative()[1]));
                values.put("CompoundDrawableRelativeEnd:", String.valueOf(tv.getCompoundDrawablesRelative()[2]));
                values.put("CompoundDrawableRelativeBottom:", String.valueOf(tv.getCompoundDrawablesRelative()[3]));
            }
            data.put("CompoundPaddingEnd", tv.getCompoundPaddingEnd());
            data.put("CompoundPaddingStart", tv.getCompoundPaddingStart());
            data.put("TextLocale", String.valueOf(tv.getTextLocale()));
            data.put("TotalPaddingEnd", tv.getTotalPaddingEnd());
            data.put("TotalPaddingStart", tv.getTotalPaddingStart());
            data.put("HasOverlappingRendering", tv.hasOverlappingRendering());
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            values.put("FontFeatureSettings", tv.getFontFeatureSettings());
            values.put("LetterSpacing", tv.getLetterSpacing());
            values.put("ShowSoftInputOnFocus", tv.getShowSoftInputOnFocus());
        }

        data.put("DidTouchFocusSelect", tv.didTouchFocusSelect());
        data.put("Baseline", tv.getBaseline());
        data.put("EditableText", String.valueOf(tv.getEditableText()));
        data.put("Error", String.valueOf(tv.getError()));
        try {
            data.put("ExtendedPaddingBottom", tv.getExtendedPaddingBottom());
            data.put("ExtendedPaddingTop", tv.getExtendedPaddingTop());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        data.put("FiltersCount", tv.getFilters() != null ? tv.getFilters().length : 0);
        data.put("FreezesText", tv.getFreezesText());
        data.put("Hint", String.valueOf(tv.getHint()));
        data.put("HintTextColors:", String.valueOf(tv.getHintTextColors()));
        data.put("ImeActionId", tv.getImeActionId());
        data.put("ImeActionLabel", String.valueOf(tv.getImeActionLabel()));
        data.put("ImeOptions", tv.getImeOptions());
        data.put("KeyListener", String.valueOf(tv.getKeyListener()));
        data.put("Layout:", String.valueOf(tv.getLayout()));
        data.put("LineCount", tv.getLineCount());
        data.put("LineHeight", tv.getLineHeight());
        data.put("LinkTextColors", String.valueOf(tv.getLinkTextColors()));
        data.put("PaintFlags", tv.getPaintFlags());
        data.put("PrivateImeOptions", String.valueOf(tv.getPrivateImeOptions()));
        data.put("SelectionEnd", tv.getSelectionEnd());
        data.put("SelectionStart", tv.getSelectionStart());
        data.put("TextColors", String.valueOf(tv.getTextColors()));
        try {
            data.put("TotalPaddingBottom", tv.getTotalPaddingBottom());
            data.put("TotalPaddingLeft", tv.getTotalPaddingLeft());
            data.put("TotalPaddingRight", tv.getTotalPaddingRight());
            data.put("TotalPaddingTop", tv.getTotalPaddingTop());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        data.put("TransformationMethod", String.valueOf(tv.getTransformationMethod()));
        data.put("Typeface", String.valueOf(tv.getTypeface()));
        data.put("Urls", tv.getUrls() != null ? Arrays.toString(tv.getUrls()) : "null");
        data.put("HasSelection", tv.hasSelection());
        data.put("IsInputMethodTar", tv.isInputMethodTarget());
        data.put("Length", tv.length());
        data.put("MoveCursorToVisibleOffset", tv.moveCursorToVisibleOffset());
        return values;
    }
}
