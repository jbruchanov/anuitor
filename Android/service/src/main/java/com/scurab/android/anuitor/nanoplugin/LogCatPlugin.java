package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.tools.HttpTools;
import com.scurab.android.anuitor.tools.LogCatProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by JBruchanov on 05/02/2016.
 */
public class LogCatPlugin extends BasePlugin {

    private static final String FILE = "logcat.txt";
    private static final String PATH = "/" + FILE;

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return HttpTools.MimeType.PLAIN_TEXT;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String queryString = session.getQueryParameterString();
        HashMap<String, String> qsValue = queryString != null ? HttpTools.parseQueryString(queryString) : null;
        String type = qsValue != null ? qsValue.get("type") : null;
        return new OKResponse(HttpTools.MimeType.PLAIN_TEXT, LogCatProvider.dumpLogcat(type));
    }
}
