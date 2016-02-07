package com.scurab.android.anuitor.extract.component;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.Translator;

import java.util.HashMap;

/**
 * Created by JBruchanov on 07/02/2016.
 */
public class LayoutParamsExtractor extends BaseExtractor<ViewGroup.LayoutParams> {

    public LayoutParamsExtractor(Translator translator) {
        super(translator);
    }

    public HashMap<String, Object> fillValues(ViewGroup.LayoutParams lp, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        if (lp == null) {
            return data;
        }

        data.put("layout_width", getTranslator().layoutSize(lp.width));
        data.put("layout_height", getTranslator().layoutSize(lp.height));
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            data.put("LayoutParams_leftMargin", mlp.leftMargin);
            data.put("LayoutParams_topMargin", mlp.topMargin);
            data.put("LayoutParams_rightMargin", mlp.rightMargin);
            data.put("LayoutParams_bottomMargin", mlp.bottomMargin);
        }

        if (lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) lp;
            data.put("LayoutParams_layoutGravity", getTranslator().gravity(flp.gravity));
        }

        if (lp instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) lp;
            data.put("LayoutParams_weight", llp.weight);
            data.put("LayoutParams_layoutGravity", getTranslator().gravity(llp.gravity));
        }

        if (lp instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) lp;
            int[] rules = rlp.getRules();
            for (int i = 0; i < rules.length; i++) {
                int rlData = rules[i];
                if (rlData != 0) {
                    data.put(getTranslator().relativeLayoutParamRuleName(i), getTranslator().relativeLayoutParamRuleValue(rlData));
                }
            }
        }

        if (lp instanceof ViewPager.LayoutParams) {
            ViewPager.LayoutParams vlp = (ViewPager.LayoutParams) lp;
            data.put("LayoutParams_layoutGravity", getTranslator().gravity(vlp.gravity));
            data.put("LayoutParams_isDecor", vlp.isDecor);
        }
        return data;
    }
}
