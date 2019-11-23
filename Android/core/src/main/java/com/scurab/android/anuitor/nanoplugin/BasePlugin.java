package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.json.JsonRef;
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

    public static final String STRING_DATA_TYPE = "string";
    public static final String STRINGS_DATA_TYPE = "string[]";
    public static final String BASE64_PNG = "base64_png";

    protected static JsonSerializer JSON = null;

    public BasePlugin() {
        JSON = JsonRef.initJson();
    }

    @Override
    public void initialize(Map<String, String> commandLineOptions) {
        //empty
    }

    public abstract String[] files();

    public abstract String mimeType();
}
