package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.tools.HttpTools;

import java.util.Map;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

import java.io.File;

import fi.iki.elonen.NanoHTTPD;


/**
 * Created by jbruchanov on 20/04/2017.
 */

public class ConfigClientPlugin extends BasePlugin {
    private static final String FILE = "config.json";
    private static final String PATH = "/" + FILE;
    private final Map<String, Object> mData;

    public ConfigClientPlugin(Map<String, Object> data) {
        mData = data;
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return APP_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        return new OKResponse(HttpTools.MimeType.PLAIN_TEXT, JSON.toJson(mData));
    }
}
