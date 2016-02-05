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
    public HashMap<String, Object> fillValues(Paint paint, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        if (paint != null && data != null) {
            data.put("PaintColor", getStringColor(paint.getColor()));
            data.put("PaintAscent", paint.ascent());
            data.put("PaintDescent", paint.descent());
            data.put("PaintAlpha", paint.getAlpha());
            data.put("PaintFlags", getBinaryString(paint.getFlags()));
            data.put("PaintFontMetricsInt", paint.getFontMetricsInt().toString());
            data.put("PaintFontSpacing", paint.getFontSpacing());
            appendClassName("PaintMaskFilter", paint.getMaskFilter(), data);
            appendClassName("PaintPathEffect", paint.getPathEffect(), data);
            appendClassName("PaintRasterizer", paint.getRasterizer(), data);
            appendClassName("PaintShader", paint.getShader(), data);
            data.put("PaintStrokeCap", paint.getStrokeCap().name());
            data.put("PaintStrokeJoin", paint.getStrokeJoin().name());
            data.put("PaintStrokeMiter", paint.getStrokeMiter());
            data.put("PaintStrokeWidth", paint.getStrokeWidth());
            data.put("PaintStyle", paint.getStyle().name());
            data.put("PaintTextAlign", paint.getTextAlign().name());
            data.put("PaintTextScaleX", paint.getTextScaleX());
            data.put("PaintTextSize", paint.getTextSize());
            data.put("PaintTextSkewX", paint.getTextSkewX());
            data.put("PaintTypeface", paint.getTypeface());
            appendClassName("PaintXFermode", paint.getXfermode(), data);
            data.put("PaintIsAntiAlias", paint.isAntiAlias());
            data.put("PaintIsDither", paint.isDither());
            data.put("PaintIsFakeBoldText", paint.isFakeBoldText());
            data.put("PaintIsFilterBitmap", paint.isFilterBitmap());
            data.put("PaintIsLinearText", paint.isLinearText());
            data.put("PaintIsStrikeThruText", paint.isStrikeThruText());
            data.put("PaintIsSubpixelText", paint.isSubpixelText());
            data.put("PaintIsUnderlineText", paint.isUnderlineText());
            data.put("PaintIsUnderlineText", paint.isUnderlineText());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                data.put("PaintHinting", paint.getHinting());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                data.put("PaintTextLocale", paint.getTextLocale().toString());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                data.put("PaintFontFeatureSettings", paint.getFontFeatureSettings());
                data.put("PaintLetterSpacing", paint.getLetterSpacing());
                data.put("PaintIsElegantTextHeight", paint.isElegantTextHeight());
            }
        }
        return data;
    }
}
