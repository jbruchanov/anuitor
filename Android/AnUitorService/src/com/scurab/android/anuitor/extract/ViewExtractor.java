package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.scurab.android.anuitor.hierarchy.ExportField;
import com.scurab.android.anuitor.hierarchy.ExportView;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ViewExtractor {
    private static final int[] POSITION = new int[2];

    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        data.put("Type", String.valueOf(v.getClass().getCanonicalName()));

        data.put("Left", v.getLeft());
        data.put("Top", v.getTop());
        data.put("Right", v.getRight());
        data.put("Bottom", v.getBottom());
        data.put("Width", v.getWidth());
        data.put("Height", v.getHeight());
        data.put("PaddingLeft", v.getPaddingLeft());
        data.put("PaddingTop", v.getPaddingTop());
        data.put("PaddingRight", v.getPaddingRight());
        data.put("PaddingBottom", v.getPaddingBottom());
        data.put("_Visibility", v.getVisibility());
        data.put("Visibility", Translator.visibility(v.getVisibility()));

        boolean isViewGroup = (v instanceof ViewGroup) && !ViewDetailExtractor.VIEWGROUP_IGNORE.contains(v.getClass());
        Integer isParentVisible = parentData == null ? View.VISIBLE : (Integer)parentData.get("_Visibility");
        boolean isVisible = v.getVisibility() == View.VISIBLE && (isParentVisible == null || View.VISIBLE == isParentVisible);
        boolean hasBackground = v.getBackground() != null;
        boolean shouldRender = isVisible && ((isViewGroup && hasBackground) || !isViewGroup);
        data.put("_RenderViewContent", shouldRender);

        //TODO:remove later
        data.put("RenderViewContent", shouldRender);


        fillLayoutParams(v, data, parentData);
        fillScale(v, data, parentData);
        fillLocationValues(v, data, parentData);

        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    public HashMap<String, Object> fillLayoutParams(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();

        data.put("LayoutParams", lp != null ? lp.getClass().getCanonicalName() : "null");

        if (lp == null) {
            return data;
        }

        data.put("layout_width", Translator.layoutSize(lp.width));
        data.put("layout_height", Translator.layoutSize(lp.height));
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            data.put("layoutParams_leftMargin", mlp.leftMargin);
            data.put("layoutParams_topMargin", mlp.topMargin);
            data.put("layoutParams_rightMargin", mlp.rightMargin);
            data.put("layoutParams_bottomMargin", mlp.bottomMargin);

        }

        if (lp instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) lp;
            int[] rules = rlp.getRules();
            for (int i = 0; i < rules.length; i++) {
                int rlData = rules[i];
                if (rlData != 0) {
                    data.put(Translator.relativeLayoutParamRuleName(i), Translator.relativeLayoutParamRuleValue(rlData));
                }
            }
        }
        return data;
    }

    public static HashMap<String, Object> fillLocationValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        v.getLocationOnScreen(POSITION);
        data.put("LocationScreenX", POSITION[0]);
        data.put("LocationScreenY", POSITION[1]);
        POSITION[0] = POSITION[1] = 0;

        v.getLocationInWindow(POSITION);
        data.put("LocationWindowX", POSITION[0]);
        data.put("LocationWindowY", POSITION[1]);
        return data;
    }

    /**
     * Fill fields for ScaleX, ScaleY values
     *
     * @param v
     * @param data
     * @param parentData
     * @return
     */
    public static HashMap<String, Object> fillScale(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            data.put("ScaleX", v.getScaleX());
            data.put("ScaleY", v.getScaleY());

            float fx = 1f;
            float fy = 1f;
            if (parentData != null) {
                Float ofx = (Float) parentData.get("_ScaleX");
                Float ofy = (Float) parentData.get("_ScaleY");
                if (ofx != null || ofy != null) {
                    fx = ofx;
                    fy = ofy;
                }
            }

            data.put("_ScaleX", v.getScaleX() * fx);
            data.put("_ScaleY", v.getScaleY() * fy);
        }
        return data;
    }

    /**
     * Fill annotated values
     *
     * @param v
     * @param data
     * @return
     */
    public static HashMap<String, Object> fillAnnotatedValues(View v, HashMap<String, Object> data) {
        Field[] fields = v.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ExportField annotation = field.getAnnotation(ExportField.class);
            if (annotation != null) {
                try {
                    Object o = field.get(v);
                    data.put(annotation.value(), o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private static boolean isExportView(View v) {
        return v.getClass().getAnnotation(ExportView.class) != null;
    }
}
