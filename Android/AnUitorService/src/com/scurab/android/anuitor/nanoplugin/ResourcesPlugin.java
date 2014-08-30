package com.scurab.android.anuitor.nanoplugin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    private static final String STRING_DATA_TYPE = "string";
    private static final String STRINGS_DATA_TYPE = "string[]";
    private static final String BASE64_PNG = "base64_png";
    private static final String ARRAY = "array";
    private static final String XML = "xml";
    private static final String ID = "id";
    private static final String NUMBER = "number";

    private Resources mRes;
    private ResourcesReflector mHelper;
    private Translator mTranslator;

    private Paint mClearPaint = new Paint();

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
                rr.data = (e == null ? "WTF NullException!" : e.getMessage());
                rr.context = e != null ? e.getClass().getName() : null;
                rr.dataType = STRING_DATA_TYPE;
                rr.type = IdsHelper.RefType.unknown;
                resultInputStream = new ByteArrayInputStream(GSON.toJson(rr).getBytes());
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
        response.type = type;
        response.id = id;
        response.name = IdsHelper.getNameForId(id);
        switch(type){
            case anim:
            case animator:
            case interpolator:
                response.data = DOM2XmlPullBuilder.transform(mRes.getAnimation(id));
                response.dataType = XML;
                break;
            case array:
                handleArray(id, response);
                break;
            case bool:
                response.data = mRes.getBoolean(id);
                response.dataType = "boolean";
                break;
            case color:
                handleColor(id, response);
                break;
            case dimen:
                response.data = mRes.getDimension(id);
                response.dataType = NUMBER;
                break;
            case drawable:
            case mipmap:
                handleDrawable(id, response);
                break;
            case fraction: {
                TypedValue tv = new TypedValue();
                mRes.getValue(id, tv, true);
                response.dataType = NUMBER;
                if (TypedValue.TYPE_FRACTION == tv.type) {
                    response.data = mRes.getFraction(id, 100, 100);
                    response.context = "Base=100";
                } else if (TypedValue.TYPE_FLOAT == tv.type) {
                    response.data = tv.getFloat();
                } else {
                    response.data = "Not implemented franction for TypedValue.type = " + tv.type;
                    response.dataType = STRING_DATA_TYPE;
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
                response.dataType = XML;
                break;
            case plurals:
                handlePlurals(id, response);
                break;
            case string:
                response.data = mRes.getString(id);
                response.dataType = STRING_DATA_TYPE;
                break;
            case xml:
                response.data = DOM2XmlPullBuilder.transform(mRes.getXml(id));
                response.dataType = XML;
                break;
            default:
            case attr:
            case raw:
            case style:
            case styleable:
            case unknown:
                response.data = String.format("Type '%s' is not supported.", type);
                response.dataType = STRING_DATA_TYPE;
                break;
        }

        String json = GSON.toJson(response);
        ByteArrayInputStream result = new ByteArrayInputStream(json.getBytes());

        return result;
    }

    private static final int[] QUANTITIES = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50, 80, 99, 100, 1000, 10000};
    private void handlePlurals(int resId, ResourceResponse outResponse) {
        outResponse.dataType = STRINGS_DATA_TYPE;
        String[] result = new String[QUANTITIES.length];
        outResponse.data = result;
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
                outResponse.dataType = STRINGS_DATA_TYPE;
            } else {
                outResponse.dataType = int[].class.getSimpleName();
            }
        } else {
            outResponse.data = stringArray;
            outResponse.dataType = STRINGS_DATA_TYPE;
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

    private static final int SIZE = 150;

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
        return drawDrawableWithSize(d, w, h);
    }

    private byte[] drawDrawableWithSize(Drawable d, int w, int h) {
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
            outResponse.dataType = ARRAY;
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.data = mHelper.load(id);
            xml.dataType = XML;
            xml.id = id;

            ResourceResponse colors = new ResourceResponse();
            rr[1] = colors;

            ColorStateList colorStateList = mRes.getColorStateList(id);
            colors.id = id;
            colors.dataType = ARRAY;
            handleColorStateList(id, colorStateList, colors);
            return;//just leave everything is rendered now
        }

        outResponse.data = HttpTools.getStringColor(mRes.getColor(id));
        outResponse.dataType = "color";
    }

    private void handleColorStateList(int id, ColorStateList colorStateList, ResourceResponse outColors) {
        ColorStateListReflector reflector = new ColorStateListReflector(colorStateList);
        final int len = reflector.getStateCount();

        ResourceResponse[] stateColors = new ResourceResponse[len];
        outColors.data = stateColors;

        for (int i = 0; i < len; i++) {
            ResourceResponse rr = new ResourceResponse();
            rr.id = id;
            rr.dataType = "color";

            int[] stateSet = reflector.getColorState(i);
            rr.context = mTranslator.stateListFlags(stateSet);

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
        Drawable drawable = mRes.getDrawable(id);
        if (drawable instanceof NinePatchDrawable) {
            outResponse.context = "9patch";
            handleNinePatchDrawable(id, (NinePatchDrawable)drawable, outResponse);
            return;
        }

        if (tv.string != null && tv.string.toString().endsWith(".xml")) {
            outResponse.dataType = ARRAY;
            ResourceResponse[] rr = new ResourceResponse[2];
            outResponse.data = rr;

            ResourceResponse xml = new ResourceResponse();
            rr[0] = xml;
            xml.data = mHelper.load(id);
            xml.dataType = XML;
            xml.id = id;

            ResourceResponse image = new ResourceResponse();
            image.id = id;
            rr[1] = image;
            if (drawable instanceof StateListDrawable) {
                image.dataType = ARRAY;
                handleStateListDrawable(id, (StateListDrawable) drawable, image);
                return;//just leave everything is rendered now
            } else if(drawable instanceof AnimationDrawable){
                image.dataType = ARRAY;
                handleAnimationDrawable(id, (AnimationDrawable) drawable, image);
                return;
            }
            outResponse = image;//assign image as outResponse to fill ours, not real one
        }

        outResponse.data = Base64.encodeToString(drawDrawable(drawable, SIZE, SIZE), Base64.NO_WRAP);
        outResponse.dataType = BASE64_PNG;
    }

    private void handleNinePatchDrawable(int resId, NinePatchDrawable drawable, ResourceResponse outResponse) {
        ResourceResponse[] images = new ResourceResponse[4];
        outResponse.data = images;
        outResponse.dataType = ARRAY;
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
            rr.dataType = BASE64_PNG;
            rr.context = String.format("Size: %sx%s %s", tw, th, i == 0 ? "original" : "");
            rr.data = Base64.encodeToString(drawDrawableWithSize(drawable, tw, th), Base64.NO_WRAP);
        }
    }

    private void handleAnimationDrawable(int resId, AnimationDrawable drawable, ResourceResponse outResponse) {
        int len = drawable.getNumberOfFrames();
        ResourceResponse[] frames = new ResourceResponse[len];
        outResponse.data = frames;
        for (int i = 0; i < len; i++) {
            Drawable frame = drawable.getFrame(i);

            ResourceResponse rr = new ResourceResponse();
            rr.id = resId;
            rr.dataType = BASE64_PNG;

            rr.context = String.format("Frame:%s", i);
            rr.data = Base64.encodeToString(drawDrawable(frame, SIZE, SIZE), Base64.NO_WRAP);
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
        outResponse.dataType = ARRAY;

        StateListDrawableReflector sldReflector = new StateListDrawableReflector(sld);
        int len = sldReflector.getStateCount();
        ResourceResponse[] stateImages = new ResourceResponse[len];
        outResponse.data = stateImages;
        for (int i = 0; i < len; i++) {
            Drawable state = sldReflector.getStateDrawable(i);
            int[] stateSet = sldReflector.getStateSet(i);
            state.setState(stateSet);

            ResourceResponse rr = new ResourceResponse();
            stateImages[i] = rr;

            rr.id = resId;
            rr.dataType = BASE64_PNG;
            rr.context = mTranslator.stateListFlags(stateSet);
            rr.data = Base64.encodeToString(drawDrawable(sldReflector.getStateDrawable(i), SIZE, SIZE), Base64.NO_WRAP);
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
