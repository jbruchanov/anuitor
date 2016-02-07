package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import com.scurab.android.anuitor.extract.BaseExtractor;
import com.scurab.android.anuitor.extract.DetailExtractor;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ReflectionExtractor;
import com.scurab.android.anuitor.model.DataResponse;
import com.scurab.android.anuitor.reflect.ObjectReflector;
import com.scurab.android.anuitor.reflect.ReflectionHelper;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by JBruchanov on 05/02/2016.
 */
public class ViewPropertyPlugin extends ActivityPlugin {

    private static final String PROPERTY = "property";
    private static final String REFLECTION = "reflection";
    private static final String FILE = "viewproperty.json";
    private static final String PATH = "/" + FILE;
    private final Paint mClearPaint = new Paint();
    private ReflectionExtractor mReflectionExtractor;

    public ViewPropertyPlugin(WindowManager windowManager) {
        super(windowManager);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
        try {
            String queryString = session.getQueryParameterString();
            HashMap<String, String> qsValue = HttpTools.parseQueryString(queryString);
            if (qsValue.containsKey(POSITION) && qsValue.containsKey(PROPERTY)) {
                boolean reflection = qsValue.containsKey(REFLECTION);
                int position = Integer.parseInt(qsValue.get(POSITION));
                String property = qsValue.get(PROPERTY);
                View view = getCurrentRootView(qsValue);
                view = view != null ? DetailExtractor.findViewByPosition(view, position) : null;
                final ReflectionHelper.Item item = ReflectionHelper.ITEMS.get(property);
                if (view != null && item != null) {
                    Object propertyValue = new ObjectReflector(view).callMethod(item.methodName);
                    if (item.arrayIndex >= 0) {
                        propertyValue = ((Object[]) propertyValue)[item.arrayIndex];
                    }
                    final DataResponse response = handleObject(propertyValue, reflection, property, item.methodName);
                    return new OKResponse(HttpTools.MimeType.APP_JSON, GSON.toJson(response));
                }
            }
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NO_CONTENT, mimeType, "Missing/Invalid 'position' and/or 'property' query string arguments ");
        } catch (Throwable e) {
            final StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            return new OKResponse(HttpTools.MimeType.TEXT_PLAIN, e.getMessage() + "\n" + stringWriter.toString());
        }
    }

    @SuppressWarnings("unchecked")
    protected DataResponse handleObject(Object object, boolean reflection, String name, String methodName) {
        DataResponse response = new DataResponse();
        if (object != null) {
            BaseExtractor extractor = reflection ? null : DetailExtractor.findExtractor(object.getClass());
            if (extractor == null) {
                if (mReflectionExtractor == null) {
                    mReflectionExtractor = new ReflectionExtractor(new Translator(), true);
                }
                extractor = mReflectionExtractor;
            }
            final HashMap data = extractor.fillValues(object, new HashMap<String, Object>(), null);
            data.put("Type", object.getClass().getName());
            data.put("Name", name);
            data.put("MethodName", methodName);
            data.put("Extractor", extractor.getClass().getName());
            response.context = data;

            if (object instanceof Drawable) {
                response.data = Base64.encodeToString(ResourcesPlugin.drawDrawableWithBounds((Drawable) object, mClearPaint), Base64.NO_WRAP);
                response.dataType = BASE64_PNG;
            }
        } else {
            response.context = "Null object";
        }
        return response;
    }

}
