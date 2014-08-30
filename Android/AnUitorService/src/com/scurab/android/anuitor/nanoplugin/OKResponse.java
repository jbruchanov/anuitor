package com.scurab.android.anuitor.nanoplugin;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jbruchanov on 11/06/2014.
 */
public class OKResponse extends NanoHTTPD.Response {

    private static final IStatus STATUS = new IStatus() {
        @Override public int getRequestStatus() { return Status.OK.getRequestStatus(); }
        @Override public String getDescription() { return Status.OK.getDescription(); }
    };

    public OKResponse(String mimeType, String data) {
        super(STATUS, mimeType, data);
    }

    public OKResponse(String mimeType, InputStream data) {
        super(STATUS, mimeType, data);
    }
}
