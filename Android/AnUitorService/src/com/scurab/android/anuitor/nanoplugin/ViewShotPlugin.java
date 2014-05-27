package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.view.View;

import com.scurab.android.anuitor.extract.ViewDetailExtractor;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fi.iki.elonen.NanoHTTPD;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ViewShotPlugin extends ActivityPlugin {

    private static final String POSITION = "position";

    public static final String VIEW_PNG = "view.png";
    public static final String PATH = "/" + VIEW_PNG;

    private static final BlockingQueue<Object> LOCKS = new ArrayBlockingQueue<Object>(3);

    static {
        LOCKS.add("1");
        LOCKS.add("2");
    }

    public ViewShotPlugin(KnowsActivity... knowsActivity) {
        super(knowsActivity);
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String queryString = session.getQueryParameterString();
        int len = queryString != null ? queryString.length() : 0;
        ByteArrayInputStream resultInputStream = null;

        if (len > 0) {
            HashMap<String, String> qsValue = HttpTools.parseQueryString(queryString);
            if (qsValue.containsKey(POSITION)) {
                int position = Integer.parseInt(qsValue.get(POSITION));
                View view = getActivity().getWindow().getDecorView().getRootView();
                view = ViewDetailExtractor.findViewByPosition(view, position);
                if (view != null) {
                    Object o = null;
                    try {
                        o = LOCKS.take();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        // get bitmap
                        view.destroyDrawingCache();
                        view.buildDrawingCache(false);
                        Bitmap b = view.getDrawingCache();
                        if (b != null) {
                            b.compress(Bitmap.CompressFormat.PNG, 20, bos);
                            resultInputStream = new ByteArrayInputStream(bos.toByteArray());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LOCKS.add(o);
                }
            }
        }

        if (resultInputStream == null) {
            resultInputStream = new ByteArrayInputStream(new byte[0]);
        }

        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override
            public int getRequestStatus() {
                return 0;
            }

            @Override
            public String getDescription() {
                return null;
            }
        }, MIME_PNG, resultInputStream);
        return response;
    }

    @Override
    public String[] files() {
        return new String[]{VIEW_PNG};
    }

    @Override
    public String mimeType() {
        return MIME_PNG;
    }

}
