package com.scurab.android.anuitor.extract.component;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 14/02/2016.
 */
public class BitmapDrawableExtractor extends DrawableExtractor {
    public BitmapDrawableExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(Drawable d, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        super.fillValues(d, data, contextData);

        BitmapDrawable bd = (BitmapDrawable) d;
        final Bitmap bitmap = bd.getBitmap();
        data.put("Bitmap", String.valueOf(bitmap));
        if (bitmap != null) {
            final BaseExtractor<Bitmap> extractor = (BaseExtractor<Bitmap>) DetailExtractor.getExtractor(bitmap.getClass());
            if (extractor != null) {
                extractor.fillValues(bitmap, data, contextData);
            }
        }
        data.put("Gravity", getTranslator().gravity(bd.getGravity()));
        data.put("Paint", String.valueOf(bd.getPaint()));
        data.put("TileModeX", String.valueOf(bd.getTileModeX()));
        data.put("TileModeY", String.valueOf(bd.getTileModeY()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("HasAntiAlias", bd.hasAntiAlias());
            data.put("HasMipMap", bd.hasMipMap());
        }
        return data;
    }
}
