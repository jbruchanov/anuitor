package com.scurab.android.uitor.json;

import androidx.annotation.NonNull;

public class JsonRef {
    public static JsonSerializer JSON;

    public static void init() {
        if (JSON == null) {
            initJson();
            if (JSON == null) {
                throw new IllegalStateException("JsonSerializer not yet created, assign BasePlugin.JSON!");
            }
        }
    }

    public static JsonSerializer initJson() {
        if (JSON == null) {
            JSON = tryCreateSerializer("com.google.gson.Gson", "GsonSerializer");
        }
        if (JSON == null) {
            JSON = tryCreateSerializer("com.fasterxml.jackson.databind.ObjectMapper", "JacksonSerializer");
        }

        if (JSON == null) {
            throw new IllegalStateException("Unable to create instance of Gson or ObjectMapper (jackson).\n" +
                    "Did you add one of this dependency into your build.gradle?");
        }

        return JSON;
    }

    private static JsonSerializer tryCreateSerializer(@NonNull String className, @NonNull String jsonCreatorClass) {
        try {
            //noinspection ConstantConditions
            if (Class.forName(className) != null) {
                return (JsonSerializer) Class.forName(String.format("com.scurab.android.uitor.json.%s", jsonCreatorClass)).newInstance();
            }
        } catch (Throwable e) {
            //ignore
        }
        return null;
    }
}
