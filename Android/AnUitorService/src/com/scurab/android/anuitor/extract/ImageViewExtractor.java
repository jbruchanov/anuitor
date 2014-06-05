package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ImageViewExtractor extends ViewExtractor {

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ImageView iv = (ImageView) v;

        data.put("ImageMatrix", iv.getImageMatrix().toShortString());
        data.put("ScaleType", String.valueOf(iv.getScaleType()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            data.put("OverlappingRendering", iv.hasOverlappingRendering());
            data.put("AdjustViewBounds", iv.getAdjustViewBounds());
            data.put("CropToPadding", iv.getCropToPadding());
            data.put("ImageAlpha", iv.getImageAlpha());
            data.put("MaxHeight", iv.getMaxHeight());
            data.put("MaxWidth", iv.getMaxWidth());
        }
        return data;
    }
}
