package com.scurab.android.anuitor.nanoplugin;

import androidx.annotation.NonNull;

import com.scurab.android.anuitor.json.JsonSerializer;

import java.util.Map;

import fi.iki.elonen.WebServerPlugin;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
@SuppressWarnings("Convert2MethodRef")
public abstract class BasePlugin implements WebServerPlugin {

    protected static final String STRING_DATA_TYPE = "string";
    protected static final String STRINGS_DATA_TYPE = "string[]";
    protected static final String BASE64_PNG = "base64_png";

    public static JsonSerializer JSON = null;

    public BasePlugin() {
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

        return JSON;
    }

    @Override
    public void initialize(Map<String, String> commandLineOptions) {

    }

    public abstract String[] files();

    public abstract String mimeType();

    private static JsonSerializer tryCreateSerializer(@NonNull String className, @NonNull String jsonCreatorClass) {
        try {
            if (Class.forName(className) != null) {
                return (JsonSerializer) Class.forName(String.format("com.scurab.android.anuitor.json.%s", jsonCreatorClass)).newInstance();
            }
        } catch (Throwable e) {
            //ignore
        }
        return null;
    }
}
