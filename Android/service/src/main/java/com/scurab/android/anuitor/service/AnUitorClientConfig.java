package com.scurab.android.anuitor.service;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.scurab.android.anuitor.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbruchanov on 20/04/2017.
 */

public class AnUitorClientConfig {

    static final String TYPE_HIGHLIGHTS = "TypeHighlights";
    static final String PROPERTY_HIGHLIGHTS = "PropertyHighlights";

    private AnUitorClientConfig() {
    }

    private static final Map<String, Object> CONFIG = new HashMap<>();

    static Map<String, Object> init(Context context) {
        CONFIG.put("ServerVersion", BuildConfig.VERSION_CODE);

        HashMap<String, Object> device = new HashMap<>();
        device.put("DisplayDensity", String.format("%.2f", context.getResources().getDisplayMetrics().density));
        device.put("API", Build.VERSION.SDK_INT);
        CONFIG.put("Device", device);
        return CONFIG;
    }

    public static void addTypeHighlighting(Class<? extends View> type, String htmlColor) {
        Map<String, String> highlights = (Map<String, String>) CONFIG.get(TYPE_HIGHLIGHTS);
        if (highlights == null) {
            CONFIG.put(TYPE_HIGHLIGHTS, highlights = new HashMap<>());
        }
        highlights.put(type.getName(), htmlColor);
    }

    public static void addPropertyHighlighting(String name, String htmlColor) {
        Map<String, String> highlights = (Map<String, String>) CONFIG.get(PROPERTY_HIGHLIGHTS);
        if (highlights == null) {
            CONFIG.put(PROPERTY_HIGHLIGHTS, highlights = new HashMap<>());
        }
        highlights.put(name, htmlColor);
    }
}
