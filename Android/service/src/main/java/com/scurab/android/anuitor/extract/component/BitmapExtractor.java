package com.scurab.android.anuitor.extract.component;

import android.graphics.Bitmap;
import android.os.Build;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by JBruchanov on 14/02/2016.
 */
public class BitmapExtractor extends BaseExtractor<Bitmap> {

    public BitmapExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(Bitmap b, HashMap<String, Object> data, HashMap<String, Object> contextData) {

        data.put("DescribeContents", b.describeContents());
        data.put("Config", String.valueOf(b.getConfig()));
        data.put("Density", b.getDensity());
        data.put("Height", b.getHeight());
        data.put("NinePatchChunk", Arrays.toString(b.getNinePatchChunk()));
        data.put("RowBytes", b.getRowBytes());
        data.put("Width", b.getWidth());
        data.put("HasAlpha", b.hasAlpha());
        data.put("IsMutable", b.isMutable());
        data.put("IsRecycled", b.isRecycled());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            data.put("AllocationByteCount", b.getAllocationByteCount());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            data.put("ByteCount", b.getByteCount());
            data.put("GenerationId", b.getGenerationId());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("HasMipMap", b.hasMipMap());
            data.put("IsPremultiplied", b.isPremultiplied());
        }

        return data;
    }
}
