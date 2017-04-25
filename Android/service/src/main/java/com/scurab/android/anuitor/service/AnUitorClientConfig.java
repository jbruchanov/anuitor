package com.scurab.android.anuitor.service;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.scurab.android.anuitor.BuildConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbruchanov on 20/04/2017.
 */

@SuppressWarnings("WeakerAccess")
public class AnUitorClientConfig {

    static final String COLOR_POSITION = "rgb(44, 107, 153)";
    static final String COLOR_LAYOUTS = "rgb(76, 153, 44)";
    static final String COLOR_VISIBILITY = "rgb(198, 21, 21)";
    static final String TYPE_HIGHLIGHTS = "TypeHighlights";
    static final String PROPERTY_HIGHLIGHTS = "PropertyHighlights";

    private AnUitorClientConfig() {
    }

    private static final Map<String, Object> CONFIG = new HashMap<>();

    static Map<String, Object> init(Context context) {
        CONFIG.put("ServerVersion", BuildConfig.VERSION_CODE);

        Map<String, Object> device = getBuildDeviceValues();
        device.put("API", Build.VERSION.SDK_INT);
        device.put("DisplayDensity", String.format("%.2f", context.getResources().getDisplayMetrics().density));
        CONFIG.put("Device", device);

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
