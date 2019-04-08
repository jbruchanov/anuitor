package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;

import com.scurab.android.anuitor.extract2.BaseExtractor;
import com.scurab.android.anuitor.extract2.DetailExtractor;
import com.scurab.android.anuitor.extract.RenderAreaWrapper;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.extract.view.ReflectionExtractor;
import com.scurab.android.anuitor.model.DataResponse;
import com.scurab.android.anuitor.model.OutRef;
import com.scurab.android.anuitor.reflect.ReflectionHelper;
import com.scurab.android.anuitor.reflect.ViewReflector;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.Executor;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
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
            if (qsValue.containsKey(POSITION)) {
                final boolean reflection = qsValue.containsKey(REFLECTION);
                int position = Integer.parseInt(qsValue.get(POSITION));
                String property = qsValue.containsKey(PROPERTY) ? qsValue.get(PROPERTY) : null;
                if ("undefined".equalsIgnoreCase(property)) {
                    property = null;
                }
                View view = getCurrentRootView(qsValue);
                view = view != null ? DetailExtractor.findViewByPosition(view, position) : null;
                if (view != null) {
                    DataResponse response;
                    if (property != null) {
                        final ReflectionHelper.Item item = ReflectionHelper.ITEMS.get(property);
                        Object propertyValue;
                        String methodName;
                        final ViewReflector reflector = new ViewReflector(view);
                        if (item != null) {
                            propertyValue = reflector.callMethod(item.methodName);
                            if (item.arrayIndex >= 0) {
                                propertyValue = ((Object[]) propertyValue)[item.arrayIndex];
                            }
                            methodName = item.methodName;
                        } else {
                            OutRef<String> oMethodName = new OutRef<>();
                            propertyValue = tryGetValue(reflector, property, oMethodName);
                            methodName = oMethodName.getValue();
                        }
                        response = handleObject(propertyValue, reflection, view.getClass().getName(), property, methodName);
                    } else {
                        final OutRef<DataResponse> ref = new OutRef<>();
                        final View finalView = view;
                        Executor.runInMainThreadBlocking(30000, new Runnable() {
                            @Override
                            public void run() {
                                ref.setValue(handleObject(finalView, reflection, finalView.getClass().getName(), "", ""));
                            }
                        });
                        response = ref.getValue();
                    }
                    return new OKResponse(HttpTools.MimeType.APP_JSON, JSON.toJson(response));
                }
            }
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NO_CONTENT, mimeType, "Missing/Invalid 'position' and/or 'property' query string arguments ");
        } catch (Throwable e) {
            final StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            return new OKResponse(HttpTools.MimeType.TEXT_PLAIN, e.getMessage() + "\n" + stringWriter.toString());
        }
    }

    private static Object tryGetValue(ViewReflector reflector, String property, OutRef<String> outMethodName) {
        String subName = Character.toUpperCase(property.charAt(0)) + property.substring(1);
        String[] methods = new String[]{property, "get" + subName, subName};
        for (String s : methods) {
            try {
                outMethodName.setValue(s);
                return reflector.callMethod(s);
            } catch (Throwable t) {
                outMethodName.setValue(null);
                //ignore
            }
        }
        throw new IllegalStateException(String.format("Not found methods for property:'%s' tried:%s", property, Arrays.toString(methods)));
    }

    @SuppressWarnings("unchecked")
    protected DataResponse handleObject(Object object, boolean reflection, String parentType, String name, String methodName) {
        DataResponse response = new DataResponse();
        if (object != null) {
            BaseExtractor extractor = reflection ? null : DetailExtractor.findExtractor(object.getClass());
            if (extractor == null) {
                if (mReflectionExtractor == null) {
                    mReflectionExtractor = new ReflectionExtractor(true);
                }
                extractor = mReflectionExtractor;
            }
            final Map<String, Object> data = extractor.fillValues(object, new HashMap<>(), null);
            data.put("Type", object.getClass().getName());
            data.put("1ParentType", parentType);
            data.put("2Name", name);
            data.put("3MethodName", methodName);
            data.put("4Extractor", extractor.getClass().getName());
            response.Context = data;

            if (object instanceof Drawable) {
                response.Data = Base64.encodeToString(ResourcesPlugin.drawDrawableWithBounds((Drawable) object, mClearPaint), Base64.NO_WRAP);
                response.DataType = BASE64_PNG;
            } else if (object instanceof View) {
                View view = (View) object;
                final RenderAreaWrapper<View> renderSize = DetailExtractor.getRenderArea(view);
                Rect renderArea = new Rect();
                renderArea.set(0, 0, view.getWidth(), view.getHeight());
                if (renderSize != null) {
                    renderSize.getRenderArea(view, renderArea);
                }
                final Bitmap bitmap = ViewshotPlugin.drawViewBlocking(view, renderArea, mClearPaint);
                if (bitmap != null) {
                    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    response.Data = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                    response.DataType = BASE64_PNG;
                }
            }
        } else {
            response.Context = "Null object";
        }
        return response;
    }

}
