package com.scurab.android.anuitor.nanoplugin;

import android.view.View;
import com.google.gson.Gson;
import com.scurab.android.anuitor.model.ViewNode;
import com.scurab.android.anuitor.extract.ViewDetailExtractor;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ViewHierarchyPlugin extends ActivityPlugin {

    public static final String TREE_JSON = "viewhierarchy.json";
    public static final String PATH = "/" + TREE_JSON;

    public ViewHierarchyPlugin(KnowsActivity... knowsActivity) {
        super(knowsActivity);
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        View view = getActivity().getWindow().getDecorView().getRootView();
        ViewNode vn = ViewDetailExtractor.parse(view, false);
        String json = new Gson().toJson(vn);

        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override public int getRequestStatus() { return 0; }
            @Override public String getDescription() { return null; }
        }, MIME_JSON, new ByteArrayInputStream(json.getBytes()));
        return response;
    }

    @Override
    public String[] files() {
        return new String[]{TREE_JSON};
    }

    @Override
    public String mimeType() {
        return MIME_JSON;
    }
}
