package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

/**
 * Created by jbruchanov on 11/01/2015.
 */
public class ActiveScreensPlugin extends ActivityPlugin {
    private static final String FILE = "screens.json";
    private static final String PATH = "/" + FILE;
    private WindowManager mWindowManager;

    public ActiveScreensPlugin(WindowManager windowManager) {
        super(windowManager);
        mWindowManager = windowManager;
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return HttpTools.MimeType.APP_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String json = JSON.toJson(mWindowManager.getViewRootNames());
        NanoHTTPD.Response response = new OKResponse(APP_JSON, new ByteArrayInputStream(json.getBytes()));
        return response;
    }
}
