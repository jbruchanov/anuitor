package com.scurab.android.anuitor.extract.component;

import android.graphics.drawable.Drawable;
import android.os.Build;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 06/02/2016.
 */
public class DrawableExtractor extends BaseExtractor<Drawable> {

    public DrawableExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(Drawable d, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        data.put("Bounds", d.getBounds().toShortString());
        data.put("ChangingConfigurations", getBinaryString(d.getChangingConfigurations()));
        data.put("IntrinsicWidth", d.getIntrinsicWidth());
        data.put("IntrinsicHeight", d.getIntrinsicHeight());
        data.put("Level", d.getLevel());
        data.put("MinimumWidth", d.getMinimumWidth());
        data.put("MinimumHeight", d.getMinimumHeight());
        data.put("Opacity", d.getOpacity());
        data.put("State", getTranslator().drawableStates(d.getState()));
        data.put("TransparentRegion", String.valueOf(d.getTransparentRegion()));
        data.put("IsStateful", d.isStateful());
        data.put("IsVisible", d.isVisible());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            data.put("Callback", String.valueOf(d.getCallback()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            data.put("Alpha", d.getAlpha());
            data.put("AutoMirrored", d.isAutoMirrored());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            data.put("CanApplyTheme", d.canApplyTheme());
            data.put("ColorFilter", String.valueOf(d.getColorFilter()));
            data.put("DirtyBounds", d.getDirtyBounds().toShortString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            data.put("LayoutDirection", getTranslator().layoutDirection(d.getLayoutDirection()));
            data.put("IsFilterBitmap", d.isFilterBitmap());
        }
        return data;
    }
}
