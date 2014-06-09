package com.scurab.android.anuitor.nanoplugin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.model.ResourceResponse;
import com.scurab.android.anuitor.reflect.ColorStateListReflector;
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
    private static final String PATH = "/" + FILE;
    private static final String STRING_DATA_TYPE = String.class.getSimpleName();
    private static final String STRINGS_NAME = String[].class.getSimpleName();

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
            case animator:
            case interpolator:
                response.data = DOM2XmlPullBuilder.transform(mRes.getAnimation(id));
                response.dataType = "xml";
                break;
            case array:
                handleArray(id, response);
                break;
            case bool:
                response.data = mRes.getBoolean(id);
                response.dataType = boolean.class.getSimpleName();
                break;
            case color:
                handleColor(id, response);
                break;
            case dimen:
                response.data = mRes.getDimension(id);
                response.dataType = Number.class.getSimpleName();
                break;
            case drawable:
            case mipmap:
                handleDrawable(id, response);
                break;
            case fraction: {
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
            case menu:
            case layout:
                String load = mHelper.load(id);
                response.data = load;
                response.dataType = "xml";
                break;
            case string:
                response.data = mRes.getString(id);
                response.data = "String";
                break;
            case xml:
                response.data = DOM2XmlPullBuilder.transform(mRes.getXml(id));
                response.dataType = "xml";
                break;
            default:
            case attr:
            case plurals:
            case raw:
            case style:
            case styleable:
            case unknown:
                response.data = String.format("Type '%s' is not supported.", type);
                response.dataType = STRING_DATA_TYPE;
                break;
        }

        String json = mGson.toJson(response);
        ByteArrayInputStream result = new ByteArrayInputStream(json.getBytes());

        return result;
    }


    private void handleArray(int id, ResourceResponse outResponse){
        String[] stringArray = mRes.getStringArray(id);
        if (hasOnlyNulls(stringArray)) {
            int[] array = mRes.getIntArray(id);
            if (hasOnlyZeros(array)) {
                handleTypedArray(id, outResponse);
                outResponse.dataType = STRINGS_NAME;
            } else {
                outResponse.dataType = int[].class.getSimpleName();
            }
        } else {
            outResponse.data = stringArray;
            outResponse.dataType = STRINGS_NAME;
        }
    }

    private void handleTypedArray(int id, ResourceResponse outResponse) {
        TypedArray array = mRes.obtainTypedArray(id);
        int len = array.length();
        TypedValue tv = new TypedValue();
        String[] s = new String[len];
        outResponse.data = s;
        for (int i = 0; i < len; i++) {
            array.getValue(i, tv);
            if (tv.type == TypedValue.TYPE_REFERENCE) {
                s[i] = IdsHelper.getNameForId(tv.data);
            } else {
                s[i] = String.valueOf(tv.data);

            }
        }
    }

    private static final int SIZE = 300;

    /**
     * Draw drawable into PNG image
     * @param d
     * @param xmlW width for xml drawable
     * @param xmlH height for xml drawable
     * @return
     */
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

    /**
     *
     * @param id
     * @param outResponse
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     * @throws org.xmlpull.v1.XmlPullParserException
     */

    private void handleColor(int id, ResourceResponse outResponse) throws IOException, TransformerException, XmlPullParserException {
        TypedValue tv = new TypedValue();
        mRes.getValue(id, tv, true);
        if (tv.string != null && tv.string.toString().endsWith(".xml")) {
            outResponse.dataType = "array";
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.data = mHelper.load(id);
            xml.dataType = "xml";
            xml.id = id;

            ResourceResponse colors = new ResourceResponse();
            ColorStateList colorStateList = mRes.getColorStateList(id);
            colors.id = id;
            rr[1] = colors;
            colors.dataType = "array";
            handleColorStateList(id, (ColorStateList) colorStateList, colors);
            return;//just leave everything is rendered now
        }

        outResponse.data = HttpTools.getStringColor(mRes.getColor(id));
        outResponse.dataType = STRING_DATA_TYPE;
    }

    private void handleColorStateList(int id, ColorStateList colorStateList, ResourceResponse outColors) {
        ColorStateListReflector reflector = new ColorStateListReflector(colorStateList);
        final int len = reflector.getStateCount();

        ResourceResponse[] stateColors = new ResourceResponse[len];
        outColors.data = stateColors;

        for (int i = 0; i < len; i++) {
            ResourceResponse rr = new ResourceResponse();
            rr.id = id;
            rr.dataType = STRING_DATA_TYPE;
            int[] stateSet = reflector.getColorState(i);
            rr.context = Translator.stateListDrawableStates(stateSet);
            int color = colorStateList.getColorForState(stateSet, Integer.MIN_VALUE);
            rr.data = HttpTools.getStringColor(color);
            if (color == Integer.MIN_VALUE) {
                int test = colorStateList.getColorForState(stateSet, Integer.MAX_VALUE);
                if (test == Integer.MAX_VALUE) {
                    rr.data = "Unable to get Color for state";
                }
            }
            stateColors[i] = rr;
        }
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

    /**
     *
     * @param resId
     * @param sld
     * @param outResponse
     */
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

    private static boolean hasOnlyNulls(String[] arr){
        for (String s : arr) {
            if(s != null){
                return false;
            }
        }
        return true;
    }

    private static boolean hasOnlyZeros(int[] arr) {
        for (int i : arr) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }
}
