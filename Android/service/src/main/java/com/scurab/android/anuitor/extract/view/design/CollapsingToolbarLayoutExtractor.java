package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ViewGroupExtractor;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class CollapsingToolbarLayoutExtractor extends ViewGroupExtractor {

    public CollapsingToolbarLayoutExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        CollapsingToolbarLayout view = (CollapsingToolbarLayout) v;

        result.put("CollapsedTitleGravity", getTranslator().gravity(view.getCollapsedTitleGravity()));
        result.put("CollapsedTitleTypeface:", view.getCollapsedTitleTypeface());
        result.put("ContentScrim:", view.getContentScrim());
        result.put("CollapsedTitleGravity", getTranslator().gravity(view.getExpandedTitleGravity()));
        result.put("ExpandedTitleMarginBottom", view.getExpandedTitleMarginBottom());
        result.put("ExpandedTitleMarginEnd", view.getExpandedTitleMarginEnd());
        result.put("ExpandedTitleMarginStart", view.getExpandedTitleMarginStart());
        result.put("ExpandedTitleMarginTop", view.getExpandedTitleMarginTop());
        result.put("ExpandedTitleTypeface:", view.getExpandedTitleTypeface());
        result.put("ScrimAnimationDuration", view.getScrimAnimationDuration());
        result.put("ScrimVisibleHeightTrigger", view.getScrimVisibleHeightTrigger());
        result.put("StatusBarScrim:", view.getStatusBarScrim());
        result.put("Title", view.getTitle());
        result.put("IsTitleEnabled", view.isTitleEnabled());

        return result;
    }
}
