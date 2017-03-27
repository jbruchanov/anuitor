package com.scurab.android.anuitor.extract.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 04/02/2016.
 */
public class RecyclerViewExtractor extends ViewGroupExtractor {

    public RecyclerViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    protected HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        RecyclerView rv = (RecyclerView) v;
        if (rv.getLayoutManager() != null) {
            data.put("ComputeHorizontalScrollExtent", rv.computeHorizontalScrollExtent());
            data.put("ComputeHorizontalScrollOffset", rv.computeHorizontalScrollOffset());
            data.put("ComputeHorizontalScrollRange", rv.computeHorizontalScrollRange());
            data.put("ComputeVerticalScrollExtent", rv.computeVerticalScrollExtent());
            data.put("ComputeVerticalScrollOffset", rv.computeVerticalScrollOffset());
            data.put("ComputeVerticalScrollRange", rv.computeVerticalScrollRange());
        }
        data.put("Adapter:", String.valueOf(rv.getAdapter()));
        data.put("Baseline", rv.getBaseline());
        data.put("CompatAccessibilityDelegate", String.valueOf(rv.getCompatAccessibilityDelegate()));
        data.put("ItemAnimator", String.valueOf(rv.getItemAnimator()));
        data.put("LayoutManager:", String.valueOf(rv.getLayoutManager()));
        data.put("MaxFlingVelocity", rv.getMaxFlingVelocity());
        data.put("MinFlingVelocity", rv.getMinFlingVelocity());
        data.put("RecycledViewPool", String.valueOf(rv.getRecycledViewPool()));
        data.put("ScrollState", getTranslator().scrollState(rv.getScrollState()));
        data.put("HasFixedSize", rv.hasFixedSize());
        data.put("HasNestedScrollingParent", rv.hasNestedScrollingParent());
        data.put("HasPendingAdapterUpdates", rv.hasPendingAdapterUpdates());
        data.put("IsAnimating", rv.isAnimating());
        data.put("IsAttachedToWindow", rv.isAttachedToWindow());
        data.put("IsComputingLayout", rv.isComputingLayout());
        data.put("IsLayoutFrozen", rv.isLayoutFrozen());
        data.put("IsNestedScrollingEnabled", rv.isNestedScrollingEnabled());

        return data;
    }
}
