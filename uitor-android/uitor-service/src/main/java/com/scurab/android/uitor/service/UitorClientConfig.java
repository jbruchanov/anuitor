package com.scurab.android.uitor.service;

import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.scurab.android.uitor.BuildConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class UitorClientConfig {

    static final String COLOR_POSITION = "rgb(44, 107, 153)";
    static final String COLOR_LAYOUTS = "rgb(76, 153, 44)";
    static final String COLOR_VISIBILITY = "rgb(198, 21, 21)";
    static final String COLOR_RED = "rgb(255, 0, 0)";
    static final String TYPE_HIGHLIGHTS = "TypeHighlights";
    static final String PROPERTY_HIGHLIGHTS = "PropertyHighlights";
    static final String GRID_STROKE_COLOR = "GridStrokeColor";
    static final String SELECTION_COLOR = "SelectionColor";
    static final String POINTER_IGNORE_IDS = "PointerIgnoreIds";
    static final String SNAPSHOT_RESOURCES = "SnapshotResources";

    private UitorClientConfig() {
    }

    private static final Map<String, Object> CONFIG = new HashMap<>();

    static Map<String, Object> init(Context context, boolean hasGroovySupport) {
        CONFIG.put("ServerVersion", BuildConfig.VERSION_CODE);

        Map<String, Object> device = getBuildDeviceValues();
        device.put("API", Build.VERSION.SDK_INT);
        device.put("DisplayDensity", String.format("%.2f", context.getResources().getDisplayMetrics().density));
        CONFIG.put("Device", device);
        CONFIG.put("Groovy", hasGroovySupport);
        CONFIG.put("Pages", new String[]{"LayoutInspectorPage", "TidyTreePage", "ThreeDPage",
                "ResourcesPage", "FileBrowserPage", "WindowsPage",
                "WindowsDetailedPage", "ScreenshotPage", "LogCatPage",
                hasGroovySupport ? "GroovyPage" : ""});

        setSelectionColor(COLOR_RED);
        initDefaultHighlights();
        return CONFIG;
    }

    public static void initDefaultHighlights() {
        addPropertyHighlighting("layout.*", COLOR_LAYOUTS);
        addPropertyHighlighting("[x|y|z]|measure.*|width|height|.*padding.*|translation.*|scale.*|scroll.|top|left|right|bottom|rotation.?", COLOR_POSITION);
        addPropertyHighlighting(".*visibility|isshown|willnotdraw", COLOR_VISIBILITY);
    }

    public static void addTypeHighlighting(Class<? extends View> type, String htmlColor) {
        Map<String, String> highlights = (Map<String, String>) CONFIG.get(TYPE_HIGHLIGHTS);
        if (highlights == null) {
            CONFIG.put(TYPE_HIGHLIGHTS, highlights = new HashMap<>());
        }
        highlights.put(type.getName(), htmlColor);
    }

    /**
     * Add property highlight
     * @param regexp any valid javascript regexp to match properties (everything is lower case)
     * @param htmlColor any valid html color
     */
    public static void addPropertyHighlighting(String regexp, String htmlColor) {
        Map<String, String> highlights = (Map<String, String>) CONFIG.get(PROPERTY_HIGHLIGHTS);
        if (highlights == null) {
            CONFIG.put(PROPERTY_HIGHLIGHTS, highlights = new HashMap<>());
        }
        highlights.put(regexp, htmlColor);
    }

    /**
     * Add view id for automatic pointer ignore
     * @param viewId viewId*
     */
    public static void addPointerIgnoreViewId(@IdRes int viewId) {
        Set<Integer> ignore = (Set<Integer>) CONFIG.get(POINTER_IGNORE_IDS);
        if (ignore == null) {
            CONFIG.put(POINTER_IGNORE_IDS, ignore = new HashSet<>());
        }
        ignore.add(viewId);
    }

    /**
     * Store resources in a snapshot
     * @param enable to have them saved in snapshot (takes more time)
     */
    public static void setResourcesInSnapshot(boolean enable) {
        CONFIG.put(SNAPSHOT_RESOURCES, enable);
    }

    /**
     * Change grid color
     * @param htmlColor
     */
    public static void setGridStrokeColor(@NonNull String htmlColor) {
        CONFIG.put(GRID_STROKE_COLOR, htmlColor);
    }

    /**
     * Change selection color
     * @param htmlColor
     */
    public static void setSelectionColor(@NonNull String htmlColor) {
        CONFIG.put(SELECTION_COLOR, htmlColor);
    }

    private static Map<String, Object> getBuildDeviceValues() {
        Map<String, Object> result = new HashMap<>();
        Field[] declaredFields = Build.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                result.put(declaredField.getName(), declaredField.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
