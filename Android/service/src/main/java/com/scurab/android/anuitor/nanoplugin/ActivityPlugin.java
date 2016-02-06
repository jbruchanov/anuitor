package com.scurab.android.anuitor.nanoplugin;

import android.app.Activity;
import android.view.View;

import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public abstract class ActivityPlugin extends BasePlugin {

    protected static final String POSITION = "position";
    public static final String APPLICATION_IS_NOT_ACTIVE = "Application is not active";
    private static final String SCREEN_INDEX = "screenIndex";

    private static final NanoHTTPD.Response EMPTY_RESPONSE = new OKResponse(HttpTools.MimeType.TEXT_PLAIN, APPLICATION_IS_NOT_ACTIVE);
    private static final NanoHTTPD.Response JSON_EMPTY_RESPONSE = new OKResponse(HttpTools.MimeType.APP_JSON, "{}");

    private WindowManager mWindowManager;

    protected ActivityPlugin(WindowManager windowManager) {
        if (windowManager == null) {
            throw new IllegalArgumentException("knowActivity is null!");
        }
        mWindowManager = windowManager;
    }

    @Override
    public final NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String[] viewRootNames = mWindowManager.getViewRootNames();
        if (viewRootNames == null || viewRootNames.length == 0) {
            if (mimeType.equals(HttpTools.MimeType.APP_JSON)) {
                return JSON_EMPTY_RESPONSE;
            } else {
                return EMPTY_RESPONSE;
            }
        } else {
            return handleRequest(uri, headers, session, file, mimeType);
        }
    }

    public abstract NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType);

    public Activity getCurrentActivity() {
        return mWindowManager.getCurrentActivity();
    }

    public View getCurrentRootView() {
        return getCurrentRootView(-1);
    }

    public View getCurrentRootView(int index) {
        return index < 0 ? mWindowManager.getCurrentRootView() : mWindowManager.getRootView(index);
    }

    public View getCurrentRootView(HashMap<String, String> qsValue) {
        View view;
        try {
            if (qsValue.containsKey(SCREEN_INDEX)) {
                String index = qsValue.get(SCREEN_INDEX);
                int i = Integer.parseInt(index);
                view = getCurrentRootView(i);
            } else {
                view = getCurrentRootView();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            view = null;
        }
        return view;
    }
}
