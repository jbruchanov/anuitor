package com.scurab.android.anuitor.nanoplugin;

import android.app.Activity;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebServerPlugin;

import java.io.File;
import java.util.Map;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 15:53
 */
public abstract class ActivityPlugin extends BasePlugin {

    public static final String APPLICATION_IS_NOT_ACTIVE = "Application is not active";
    private KnowsActivity[] mKnowsActivity;

    protected ActivityPlugin(KnowsActivity... knowsActivity) {
        if (knowsActivity == null) {
            throw new IllegalArgumentException("knowActivity is null!");
        }
        mKnowsActivity = knowsActivity;
    }

    @Override
    public final NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        if (getActivity() == null) {
            if (mimeType.equals("application/json")) {
                return sJsonEmptyResponse;
            } else {
                return sEmptyResponse;
            }
        } else {
            return handleRequest(uri, headers, session, file, mimeType);
        }
    }

    private static final NanoHTTPD.Response sEmptyResponse = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
        @Override public int getRequestStatus() { return 0; }
        @Override public String getDescription() { return APPLICATION_IS_NOT_ACTIVE; }
    }, "text/plain", APPLICATION_IS_NOT_ACTIVE);

    private static final NanoHTTPD.Response sJsonEmptyResponse = new NanoHTTPD.Response(new NanoHTTPD.Response.IStatus() {
        @Override public int getRequestStatus() { return 0; }
        @Override public String getDescription() { return "{}"; }
    }, "application/json", "{}");

    public abstract NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType);

    public Activity getActivity() {
        for (KnowsActivity knowsActivity : mKnowsActivity) {
            if (knowsActivity == null) {
                continue;
            }
            Activity a = knowsActivity.getCurrentActivity();
            if (a != null) {
                return a;
            }
        }
        return null;
    }
}
