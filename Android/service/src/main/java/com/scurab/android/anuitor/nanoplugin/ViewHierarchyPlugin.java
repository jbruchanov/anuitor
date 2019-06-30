package com.scurab.android.anuitor.nanoplugin;

import android.view.View;

import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.model.ViewNode;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ViewHierarchyPlugin extends ActivityPlugin {

    public static final String TREE_JSON = "viewhierarchy.json";
    public static final String PATH = "/" + TREE_JSON;

    public ViewHierarchyPlugin(WindowManager windowManager) {
        super(windowManager);
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        View view = getCurrentRootView(HttpTools.parseQueryString(session.getQueryParameterString()));
        String json;
        NanoHTTPD.Response response;
        if (view != null) {
            ViewNode vn = DetailExtractor.parse(view, false);
            try {
                json = vn.toJson().toString();
            } catch (Throwable e) {
                json = String.format("{\"exception\":\"%s\"}", e.getMessage());
                e.printStackTrace();
            }
            response = new OKResponse(APP_JSON, new ByteArrayInputStream(json.getBytes()));
        } else {
            response = new Response(NanoHTTPD.Response.Status.NOT_FOUND, APP_JSON, (InputStream) null);
        }

        return response;
    }

    @Override
    public String[] files() {
        return new String[]{TREE_JSON};
    }

    @Override
    public String mimeType() {
        return APP_JSON;
    }
}
