package com.scurab.android.anuitor.extract.component;

import android.graphics.Paint;
import android.os.Build;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 05/02/2016.
 */
public class PaintExtractor extends BaseExtractor<Paint> {

    public PaintExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(Paint paint, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (paint != null && data != null) {
            data.put("Color", getStringColor(paint.getColor()));
            data.put("Ascent", paint.ascent());
            data.put("Descent", paint.descent());
            data.put("Alpha", paint.getAlpha());
            data.put("Flags", getBinaryString(paint.getFlags()));
            data.put("FontMetricsInt", paint.getFontMetricsInt().toString());
            data.put("FontSpacing", paint.getFontSpacing());
            appendClassName("MaskFilter", paint.getMaskFilter(), data);
            appendClassName("PathEffect", paint.getPathEffect(), data);
            appendClassName("Shader", paint.getShader(), data);
            data.put("StrokeCap", paint.getStrokeCap().name());
            data.put("StrokeJoin", paint.getStrokeJoin().name());
            data.put("StrokeMiter", paint.getStrokeMiter());
            data.put("StrokeWidth", paint.getStrokeWidth());
            data.put("Style", paint.getStyle().name());
            data.put("TextAlign", paint.getTextAlign().name());
            data.put("TextScaleX", paint.getTextScaleX());
            data.put("TextSize", paint.getTextSize());
            data.put("TextSkewX", paint.getTextSkewX());
            data.put("Typeface", String.valueOf(paint.getTypeface()));
            appendClassName("XFermode", paint.getXfermode(), data);
            data.put("IsAntiAlias", paint.isAntiAlias());
            data.put("IsDither", paint.isDither());
            data.put("IsFakeBoldText", paint.isFakeBoldText());
            data.put("IsFilterBitmap", paint.isFilterBitmap());
            data.put("IsLinearText", paint.isLinearText());
            data.put("IsStrikeThruText", paint.isStrikeThruText());
            data.put("IsSubpixelText", paint.isSubpixelText());
            data.put("IsUnderlineText", paint.isUnderlineText());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                data.put("Hinting", paint.getHinting());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                data.put("TextLocale", paint.getTextLocale().toString());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                data.put("FontFeatureSettings", paint.getFontFeatureSettings());
                data.put("LetterSpacing", paint.getLetterSpacing());
                data.put("IsElegantTextHeight", paint.isElegantTextHeight());
            }
        }
        return data;
    }
}
