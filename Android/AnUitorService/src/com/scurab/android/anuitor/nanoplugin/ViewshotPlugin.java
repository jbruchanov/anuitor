package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.scurab.android.anuitor.extract.ViewDetailExtractor;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fi.iki.elonen.NanoHTTPD;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ViewshotPlugin extends ActivityPlugin {

    private static final String POSITION = "position";

    public static final String VIEW_PNG = "view.png";
    public static final String PATH = "/" + VIEW_PNG;

    private static final BlockingQueue<Object> LOCKS = new ArrayBlockingQueue<Object>(3);

    private Paint mClearPaint = new Paint();

    static {
        LOCKS.add("1");
        LOCKS.add("2");
        LOCKS.add("3");
    }

    public ViewshotPlugin(WindowManager windowManager) {
        super(windowManager);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
                View view = getCurrentRootView();
                view = view != null ? ViewDetailExtractor.findViewByPosition(view, position) : null;
                if (view != null) {
                    Object o = null;
                    try {
                        o = LOCKS.take();
                        int w = view.getWidth();
                        int h = view.getHeight();
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        Bitmap b;

                        if (w == 0 || h == 0) {
                            //just workaround for incorrect call, view is not visible
                            b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(b);
                            c.drawRect(0, 0, w, h, mClearPaint);
                        } else {
                            if (view instanceof ViewGroup) {
                                //just draw background if we have it
                                Drawable drawable = view.getBackground();
                                b = drawDrawable(drawable, w, h);
                            } else {
                                if (view.getVisibility() == View.VISIBLE) {
                                    // get bitmap
                                    view.destroyDrawingCache();
                                    view.buildDrawingCache(false);
                                    b = view.getDrawingCache();
                                    if (b == null) {
                                        b = drawView(view, w, h);
                                    }
                                } else {
                                    b = getEmptyBitmap();
                                }
                            }
                        }

                        b.compress(Bitmap.CompressFormat.PNG, 20, bos);
                        resultInputStream = new ByteArrayInputStream(bos.toByteArray());
                        b.recycle();
                    } catch (Exception e) {
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

    private Bitmap drawView(View view, int w, int h) {
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        c.drawRect(0, 0, w, h, mClearPaint);//clear white background to get transparency
        view.draw(c);
        return b;
    }

    private Bitmap drawDrawable(Drawable drawable, int w, int h) {
        if (drawable == null) {
            return getEmptyBitmap();
        }
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        c.drawRect(0, 0, w, h, mClearPaint);//clear white background to get transparency
        drawable.draw(c);
        return b;
    }

    private Bitmap getEmptyBitmap() {
        Bitmap b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, 1, 1, mClearPaint);
        return b;
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
