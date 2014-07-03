package com.scurab.android.anuitor.nanoplugin;

import android.app.Activity;
import android.view.View;

import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public abstract class ActivityPlugin extends BasePlugin implements WindowManager {

    public static final String APPLICATION_IS_NOT_ACTIVE = "Application is not active";

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
        if (getCurrentActivity() == null) {
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

    @Override
    public Activity getCurrentActivity() {
        return mWindowManager.getCurrentActivity();
    }

    @Override
    public View getCurrentRootView() {
        return mWindowManager.getCurrentRootView();
    }
}
