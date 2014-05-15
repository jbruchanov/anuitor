package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.view.View;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ScreenViewPlugin extends ActivityPlugin {

    public static final String SCREEN_PNG = "screen.png";

    public ScreenViewPlugin(KnowsActivity... knowsActivity) {
        super(knowsActivity);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        View view = getActivity().getWindow().getDecorView().getRootView();
        view.destroyDrawingCache();
        view.buildDrawingCache(false);

        // get bitmap
        Bitmap b = view.getDrawingCache();
        b.compress(Bitmap.CompressFormat.PNG, 20, bos);
        ByteArrayInputStream resultInputStream = new ByteArrayInputStream(bos.toByteArray());


        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override public int getRequestStatus() { return 0; }
            @Override public String getDescription() { return null; }
        }, MIME_PNG, resultInputStream);
        return response;
    }

    @Override
    public String[] files() {
        return new String[]{SCREEN_PNG};
    }

    @Override
    public String mimeType() {
        return MIME_PNG;
    }
}
