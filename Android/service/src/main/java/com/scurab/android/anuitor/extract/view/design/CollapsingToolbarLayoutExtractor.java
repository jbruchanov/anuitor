package com.scurab.android.anuitor.extract.view.design;

import com.google.android.material.appbar.CollapsingToolbarLayout;
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
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CollapsingToolbarLayout view = (CollapsingToolbarLayout) v;

        data.put("CollapsedTitleGravity", getTranslator().gravity(view.getCollapsedTitleGravity()));
        data.put("CollapsedTitleTypeface:", view.getCollapsedTitleTypeface());
        data.put("ContentScrim:", view.getContentScrim());
        data.put("CollapsedTitleGravity", getTranslator().gravity(view.getExpandedTitleGravity()));
        data.put("ExpandedTitleMarginBottom", view.getExpandedTitleMarginBottom());
        data.put("ExpandedTitleMarginEnd", view.getExpandedTitleMarginEnd());
        data.put("ExpandedTitleMarginStart", view.getExpandedTitleMarginStart());
        data.put("ExpandedTitleMarginTop", view.getExpandedTitleMarginTop());
        data.put("ExpandedTitleTypeface:", view.getExpandedTitleTypeface());
        data.put("ScrimAnimationDuration", view.getScrimAnimationDuration());
        data.put("ScrimVisibleHeightTrigger", view.getScrimVisibleHeightTrigger());
        data.put("StatusBarScrim:", view.getStatusBarScrim());
        data.put("Title", view.getTitle());
        data.put("IsTitleEnabled", view.isTitleEnabled());

        return data;
    }
}
