package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.Executor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ScreenViewPlugin extends ActivityPlugin {

    public static final String SCREEN_PNG = "screen.png";
    public static final String PATH = "/" + SCREEN_PNG;

    private Paint mClearPaint;

    private int[] mLocation = new int[2];

    public ScreenViewPlugin(WindowManager windowManager) {
        super(windowManager);
        mClearPaint = onCreateClearPaint();
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        final View view = getCurrentRootView();
        ByteArrayInputStream resultInputStream = null;

        if (view != null) {
            view.getLocationOnScreen(mLocation);
            final Bitmap[] outBitmap = new Bitmap[1];
            Executor.runInMainThreadBlockingOnlyIfCrashing(new Runnable() {
                @Override
                public void run() {
                    if (mLocation[0] != 0 || mLocation[1] != 0) {//dialog or something, rootview is not at [0,0]
                        int w = mLocation[0] + view.getWidth();
                        int h = mLocation[1] + view.getHeight();
                        outBitmap[0] = onCreateBitmap(w, h);
                        Canvas c = onCreateCanvas(outBitmap[0]);
                        c.drawRect(0, 0, w, h, mClearPaint);//clear white background to get transparency
                        c.translate(mLocation[0], mLocation[1]);
                        view.draw(c);
                    } else {
                        view.destroyDrawingCache();
                        view.buildDrawingCache(false);

                        // get bitmap
                        outBitmap[0] = view.getDrawingCache();
                    }
                }
            });

            Bitmap b = outBitmap[0];
            if (b == null) {//this can happen if app is not running
                resultInputStream = new ByteArrayInputStream(new byte[0]);
            } else {
                b.compress(Bitmap.CompressFormat.PNG, 100, bos);
                resultInputStream = new ByteArrayInputStream(bos.toByteArray());
                b.recycle();
            }
        } else {
            resultInputStream = new ByteArrayInputStream(new byte[0]);
        }


        NanoHTTPD.Response response = new OKResponse(IMAGE_PNG, resultInputStream);
        return response;
    }

    Canvas onCreateCanvas(Bitmap b) {
        return new Canvas(b);
    }

    Bitmap onCreateBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    Paint onCreateClearPaint() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return p;
    }

    Paint getClearPaint() {
        return mClearPaint;
    }

    @Override
    public String[] files() {
        return new String[]{SCREEN_PNG};
    }

    @Override
    public String mimeType() {
        return IMAGE_PNG;
    }
}
