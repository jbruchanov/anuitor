package com.scurab.android.anuitor.nanoplugin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Base64;
import android.util.TypedValue;

import com.scurab.android.anuitor.extract.Translator;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.model.ResourceResponse;
import com.scurab.android.anuitor.reflect.ColorStateListReflector;
import com.scurab.android.anuitor.reflect.ResourcesReflector;
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

import static com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON;

/**
 * Created by jbruchanov on 22/05/2014.
 */
public class ResourcesPlugin extends BasePlugin {

    private static final int MAX_9PATCH_SIZE = 600;
    private static final int MIN_9PATCH_SIZE = 100;
    private static final int INC_9PATCH_CONST = 3;

    private static final String FILE = "resources.json";
    private static final String PATH = "/" + FILE;
    private static final String ARRAY = "array";
    private static final String XML = "xml";
    private static final String ID = "id";
    private static final String NUMBER = "number";

    private Resources mRes;
    private ResourcesReflector mHelper;
    private Translator mTranslator;

    private final Paint mClearPaint = new Paint();

    public ResourcesPlugin(Resources res, Translator translator) {
        mRes = res;
        mTranslator = translator;
        mHelper = new ResourcesReflector(mRes);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
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
        String queryString = session.getQueryParameterString();
        HashMap<String, String> qs = HttpTools.parseQueryString(queryString);
        String type = APP_JSON;
        ByteArrayInputStream resultInputStream = null;
        if (qs.containsKey(ID)) {
            int id = -1;
            try {
                id = Integer.parseInt(qs.get(ID));
                resultInputStream = dispatchIdRequest(id);
            } catch (Exception e) {
                ResourceResponse rr = new ResourceResponse();
                rr.Data = (e == null ? "WTF NullException!" : e.getMessage());
                rr.Context = e != null ? e.getClass().getName() : null;
                rr.DataType = STRING_DATA_TYPE;
                rr.Type = IdsHelper.RefType.unknown;
                resultInputStream = new ByteArrayInputStream(JSON.toJson(rr).getBytes());
            }
        } else {
            String s = IdsHelper.toJson(mRes);
            resultInputStream = new ByteArrayInputStream(s.getBytes());
        }

        NanoHTTPD.Response response = new OKResponse(type, resultInputStream);
        return response;
    }

    protected ByteArrayInputStream dispatchIdRequest(int id) throws IOException, TransformerException, XmlPullParserException {
        ResourceResponse response = new ResourceResponse();
        IdsHelper.RefType type = IdsHelper.getType(id);
        response.Type = type;
        response.id = id;
        response.Name = IdsHelper.getNameForId(id);
        switch(type){
            case anim:
            case animator:
            case interpolator:
                response.Data = DOM2XmlPullBuilder.transform(mRes.getAnimation(id));
                response.DataType = XML;
                break;
            case array:
                handleArray(id, response);
                break;
            case bool:
                response.Data = mRes.getBoolean(id);
                response.DataType = "boolean";
                break;
            case color:
                handleColor(id, response);
                break;
            case dimen:
                response.Data = mRes.getDimension(id);
                response.DataType = NUMBER;
                break;
            case drawable:
            case mipmap:
                handleDrawable(id, response);
                break;
            case fraction: {
                TypedValue tv = new TypedValue();
                mRes.getValue(id, tv, true);
                response.DataType = NUMBER;
                if (TypedValue.TYPE_FRACTION == tv.type) {
                    response.Data = mRes.getFraction(id, 100, 100);
                    response.Context = "Base=100";
                } else if (TypedValue.TYPE_FLOAT == tv.type) {
                    response.Data = tv.getFloat();
                } else {
                    response.Data = "Not implemented franction for TypedValue.type = " + tv.type;
                    response.DataType = STRING_DATA_TYPE;
                }
            }
                break;
            case id:
                response.Data = id;
                response.DataType = int.class.getSimpleName();
                break;
            case integer:
                response.Data = mRes.getInteger(id);
                response.DataType = int.class.getSimpleName();
                break;
            case menu:
            case layout:
            case transition:
                String load = mHelper.load(id);
                response.Data = load;
                response.DataType = XML;
                break;
            case plurals:
                handlePlurals(id, response);
                break;
            case string:
                response.Data = mRes.getString(id);
                response.DataType = STRING_DATA_TYPE;
                break;
            case xml:
                response.Data = DOM2XmlPullBuilder.transform(mRes.getXml(id));
                response.DataType = XML;
                break;
            default:
            case attr:
            case raw:
            case style:
            case styleable:
            case unknown:
                response.Data = String.format("Type '%s' is not supported.", type);
                response.DataType = STRING_DATA_TYPE;
                break;
        }

        String json = JSON.toJson(response);
        ByteArrayInputStream result = new ByteArrayInputStream(json.getBytes());

        return result;
    }

    private static final int[] QUANTITIES = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50, 80, 99, 100, 1000, 10000};
    private void handlePlurals(int resId, ResourceResponse outResponse) {
        outResponse.DataType = STRINGS_DATA_TYPE;
        String[] result = new String[QUANTITIES.length];
        outResponse.Data = result;
        for (int i = 0; i < QUANTITIES.length; i++) {
            int quantity = QUANTITIES[i];
            result[i] = String.format("%s\t(%s)", mRes.getQuantityString(resId, quantity, quantity), quantity);
        }
    }


    private void handleArray(int id, ResourceResponse outResponse){
        String[] stringArray = mRes.getStringArray(id);
        if (hasOnlyNulls(stringArray)) {
            int[] array = mRes.getIntArray(id);
            if (hasOnlyZeros(array)) {
                handleTypedArray(id, outResponse);
                outResponse.DataType = STRINGS_DATA_TYPE;
            } else {
                outResponse.DataType = int[].class.getSimpleName();
            }
        } else {
            outResponse.Data = stringArray;
            outResponse.DataType = STRINGS_DATA_TYPE;
        }
    }

    private void handleTypedArray(int id, ResourceResponse outResponse) {
        TypedArray array = mRes.obtainTypedArray(id);
        int len = array.length();
        TypedValue tv = new TypedValue();
        String[] s = new String[len];
        outResponse.Data = s;
        for (int i = 0; i < len; i++) {
            array.getValue(i, tv);
            if (tv.type == TypedValue.TYPE_REFERENCE) {
                s[i] = IdsHelper.getNameForId(tv.data);
            } else {
                s[i] = String.valueOf(tv.data);

            }
        }
    }

    private static final int SIZE = 150;

    /**
     * Draw drawable into PNG image
     * @param d
     * @param xmlW width for xml drawable
     * @param xmlH height for xml drawable
     * @return
     */
    public static byte[] drawDrawable(Drawable d, int xmlW, int xmlH, Paint clearPaint) {
        int w, h; w = xmlW; h = xmlH;
        int iw = d.getIntrinsicWidth();
        int ih = d.getIntrinsicHeight();
        if (iw != -1 && ih != -1) {
            w = iw;
            h = ih;
        }
        return drawDrawableWithSize(d, w, h, clearPaint);
    }

    public static byte[] drawDrawableWithBounds(Drawable d, Paint clearPaint) {
        final Rect bounds = d.getBounds();
        return drawDrawableWithSize(d, bounds.width(), bounds.height(), false, clearPaint);
    }

    public static byte[] drawDrawableWithSize(Drawable d, int w, int h, Paint clearPaint) {
        return drawDrawableWithSize(d, w, h, true, clearPaint);
    }

    public static byte[] drawDrawableWithSize(Drawable d, int w, int h, boolean setBounds, Paint clearPaint) {
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (setBounds) {
            d.setBounds(0, 0, w, h);
        }
        Canvas c = new Canvas(b);
        c.drawRect(0, 0, w, h, clearPaint);
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
            outResponse.DataType = ARRAY;
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.Data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.Data = mHelper.load(id);
            xml.DataType = XML;
            xml.id = id;

            ResourceResponse colors = new ResourceResponse();
            rr[1] = colors;

            ColorStateList colorStateList = mRes.getColorStateList(id);
            colors.id = id;
            colors.DataType = ARRAY;
            handleColorStateList(id, colorStateList, colors);
            return;//just leave everything is rendered now
        }

        outResponse.Data = HttpTools.getStringColor(mRes.getColor(id));
        outResponse.DataType = "color";
    }

    private void handleColorStateList(int id, ColorStateList colorStateList, ResourceResponse outColors) {
        ColorStateListReflector reflector = new ColorStateListReflector(colorStateList);
        final int len = reflector.getStateCount();

        ResourceResponse[] stateColors = new ResourceResponse[len];
        outColors.Data = stateColors;

        for (int i = 0; i < len; i++) {
            ResourceResponse rr = new ResourceResponse();
            rr.id = id;
            rr.DataType = "color";

            int[] stateSet = reflector.getColorState(i);
            rr.Context = mTranslator.stateListFlags(stateSet);

            int color = colorStateList.getColorForState(stateSet, Integer.MIN_VALUE);
            rr.Data = HttpTools.getStringColor(color);

            if (color == Integer.MIN_VALUE) {
                int test = colorStateList.getColorForState(stateSet, Integer.MAX_VALUE);
                if (test == Integer.MAX_VALUE) {
                    rr.Data = "Unable to get Color for state";
                }
            }
            stateColors[i] = rr;
        }
    }

    private void handleDrawable(int id, ResourceResponse outResponse) throws IOException, TransformerException, XmlPullParserException {
        TypedValue tv = new TypedValue();
        mRes.getValue(id, tv, true);
        Drawable drawable = mRes.getDrawable(id);
        if (drawable instanceof NinePatchDrawable) {
            outResponse.Context = "9patch";
            handleNinePatchDrawable(id, (NinePatchDrawable)drawable, outResponse);
            return;
        }

        if (tv.string != null && tv.string.toString().endsWith(".xml")) {
            outResponse.DataType = ARRAY;
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.Data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.Data = mHelper.load(id);
            xml.DataType = XML;
            xml.id = id;

            ResourceResponse image = new ResourceResponse();
            image.id = id;
            rr[1] = image;
            if (drawable instanceof StateListDrawable) {
                image.DataType = ARRAY;
                handleStateListDrawable(id, (StateListDrawable) drawable, image);
                return;//just leave everything is rendered now
            } else if(drawable instanceof AnimationDrawable){
                image.DataType = ARRAY;
                handleAnimationDrawable(id, (AnimationDrawable) drawable, image);
                return;
            }
            outResponse = image;//assign image as outResponse to fill ours, not real one
        }

        outResponse.Data = Base64.encodeToString(drawDrawable(drawable, SIZE, SIZE, mClearPaint), Base64.NO_WRAP);
        outResponse.DataType = BASE64_PNG;
    }

    private void handleNinePatchDrawable(int resId, NinePatchDrawable drawable, ResourceResponse outResponse) {
        ResourceResponse[] images = new ResourceResponse[4];
        outResponse.Data = images;
        outResponse.DataType = ARRAY;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        int[] sizes = {w, h,
                       Math.max(MIN_9PATCH_SIZE, Math.min(INC_9PATCH_CONST * w, MAX_9PATCH_SIZE)), h,
                       w, Math.max(MIN_9PATCH_SIZE, Math.min(INC_9PATCH_CONST * h, MAX_9PATCH_SIZE)),
                       Math.max(MIN_9PATCH_SIZE, Math.min(INC_9PATCH_CONST * w, MAX_9PATCH_SIZE)), Math.max(MIN_9PATCH_SIZE, Math.min(INC_9PATCH_CONST * h, MAX_9PATCH_SIZE))};

        for (int i = 0; i < images.length; i++) {
            int tw = sizes[i * 2];
            int th = sizes[(i * 2) + 1];

            ResourceResponse rr = new ResourceResponse();
            images[i] = rr;

            rr.id = resId;
            rr.DataType = BASE64_PNG;
            rr.Context = String.format("Size: %sx%s %s", tw, th, i == 0 ? "original" : "");
            rr.Data = Base64.encodeToString(drawDrawableWithSize(drawable, tw, th, mClearPaint), Base64.NO_WRAP);
        }
    }

    private void handleAnimationDrawable(int resId, AnimationDrawable drawable, ResourceResponse outResponse) {
        int len = drawable.getNumberOfFrames();
        ResourceResponse[] frames = new ResourceResponse[len];
        outResponse.Data = frames;
        for (int i = 0; i < len; i++) {
            Drawable frame = drawable.getFrame(i);

            ResourceResponse rr = new ResourceResponse();
            rr.id = resId;
            rr.DataType = BASE64_PNG;

            rr.Context = String.format("Frame:%s", i);
            rr.Data = Base64.encodeToString(drawDrawable(frame, SIZE, SIZE, mClearPaint), Base64.NO_WRAP);
            frames[i] = rr;
        }
    }

    /**
     *
     * @param resId
     * @param sld
     * @param outResponse
     */
    private void handleStateListDrawable(int resId, StateListDrawable sld, ResourceResponse outResponse) {
        outResponse.DataType = ARRAY;

        StateListDrawableReflector sldReflector = new StateListDrawableReflector(sld);
        int len = sldReflector.getStateCount();
        ResourceResponse[] stateImages = new ResourceResponse[len];
        outResponse.Data = stateImages;
        for (int i = 0; i < len; i++) {
            Drawable state = sldReflector.getStateDrawable(i);
            int[] stateSet = sldReflector.getStateSet(i);
            state.setState(stateSet);

            ResourceResponse rr = new ResourceResponse();
            stateImages[i] = rr;

            rr.id = resId;
            rr.DataType = BASE64_PNG;
            rr.Context = mTranslator.stateListFlags(stateSet);
            rr.Data = Base64.encodeToString(drawDrawable(sldReflector.getStateDrawable(i), SIZE, SIZE, mClearPaint), Base64.NO_WRAP);
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
