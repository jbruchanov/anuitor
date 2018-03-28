package com.scurab.android.anuitor.extract.view;

import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.RenderAreaWrapper;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.ExportField;
import com.scurab.android.anuitor.hierarchy.ExportView;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.ViewOverlayReflector;
import com.scurab.android.anuitor.reflect.ViewReflector;
import com.scurab.android.anuitor.tools.Executor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 14:20
 */
public class ViewExtractor extends BaseExtractor<View> {
    private static final int[] POSITION = new int[2];

    /**
     * Those values must be present in every View related dataset<br/>
     * ScaleXY, _Visibility, _Position, LocationScreenX, Height, Width as {@link Number}<br/>
     * _RenderViewContent as {@link java.lang.Boolean}<br/>
     * Type as {@link java.lang.String}
     *
     */
    public static final String[] MANDATORY_KEYS = {"_ScaleX", "_ScaleY", "_Visibility", "_RenderViewContent", "Position", "LocationScreenX", "LocationScreenY", "Height", "Width", "Type"};

    public ViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> onFillValues(View view, HashMap<String, Object> data, HashMap<String, Object> contextData) {
        final HashMap<String, Object> result = super.onFillValues(view, data, contextData);
        if (result.containsKey(KEY_ERROR_CLASS)) {
            fillMandatoryFields(view, data, contextData);
        }
        return result;
    }

    protected HashMap<String, Object> fillValues(final View v, final HashMap<String, Object> data, final HashMap<String, Object> parentData) {
        /*
         * needs to be run sometimes in main thread, specifically when in getPadding... -> resolvePadding() is called
         * If crashed internally, new fragments were not visible (put app into background and open it again will solve the issue)
         */
        Executor.runInMainThreadBlocking(new Runnable() {
            @Override
            public void run() {
                fillValuesImpl(v, data, parentData);
            }
        });
        return data;
    }

    private HashMap<String, Object> fillValuesImpl(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        Translator translator = getTranslator();
        ViewReflector reflector = new ViewReflector(v);
        fillMandatoryFields(v, data, parentData);
        try {
            data.put("Baseline", v.getBaseline());
        } catch (Exception e) {
            data.put("Baseline", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        data.put("Background:", String.valueOf(v.getBackground()));
        data.put("BackgroundResourceId", reflector.getBackgroundResourceId());
        data.put("Context:", String.valueOf(v.getContext()));
        data.put("ContentDescription", String.valueOf(v.getContentDescription()));
        data.put("IsClickable", v.isClickable());
        data.put("IsLongClickable", v.isLongClickable());
        data.put("IsEnabled", v.isEnabled());
        data.put("IsFocusable", v.isFocusable());
        data.put("IsFocusableInTouchMode", v.isFocusableInTouchMode());
        data.put("IsDuplicateParentState", v.isDuplicateParentStateEnabled());
        data.put("IsShown", v.isShown());
        data.put("KeepScreenOn", v.getKeepScreenOn());
        data.put("Left", v.getLeft());
        data.put("Top", v.getTop());
        data.put("Right", v.getRight());
        data.put("Bottom", v.getBottom());
        data.put("PaddingLeft", v.getPaddingLeft());
        data.put("PaddingTop", v.getPaddingTop());
        data.put("PaddingRight", v.getPaddingRight());
        data.put("PaddingBottom", v.getPaddingBottom());
        data.put("_Visibility", v.getVisibility());
        data.put("Visibility", translator.visibility(v.getVisibility()));

        data.put("NextFocusDownId", IdsHelper.getNameForId(v.getNextFocusDownId()));
        data.put("NextFocusLeftId", IdsHelper.getNameForId(v.getNextFocusLeftId()));
        data.put("NextFocusRightId", IdsHelper.getNameForId(v.getNextFocusRightId()));
        data.put("NextFocusUpId", IdsHelper.getNameForId(v.getNextFocusUpId()));

        data.put("ScrollX", v.getScrollX());
        data.put("ScrollY", v.getScrollY());
        data.put("Tag:", String.valueOf(v.getTag()));
        data.put("Tags:", String.valueOf(reflector.getKeyedTags()));
        data.put("HasFocus", v.hasFocus());
        data.put("HasFocusable", v.hasFocusable());
        data.put("IsOpaque", v.isOpaque());
        data.put("IsPressed", v.isPressed());
        data.put("IsSelected", v.isSelected());
        data.put("IsSoundEffects", v.isSoundEffectsEnabled());
        data.put("WillNotDraw", v.willNotDraw());
        data.put("WillNotCacheDrawing", v.willNotCacheDrawing());

        final Rect rect = new Rect();
        v.getHitRect(rect);
        data.put("HitRect", rect.toShortString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            data.put("Alpha", v.getAlpha());
            data.put("Rotation", v.getRotation());
            data.put("RotationX", v.getRotationX());
            data.put("RotationY", v.getRotationY());
            data.put("PivotX", v.getPivotX());
            data.put("PivotY", v.getPivotY());
            data.put("TranslationX", v.getTranslationX());
            data.put("TranslationY", v.getTranslationY());
            data.put("IsHWAccelerated", v.isHardwareAccelerated());
            data.put("LayerType", translator.layerType(v.getLayerType()));
            data.put("Matrix", v.getMatrix().toShortString());
            data.put("NextFocusForwardId", IdsHelper.getNameForId(v.getNextFocusForwardId()));
            data.put("X", v.getX());
            data.put("Y", v.getY());
            data.put("MeasuredHeightAndState", v.getMeasuredHeightAndState());
            data.put("MeasuredState", v.getMeasuredState());
            data.put("MeasuredWidthAndState", v.getMeasuredWidthAndState());
            data.put("SystemUiVisibility", v.getSystemUiVisibility());//11
            data.put("VerticalScrollbarPosition", v.getVerticalScrollbarPosition());//11
            data.put("IsActivated", v.isActivated());//11
            data.put("IsDirty", v.isDirty());//11
            data.put("IsSaveFromParentEnabled", v.isSaveFromParentEnabled());//11
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            data.put("IsHovered", v.isHovered());//14
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            data.put("HasOnClickListener", v.hasOnClickListeners());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (v.getResources() != null) {//viewstub doesn't have to have res
                data.put("CameraDistance", v.getCameraDistance());
            }
            data.put("IsImportantForA11Y", translator.importantForA11Y(v.getImportantForAccessibility()));
            data.put("MinWidth", v.getMinimumWidth());
            data.put("MinHeight", v.getMinimumHeight());
            data.put("AccessibilityNodeProvider", String.valueOf(v.getAccessibilityNodeProvider()));
            data.put("FitsSystemWindows", v.getFitsSystemWindows());
            data.put("ScrollBarDefaultDelayBeforeFade", v.getScrollBarDefaultDelayBeforeFade());
            data.put("ScrollBarFadeDuration", v.getScrollBarFadeDuration());
            data.put("ScrollBarSize", v.getScrollBarSize());
            data.put("IsScrollContainer", v.isScrollContainer());
            data.put("ParentForAccessibility", String.valueOf(v.getParentForAccessibility()));//16
            data.put("WindowSystemUiVisibility", v.getWindowSystemUiVisibility());//16;
            data.put("HasOverlappingRendering", v.hasOverlappingRendering());//16
            data.put("HasTransientState", v.hasTransientState());//16
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            data.put("LabelFor", IdsHelper.getNameForId(v.getLabelFor()));
            data.put("LayerDirection", translator.layoutDirection(v.getLayoutDirection()));
            data.put("Display:", String.valueOf(v.getDisplay()));
            data.put("PaddingEnd", v.getPaddingEnd());
            data.put("PaddingStart", v.getPaddingStart());
            data.put("TextAlignment", v.getTextAlignment());//17
            data.put("TextDirection", v.getTextDirection());//17
            data.put("IsPaddingRelative", v.isPaddingRelative());//17
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            data.put("ClipBounds", String.valueOf(v.getClipBounds()));
            final ViewOverlay overlay = v.getOverlay();
            final ViewOverlayReflector viewOverlayReflector = new ViewOverlayReflector(overlay);
            data.put("Overlay", String.valueOf(overlay));
            data.put("OverlayChildCount", viewOverlayReflector.getChildCount());
            if (viewOverlayReflector.getChildCount() > 0) {
                data.put("OverlayViewGroup:", String.valueOf(viewOverlayReflector.getOverlayViewGroup()));
            }
            data.put("OverlayDrawableCount", viewOverlayReflector.getOverlayDrawablesCount());
            if (viewOverlayReflector.getOverlayDrawablesCount() > 0) {
                data.put("OverlayDrawables:", String.valueOf(viewOverlayReflector.getOverlayDrawables()));
            }
            data.put("WindowId:", String.valueOf(v.getWindowId()));//18
            data.put("IsInLayout", v.isInLayout());//18
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            data.put("CanResolveLayoutDirection", v.canResolveLayoutDirection());
            data.put("CanResolveTextAlignment", v.canResolveTextAlignment());
            data.put("CanResolveTextDirection", v.canResolveTextDirection());
            data.put("AccessibilityLiveRegion", v.getAccessibilityLiveRegion());
            data.put("IsTextAlignmentResolved", v.isTextAlignmentResolved());
            data.put("IsTextDirectionResolved", v.isTextDirectionResolved());
            data.put("IsAttachedToWindow", v.isAttachedToWindow());//19
            data.put("IsLaidOut", v.isLaidOut());//19
            data.put("IsLayoutDirectionResolved", v.isLayoutDirectionResolved());//19
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            data.put("BackgroundTintList", String.valueOf(v.getBackgroundTintList()));
            data.put("BackgroundTintMode", String.valueOf(v.getBackgroundTintMode()));
            data.put("ClipToOutline", v.getClipToOutline());
            data.put("Elevation", v.getElevation());
            data.put("TransitionName", v.getTransitionName());
            data.put("TranslationZ", v.getTranslationZ());
            data.put("Z", v.getZ());
            data.put("NestedScrollingParent", v.hasNestedScrollingParent());
            data.put("StateListAnimator", v.getStateListAnimator());
            data.put("OutlineProvider", String.valueOf(v.getOutlineProvider()));
            data.put("IsAccessibilityFocused", v.isAccessibilityFocused());//21
            data.put("IsImportantForAccessibility", v.isImportantForAccessibility());//21
            data.put("IsNestedScrollingEnabled", v.isNestedScrollingEnabled());//21
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP_MR1) {
            data.put("AccessibilityTraversalAfter", v.getAccessibilityTraversalAfter());
            data.put("AccessibilityTraversalBefore", v.getAccessibilityTraversalBefore());
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            data.put("AccessibilityClassName", v.getAccessibilityClassName());
            data.put("Foreground:", String.valueOf(v.getForeground()));
            data.put("ForegroundTintMode", String.valueOf(v.getForegroundTintMode()));
            data.put("IsContextClickable", v.isContextClickable());
            data.put("ScrollIndicators", getTranslator().scrollIndicators(v.getScrollIndicators()));
            data.put("ForegroundGravity", getTranslator().gravity(v.getForegroundGravity()));
            data.put("ForegroundTintList", String.valueOf(v.getForegroundTintList()));
            data.put("RootWindowInsets:", String.valueOf(v.getRootWindowInsets()));
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            data.put("HasOverlappingRendering", String.valueOf(v.getHasOverlappingRendering()));
            data.put("IsTemporarilyDetached", String.valueOf(v.isTemporarilyDetached()));
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.N_MR1) {
            data.put("RevealOnFocusHint", String.valueOf(v.getRevealOnFocusHint()));
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            data.put("AutofillHints", Arrays.toString(v.getAutofillHints()));
            data.put("DefaultFocusHighlightEnabled", v.getDefaultFocusHighlightEnabled());
            data.put("Focusable", translator.focusable(v.getFocusable()));
            data.put("ImportantForAutofill", translator.importantForAutoFill(v.getImportantForAutofill()));
            data.put("DefaultFocusHighlightEnabled", v.getDefaultFocusHighlightEnabled());
            data.put("ImportantForAutofill", v.getImportantForAutofill());
        }

        data.put("Animation", String.valueOf(v.getAnimation()));
        data.put("ApplicationWindowToken:", String.valueOf(v.getApplicationWindowToken()));
        data.put("DrawableState", getTranslator().drawableStates(v.getDrawableState()));
        data.put("DrawingCache", String.valueOf(v.getDrawingCache()));
        data.put("DrawingCacheBackgroundColor", getStringColor(v.getDrawingCacheBackgroundColor()));
        data.put("DrawingCacheQuality", v.getDrawingCacheQuality());
        data.put("DrawingTime", v.getDrawingTime());
        data.put("FilterTouchesWhenObscured", v.getFilterTouchesWhenObscured());
        data.put("HorizontalFadingEdgeLength", v.getHorizontalFadingEdgeLength());
        data.put("MeasuredHeight", v.getMeasuredHeight());
        data.put("MeasuredWidth", v.getMeasuredWidth());
        data.put("OnFocusChangeListener", String.valueOf(v.getOnFocusChangeListener()));
        data.put("OverScrollMode", v.getOverScrollMode());
        data.put("Parent:", String.valueOf(v.getParent()));
        data.put("RootView:", String.valueOf(v.getRootView()));
        data.put("ScrollBarStyle", v.getScrollBarStyle());
        data.put("SolidColor", getStringColor(v.getSolidColor()));
        data.put("VerticalScrollbarWidth", v.getVerticalScrollbarWidth());
        //data.put("ViewTreeObserver", String.valueOf(v.getViewTreeObserver()));
        data.put("WindowToken:", String.valueOf(v.getWindowToken()));
        data.put("WindowVisibility", getTranslator().visibility(v.getWindowVisibility()));
        data.put("HasWindowFocus", v.hasWindowFocus());
        data.put("IsDrawingCacheEnabled", v.isDrawingCacheEnabled());
        data.put("IsFocused", v.isFocused());
        data.put("IsHapticFeedbackEnabled", v.isHapticFeedbackEnabled());
        data.put("IsHorizontalFadingEdgeEnabled", v.isHorizontalFadingEdgeEnabled());
        data.put("IsHorizontalScrollBarEnabled", v.isHorizontalScrollBarEnabled());
        data.put("IsInEditMode", v.isInEditMode());
        data.put("IsInTouchMode", v.isInTouchMode());
        data.put("IsLayoutRequested", v.isLayoutRequested());
        data.put("IsSaveEnabled", v.isSaveEnabled());
        data.put("IsScrollbarFadingEnabled", v.isScrollbarFadingEnabled());
        data.put("IsVerticalFadingEdgeEnabled", v.isVerticalFadingEdgeEnabled());
        data.put("IsVerticalScrollBarEnabled", v.isVerticalScrollBarEnabled());
        data.put("TouchDelegate", String.valueOf(v.getTouchDelegate()));
        final ArrayList<View> touchables = v.getTouchables();
        data.put("TouchablesCount", touchables != null ? touchables.size() : 0);
        data.put("VerticalFadingEdgeLength", v.getVerticalFadingEdgeLength());


        final RenderAreaWrapper<View> renderArea = DetailExtractor.getRenderArea(v);
        if (renderArea != null) {
            renderArea.getRenderArea(v, rect);
            final String value = toString(rect);
            data.put("_RenderAreaRelative", value);
            data.put("RenderAreaRelative", value);
        }

        data.put("LayoutParams:", v.getLayoutParams() != null ? v.getLayoutParams().getClass().getName() : null);
        //noinspection unchecked
        if (v.getLayoutParams() != null) {
            final BaseExtractor<ViewGroup.LayoutParams> extractor = (BaseExtractor<ViewGroup.LayoutParams>) DetailExtractor.getExtractor(v.getLayoutParams().getClass());
            extractor.onFillValues(v.getLayoutParams(), data, parentData);
        }


        if (isExportView(v)) {
            fillAnnotatedValues(v, data);
        }

        return data;
    }

    public HashMap<String, Object> fillMandatoryFields(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        data.put("Type", String.valueOf(v.getClass().getName()));
        data.put("Extractor", getClass().getName());
        data.put("Width", v.getWidth());
        data.put("Height", v.getHeight());

        boolean isViewGroup = (v instanceof ViewGroup) && !DetailExtractor.isExcludedViewGroup(v.getClass().getName());
        Integer isParentVisible = parentData == null ? View.VISIBLE : (Integer) parentData.get("_Visibility");
        boolean isVisible = v.getVisibility() == View.VISIBLE && (isParentVisible == null || View.VISIBLE == isParentVisible);
        boolean hasBackground = v.getBackground() != null;
        boolean shouldRender = isVisible && v.isShown() && ((isViewGroup && hasBackground) || !isViewGroup);
        data.put("_RenderViewContent", shouldRender);

        //TODO:remove later
        data.put("RenderViewContent", shouldRender);
        fillScale(v, data, parentData);
        fillLocationValues(v, data, parentData);
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
        float sx = 1;
        float sy = 1;
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
            sx = v.getScaleX() * fx;
            sy = v.getScaleY() * fy;
        }

        data.put("_ScaleX", sx);
        data.put("_ScaleY", sy);
        data.put("ScaleAbsoluteX", sx);
        data.put("ScaleAbsoluteY", sy);
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
        Class<?> clz = v.getClass();
        while(clz != View.class) {//we can stop on View.class there is nothing for us
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ExportField annotation = field.getAnnotation(ExportField.class);
                if (annotation != null) {
                    try {
                        Object o = field.get(v);
                        data.put(annotation.value(), o == null ? null : String.valueOf(o));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            clz = clz.getSuperclass();
        }
        return data;
    }

    private static boolean isExportView(View v) {
        Class<?> clz = v.getClass();
        while (clz != View.class) {//we can stop on View.class there is nothing for us
            if (clz.getAnnotation(ExportView.class) != null) {
                return true;
            }
            clz = clz.getSuperclass();
        }
        return false;
    }

    private String toString(Rect rect) {
        return new StringBuilder()
                .append(rect.left).append(",")
                .append(rect.top).append(",")
                .append(rect.right).append(",")
                .append(rect.bottom).toString();
    }

    protected static String escapeString(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            if (Character.isWhitespace(c)) {
                switch (c) {
                    case ' ':sb.append(" ");break;
                    case '\n':sb.append("\\n");break;
                    case '\t':sb.append("\\t");break;
                    case '\r':sb.append("\\r");break;
                    case '\b':sb.append("\\b");break;
                    case '\f':sb.append("\\f");break;
                    default:
                        sb.append("{0x").append(Integer.toHexString((int) c)).append("}");
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
