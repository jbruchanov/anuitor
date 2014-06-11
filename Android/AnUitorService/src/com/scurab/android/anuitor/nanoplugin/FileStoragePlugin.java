package com.scurab.android.anuitor.nanoplugin;

import android.content.Context;

import com.scurab.android.anuitor.model.FSItem;
import com.scurab.android.anuitor.tools.FileSystemTools;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

/**
 * User: jbruchanov
 * Date: 15/05/2014
 * Time: 14:31
 */
public class FileStoragePlugin extends BasePlugin {

    private static final String FILE = "storage.json";
    private static final String PATH = "/" + FILE;
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
        return APP_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        InputStream inputStream;
        String mime;
        String content = null;
        try {
            String path = session.getQueryParameterString();
            int len = path != null ? path.length() : 0;

            List<FSItem> files;
            String json;

            File f = new File(path);
            if (f.exists() && f.isFile()) {
                inputStream = new FileInputStream(f);
                mime = HttpTools.getMimeType(f);
                content = "inline; filename=" + f.getName();
            } else {
                mime = APP_JSON;
                files = len == 0 ? mRootItems : FileSystemTools.get(f);
                json = GSON.toJson(files);
                inputStream = new ByteArrayInputStream(json.getBytes());
            }
        } catch (Exception e) {
            inputStream = new ByteArrayInputStream(e.getMessage().getBytes());
            mime = APP_JSON;
        }

        final String fd = content;

        NanoHTTPD.Response response = new OKResponse(mime, inputStream);
        if(content != null) {
            response.addHeader("Content-Disposition", content);
        }
        return response;
    }
}
