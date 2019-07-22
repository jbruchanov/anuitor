package com.scurab.android.anuitor.nanoplugin;

import androidx.annotation.NonNull;

import com.scurab.android.anuitor.tools.HttpTools;
import com.scurab.android.anuitor.tools.LogCatProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import groovy.lang.GrooidShell;

/**
 * Created by JBruchanov on 20/01/2017.
 */

public class GroovyPlugin extends BasePlugin {

    private static final String FILE = "groovy";
    private static final String PATH = "/" + FILE;
    public static final int BUFFER_SIZE = 8 * 1024;
    private final GrooidShell mShell;

    public GroovyPlugin(@NonNull File tempDir) {
        mShell = new GrooidShell(tempDir, GroovyPlugin.class.getClassLoader());
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return uri.equals(PATH);
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        String response;
        try {
            //String code = IOUtils.toString(session.getInputStream(), "utf-8");
            final String contentLength = headers.get("content-length");
            if (contentLength == null || contentLength.isEmpty()) {
                throw new IllegalStateException("Missing Content-Length header");
            }
            String code = read(session.getInputStream(), Integer.parseInt(contentLength));
            if (code.isEmpty()) {
                throw new IllegalArgumentException("Empty code?!");
            }
            final GrooidShell.EvalResult evaluate = mShell.evaluateOnMainThread(code);
            response = evaluate.result != null ? evaluate.result.toString() : "";
        } catch (Throwable e) {
            response = String.format("%s\n%s", e.getMessage(), LogCatProvider.getStackTrace(e));
        }
        return new OKResponse(HttpTools.MimeType.APP_OCTET, new ByteArrayInputStream(response.getBytes()));
    }

    @Override
    public String[] files() {
        return new String[]{FILE};
    }

    @Override
    public String mimeType() {
        return HttpTools.MimeType.APP_OCTET;
    }

    private static String read(InputStream is, int toRead) throws IOException {
        StringBuilder sb = new StringBuilder();
        int bufferSize = Math.min(toRead, BUFFER_SIZE);
        byte[] buffer = new byte[bufferSize];
        int len = is.read(buffer, 0, bufferSize);
        while (len != -1) {
            sb.append(new String(buffer, 0, len));
            toRead -= len;
            if (toRead == 0) {
                len = -1;
            } else {
                len = is.read(buffer);
            }
        }
        return sb.toString();
    }
}
