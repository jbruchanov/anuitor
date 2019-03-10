package com.scurab.android.anuitor.extract.view;

import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class SlidingPaneLayoutExtractor extends ViewGroupExtractor {

    public SlidingPaneLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        SlidingPaneLayout sdp = (SlidingPaneLayout) v;
        data.put("CanSlide", sdp.canSlide());
        data.put("IsSlidable", sdp.isSlideable());
        data.put("CoveredFadeColor", getStringColor(sdp.getCoveredFadeColor()));
        data.put("ParallaxDistance", sdp.getParallaxDistance());
        data.put("IsOpen", sdp.isOpen());
        return data;
    }
}
