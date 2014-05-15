package com.scurab.android.anuitor.nanoplugin;

import fi.iki.elonen.WebServerPlugin;

import java.io.File;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public abstract class BasePlugin implements WebServerPlugin {

    public static final String MIME_JSON = "application/json";
    public static final String MIME_PNG = "image/png";

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return true;
    }

    @Override
    public void initialize(Map<String, String> commandLineOptions) {

    }

    public abstract String[] files();

    public abstract String mimeType();
}
