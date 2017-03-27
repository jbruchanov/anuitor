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
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        FloatingActionButton view = (FloatingActionButton) v;

        data.put("BackgroundTintList", String.valueOf(view.getBackgroundTintList()));
        data.put("BackgroundTintMode", String.valueOf(view.getBackgroundTintMode()));
        data.put("CompatElevation", view.getCompatElevation());
        data.put("ContentBackground:", view.getContentBackground());
        data.put("RippleColor", getStringColor(view.getRippleColor()));
        data.put("Size", view.getSize());
        data.put("UseCompatPadding", view.getUseCompatPadding());

        return data;
    }
}
