package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ImageViewExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class FloatingActionButtonExtractor extends ImageViewExtractor {

    public FloatingActionButtonExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        FloatingActionButton view = (FloatingActionButton) v;

        result.put("BackgroundTintList", String.valueOf(view.getBackgroundTintList()));
        result.put("BackgroundTintMode", String.valueOf(view.getBackgroundTintMode()));
        result.put("CompatElevation", view.getCompatElevation());
        result.put("ContentBackground:", view.getContentBackground());
        result.put("RippleColor", getStringColor(view.getRippleColor()));
        result.put("Size", view.getSize());
        result.put("UseCompatPadding", view.getUseCompatPadding());

        return result;
    }
}
