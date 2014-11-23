package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fi.iki.elonen.NanoHTTPD;

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public class ViewshotPlugin extends ActivityPlugin {

    private static final String POSITION = "position";
    private static final boolean SAVE = false;//save every imageview req to sdcard

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
    public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session,
                                            File file, String mimeType) {
        String queryString = session.getQueryParameterString();
        int len = queryString != null ? queryString.length() : 0;
        ByteArrayInputStream resultInputStream = null;

        if (len > 0) {
            HashMap<String, String> qsValue = HttpTools.parseQueryString(queryString);
            if (qsValue.containsKey(POSITION)) {
                int position = Integer.parseInt(qsValue.get(POSITION));
                View view = getCurrentRootView();
                view = view != null ? DetailExtractor.findViewByPosition(view, position) : null;
                if (view != null) {
                    float[] scale = getAbsoluteScale(view);

                    Object o = null;
                    try {
                        o = LOCKS.take();
                        int w = view.getWidth();
                        int h = view.getHeight();
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        Bitmap bitmap = null;

                        if (w == 0 || h == 0) {
                            //just workaround for incorrect call, view is not visible
                            bitmap = getEmptyBitmap();
                        } else {
                            if (view.getVisibility() == View.VISIBLE) {

                                //just draw viewgroup's background if we have it
                                if (view instanceof ViewGroup && !DetailExtractor
                                        .isExcludedViewGroup(view.getClass().getName())) {
                                    Drawable drawable = view.getBackground();
                                    if (drawable != null) {
                                        bitmap = drawDrawable(drawable, w, h);
                                    }
                                }

                                if (bitmap == null) {
                                    // get bitmap
                                    view.destroyDrawingCache();
                                    try {
                                        view.buildDrawingCache(false);
                                    } catch (Throwable e) {
                                        Log.e("ViewshotPlugin", e.getMessage());
                                        e.printStackTrace();
                                    }
                                    bitmap = view.getDrawingCache();
                                    if (bitmap == null) {
                                        bitmap = drawView(view, w, h);
                                    }
                                }
                            } else {
                                bitmap = getEmptyBitmap();
                            }
                        }

                        if (scale[0] != 0f || scale[1] != 0f && (bitmap.getWidth() > 0 && bitmap.getHeight() > 0)) {
                            int scaledW = (int) ((bitmap.getWidth() * scale[0]) + 0.5f);
                            int scaledH = (int) ((bitmap.getHeight() * scale[1]) + 0.5f);
                            if (scaledW > 0 && scaledH > 0) {//just prevention about some nonsense
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, false);
                                bitmap.recycle();
                                bitmap = scaled;
                            }
                        }
                        bitmap.compress(Bitmap.CompressFormat.PNG, 20, bos);
                        byte[] image = bos.toByteArray();
                        resultInputStream = new ByteArrayInputStream(image);
                        //region collection purpose only, can be removed
                        if (SAVE) {
                            try {
                                new File("/sdcard/anuitor").mkdir();
                                String toSave = String.format("/sdcard/anuitor/imageview_%s.png", position);
                                FileOutputStream fos = new FileOutputStream(toSave);
                                fos.write(image, 0, image.length);
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //endregion
                        bitmap.recycle();
                    } catch (Exception e) {
                        Log.e("ViewshotPlugin", e.getMessage());
                        e.printStackTrace();
                    }
                    LOCKS.add(o);
                }
            }
        }

        if (resultInputStream == null) {
            resultInputStream = new ByteArrayInputStream(new byte[0]);
        }

        NanoHTTPD.Response response = new OKResponse(IMAGE_PNG, resultInputStream);
        return response;
    }

    /**
     * Travesre view hierarchy predecessors to get absolute scale of this view
     * @param view
     * @return
     */
    private static float[] getAbsoluteScale(View view) {
        float[] scale = {1f, 1f};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            scale[0] = view.getScaleX();
            scale[1] = view.getScaleY();
            ViewParent vp = view.getParent();
            while (vp != null && vp instanceof View) {
                view = (View) vp;
                scale[0] *= view.getScaleX();
                scale[1] *= view.getScaleY();
                vp = view.getParent();
            }
        }
        return scale;
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
        return new String[] {VIEW_PNG};
    }

    @Override
    public String mimeType() {
        return IMAGE_PNG;
    }

}
