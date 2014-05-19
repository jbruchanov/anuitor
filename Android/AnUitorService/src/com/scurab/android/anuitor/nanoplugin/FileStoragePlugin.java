package com.scurab.android.anuitor.nanoplugin;

import android.content.Context;

import com.google.gson.Gson;
import com.scurab.android.anuitor.model.FSItem;
import com.scurab.android.anuitor.tools.FileSystemTools;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 15/05/2014
 * Time: 14:31
 */
public class FileStoragePlugin extends BasePlugin {

    private static final String FILE = "storage.json";
    private final List<FSItem> mRootItems;


    public FileStoragePlugin(Context context) {
        mRootItems = FileSystemTools.get(context);
    }

    @Override
    public String[] files() {
        return new String[] {FILE};
    }

    @Override
    public String mimeType() {
        return MIME_JSON;
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String path = session.getQueryParameterString();
        int len = path != null ? path.length() : 0;

        List<FSItem> files;
        String json;

        files = len == 0 ? mRootItems : FileSystemTools.get(path);
        json = new Gson().toJson(files);

        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override public int getRequestStatus() { return 0; }
            @Override public String getDescription() { return null; }
        }, MIME_JSON, new ByteArrayInputStream(json.getBytes()));
        return response;
    }
}
