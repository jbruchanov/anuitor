package com.scurab.android.anuitor.extract.view;

import android.graphics.Matrix;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.ImageView;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by jbruchanov on 05/06/2014.
 */
public class ImageViewExtractor extends ViewExtractor {

    public ImageViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        ImageView iv = (ImageView) v;

        Matrix imageMatrix = iv.getImageMatrix();
        data.put("ImageMatrix", imageMatrix != null ? imageMatrix.toShortString() : null);
        data.put("ScaleType", String.valueOf(iv.getScaleType()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            data.put("OverlappingRendering", iv.hasOverlappingRendering());
            data.put("AdjustViewBounds", iv.getAdjustViewBounds());
            data.put("CropToPadding", iv.getCropToPadding());
            data.put("ImageAlpha", iv.getImageAlpha());
            data.put("MaxHeight", iv.getMaxHeight());
            data.put("MaxWidth", iv.getMaxWidth());
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("ImageTintMode", String.valueOf(iv.getImageTintMode()));
        }
        return data;
    }
}
