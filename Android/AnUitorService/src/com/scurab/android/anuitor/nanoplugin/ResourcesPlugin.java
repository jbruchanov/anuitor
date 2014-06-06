package com.scurab.android.anuitor.nanoplugin;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Base64;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.ResourcesHelper;
import com.scurab.android.anuitor.reflect.StateListDrawableReflector;
import com.scurab.android.anuitor.tools.DOM2XmlPullBuilder;
import com.scurab.android.anuitor.tools.HttpTools;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class ResourcesPlugin extends BasePlugin {

    private static final String FILE = "resources.json";
    public static final String PATH = "/" + FILE;

    private Resources mRes;
    private ResourcesHelper mHelper;

    private Gson mGson = new Gson();

    private Paint mClearPaint = new Paint();

    public ResourcesPlugin(Resources res) {
        mRes = res;
        mHelper = new ResourcesHelper(mRes);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return MIME_JSON;
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return PATH.equals(uri);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String queryString = session.getQueryParameterString();
        HashMap<String, String> qs = HttpTools.parseQueryString(queryString);
        String type = MIME_JSON;
        ByteArrayInputStream resultInputStream = null;
        if (qs.containsKey("id")) {
            int id = -1;
            try {
                id = Integer.parseInt(qs.get("id"));
                resultInputStream = dispatchIdRequest(id);
            } catch (Exception e) {
                resultInputStream = new ByteArrayInputStream(e.getMessage().getBytes());
                type = "text/plain";
            }
        } else {
            String s = IdsHelper.toJson(mRes);
            resultInputStream = new ByteArrayInputStream(s.getBytes());
        }

        NanoHTTPD.Response response = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
            @Override public int getRequestStatus() { return 0; }
            @Override public String getDescription() { return null; }
        }, type, resultInputStream);
        return response;
    }

    protected ByteArrayInputStream dispatchIdRequest(int id) throws IOException, TransformerException, XmlPullParserException {
        ResourceResponse response = new ResourceResponse();
        IdsHelper.RefType type = IdsHelper.getType(id);
        response.type = type;
        response.id = id;
        response.name = IdsHelper.getNameForId(id);
        switch(type){
            case anim:
                response.data = DOM2XmlPullBuilder.transform(mRes.getAnimation(id));
                response.dataType = "xml";
                break;
            case animator:
                break;
            case array:
                response.data = mRes.getStringArray(id);
                response.dataType = String[].class.getSimpleName();
                break;
            case attr:
                break;
            case bool:
                response.data = mRes.getBoolean(id);
                response.dataType = boolean.class.getSimpleName();
                break;
            case color:
                break;
            case dimen:
                response.data = mRes.getDimension(id);
                response.dataType = Number.class.getSimpleName();
                break;
            case drawable:
                handleDrawable(id, response);
                break;
            case fraction:
                TypedValue tv = new TypedValue();
                mRes.getValue(id, tv, true);
                if (TypedValue.TYPE_FRACTION == tv.type) {
                    response.data = mRes.getFraction(id, 100, 100);
                    response.context = "Base=100";
                } else if (TypedValue.TYPE_FLOAT == tv.type) {
                    response.data = tv.getFloat();
                } else {
                    response.data = "Not implemented franction for TypedValue.type = " + tv.type;
                }
                break;
            case id:
                response.data = id;
                response.dataType = int.class.getSimpleName();
                break;
            case integer:
                response.data = mRes.getInteger(id);
                response.dataType = int.class.getSimpleName();
                break;
            case interpolator:
                break;
            case layout:
                String load = mHelper.load(id);
                response.data = load;
                response.dataType = "xml";
                break;
            case menu:
                break;
            case mimpam:
                break;
            case plurals:
                break;
            case raw:
                break;
            case string:
                response.data = mRes.getString(id);
                response.data = "String";
                break;
            case style:
                break;
            case styleable:
                break;
            case xml:
                response.data = DOM2XmlPullBuilder.transform(mRes.getXml(id));
                response.dataType = "xml";
                break;
            case unknown:
                break;
        }

        String json = mGson.toJson(response);
        ByteArrayInputStream result = new ByteArrayInputStream(json.getBytes());

        return result;
    }

    private static final int SIZE = 300;

    private byte[] drawDrawable(Drawable d, int xmlW, int xmlH) {
        int w, h; w = xmlW; h = xmlH;
        int iw = d.getIntrinsicWidth();
        int ih = d.getIntrinsicHeight();
        if (iw != -1 && ih != -1) {
            w = iw;
            h = ih;
        }
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        d.setBounds(0, 0, w, h);
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, w, h, mClearPaint);
        d.draw(c);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        b.recycle();
        return baos.toByteArray();
    }

    private void handleDrawable(int id, ResourceResponse outResponse) throws IOException, TransformerException, XmlPullParserException {
        TypedValue tv = new TypedValue();
        mRes.getValue(id, tv, true);
        if (tv.string.toString().endsWith(".xml")) {
            outResponse.dataType = "array";
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.data = mHelper.load(id);
            xml.dataType = "xml";
            xml.id = id;

            ResourceResponse image = new ResourceResponse();
            Drawable drawable = mRes.getDrawable(id);
            image.id = id;
            rr[1] = image;
            if (drawable instanceof StateListDrawable) {
                image.dataType = "array";
                handleStateListDrawable(id, (StateListDrawable) drawable, image);
                return;//just leave everything is rendered now
            }
            outResponse = image;//assign image as outResponse to fill ours, not real one
        }

        outResponse.data = Base64.encodeToString(drawDrawable(mRes.getDrawable(id), SIZE, SIZE), Base64.NO_WRAP);
        outResponse.dataType = "base64_png";
    }

    private void handleStateListDrawable(int resId, StateListDrawable sld, ResourceResponse outResponse) {
        outResponse.dataType = "array";

        StateListDrawableReflector sldReflector = new StateListDrawableReflector(sld);
        int len = sldReflector.getStateCount();
        ResourceResponse[] stateImages = new ResourceResponse[len];
        outResponse.data = stateImages;
        for (int i = 0; i < len; i++) {
            Drawable state = sldReflector.getStateDrawable(i);

            ResourceResponse rr = new ResourceResponse();
            rr.id = resId;
            rr.dataType = "base64_png";
            int[] stateSet = sldReflector.getStateSet(i);
            rr.context = Translator.stateListDrawableStates(stateSet);
            rr.data = Base64.encodeToString(drawDrawable(sld, SIZE, SIZE), Base64.NO_WRAP);
            stateImages[i] = rr;
        }
    }

    public static class ResourceResponse {
        @SerializedName("Type")
        public IdsHelper.RefType type;

        @SerializedName("Id")
        public int id;

        @SerializedName("Name")
        public String name;

        @SerializedName("Data")
        public Object data;

        @SerializedName("Context")
        public Object context;

        @SerializedName("DataType")
        public String dataType;
    }
}
