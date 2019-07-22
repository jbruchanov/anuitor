package com.scurab.android.anuitor.nanoplugin;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class Response extends NanoHTTPD.Response {

    public Response(Status status, String mimeType, InputStream data) {
        super(status, mimeType, data);
    }

    public Response(Status status, String mimeType, String txt) {
        super(status, mimeType, txt);
    }
}
