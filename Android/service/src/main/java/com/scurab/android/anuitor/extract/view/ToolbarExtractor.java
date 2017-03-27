package com.scurab.android.anuitor.extract.view;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toolbar;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ToolbarExtractor extends ViewGroupExtractor {
    public ToolbarExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        final HashMap<String, Object> result = super.fillValues(v, data, parentData);

        Toolbar view = (Toolbar) v;

        result.put("ContentInsetEnd", view.getContentInsetEnd());
        result.put("ContentInsetLeft", view.getContentInsetLeft());
        result.put("ContentInsetRight", view.getContentInsetRight());
        result.put("ContentInsetStart", view.getContentInsetStart());
        result.put("Logo:", view.getLogo());
        result.put("LogoDescription", view.getLogoDescription());
        result.put("Menu:", view.getMenu());
        result.put("NavigationContentDescription", view.getNavigationContentDescription());
        result.put("NavigationIcon:", view.getNavigationIcon());
        result.put("PopupTheme", IdsHelper.getNameForId(view.getPopupTheme()));
        result.put("Subtitle", view.getSubtitle());
        result.put("Title", view.getTitle());
        result.put("HasExpandedActionView", view.hasExpandedActionView());
        result.put("IsOverflowMenuShowing", view.isOverflowMenuShowing());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result.put("ContentInsetEndWithActions", view.getContentInsetEndWithActions());
            result.put("ContentInsetStartWithNavigation", view.getContentInsetStartWithNavigation());
            result.put("CurrentContentInsetEnd", view.getCurrentContentInsetEnd());
            result.put("CurrentContentInsetLeft", view.getCurrentContentInsetLeft());
            result.put("CurrentContentInsetRight", view.getCurrentContentInsetRight());
            result.put("CurrentContentInsetStart", view.getCurrentContentInsetStart());
            result.put("OverflowIcon:", view.getOverflowIcon());
            result.put("TitleMarginBottom", view.getTitleMarginBottom());
            result.put("TitleMarginEnd", view.getTitleMarginEnd());
            result.put("TitleMarginStart", view.getTitleMarginStart());
            result.put("TitleMarginTop", view.getTitleMarginTop());
        }

        return result;
    }
}
