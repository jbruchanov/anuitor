package com.scurab.android.anuitor.extract.view;

import android.support.v7.widget.CardView;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class CardViewExtractor extends ViewGroupExtractor {

    public CardViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CardView cv = (CardView) v;
        data.put("CardElevation", cv.getCardElevation());
        data.put("ContentPaddingBottom", cv.getContentPaddingBottom());
        data.put("ContentPaddingLeft", cv.getContentPaddingLeft());
        data.put("ContentPaddingRight", cv.getContentPaddingRight());
        data.put("ContentPaddingTop", cv.getContentPaddingTop());
        data.put("MaxCardElevation", cv.getMaxCardElevation());
        data.put("PreventCornerOverlap", cv.getPreventCornerOverlap());
        data.put("Radius", cv.getRadius());
        data.put("UseCompatPadding", cv.getUseCompatPadding());

        return data;
    }
}
