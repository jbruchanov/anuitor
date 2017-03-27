package com.scurab.android.anuitor.extract.view.design;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.ViewGroup;

import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.component.LayoutParamsExtractor;
import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.util.HashMap;

/**
 * Created by JBruchanov on 13/03/2017.
 */

public class DesignLayoutParamsExtractor extends LayoutParamsExtractor {

    public DesignLayoutParamsExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(ViewGroup.LayoutParams lp, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(lp, data, parentData);

        if (lp instanceof AppBarLayout.LayoutParams) {
            AppBarLayout.LayoutParams alp = (AppBarLayout.LayoutParams) lp;
            data.put("LayoutParams_ScrollFlags", getTranslator().appBarLayoutScrollFlags(alp.getScrollFlags()));
            data.put("LayoutParams_ScrollInterpolator", alp.getScrollInterpolator() != null ? alp.getScrollInterpolator().getClass().getName() : "null");
        }

        if (lp instanceof CollapsingToolbarLayout.LayoutParams) {
            CollapsingToolbarLayout.LayoutParams clp = (CollapsingToolbarLayout.LayoutParams) lp;
            data.put("LayoutParams_CollapseMode", getTranslator().collapseMode(clp.getCollapseMode()));
            data.put("LayoutParams_ParallaxMultiplier", clp.getParallaxMultiplier());

        }

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) lp;
            data.put("LayoutParams_AnchorId", IdsHelper.getNameForId(clp.getAnchorId()));
            data.put("LayoutParams_Behavior", clp.getBehavior() != null ? clp.getBehavior().getClass().getName() : "null");

        }
        return data;
    }

    public static void registerExtractors(Translator translator) {
        DesignLayoutParamsExtractor extractor = new DesignLayoutParamsExtractor(translator);
        DetailExtractor.registerExtractor(AppBarLayout.LayoutParams.class, extractor);
        DetailExtractor.registerExtractor(CollapsingToolbarLayout.LayoutParams.class, extractor);
        DetailExtractor.registerExtractor(CoordinatorLayout.LayoutParams.class, extractor);
    }
}
