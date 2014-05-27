package com.scurab.android.anuitor.nanoplugin;

import android.content.res.Resources;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class ResourcesPlugin extends BasePlugin {

    private static final String FILE = "resources.json";
    public static final String PATH = "/" + FILE;

    private Resources mRes;

    public ResourcesPlugin(Resources res) {
        mRes = res;
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return MIME_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String s = IdsHelper.toJson(mRes);
        ByteArrayInputStream resultInputStream = new ByteArrayInputStream(s.getBytes());

        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override public int getRequestStatus() { return 0; }
            @Override public String getDescription() { return null; }
        }, MIME_JSON, resultInputStream);
        return response;
    }
}
