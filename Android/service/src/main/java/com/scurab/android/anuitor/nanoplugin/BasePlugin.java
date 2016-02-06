package com.scurab.android.anuitor.nanoplugin;

import com.google.gson.Gson;

import java.util.Map;

import fi.iki.elonen.WebServerPlugin;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public abstract class BasePlugin implements WebServerPlugin {

    protected static final String STRING_DATA_TYPE = "string";
    protected static final String STRINGS_DATA_TYPE = "string[]";
    protected static final String BASE64_PNG = "base64_png";

    public static final Gson GSON = new Gson();

    @Override
    public void initialize(Map<String, String> commandLineOptions) {

    }

    public abstract String[] files();

    public abstract String mimeType();
}
