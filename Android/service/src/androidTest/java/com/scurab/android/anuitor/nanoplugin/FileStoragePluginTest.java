package com.scurab.android.anuitor.nanoplugin;

import android.os.Environment;

import com.google.gson.Gson;
import com.scurab.android.anuitor.model.FSItem;
import com.scurab.android.anuitor.tools.HttpTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class FileStoragePluginTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetRoot() throws IOException {
        String s = getResponse(null);

        FSItem[] items = new Gson().fromJson(s, FSItem[].class);
        assertNotNull(items);

        String appFolder = String.format("/data/data/%s", getContext().getPackageName());

        assertTrue(items.length > 0);
        assertTrue(appFolder.equals(items[0].getName()));
    }

    public void testGetRootSDCard() throws IOException {
        String s = getResponse(Environment.getExternalStorageDirectory().getAbsolutePath());
        FSItem[] items = new Gson().fromJson(s, FSItem[].class);

        assertNotNull(items);
        assertTrue(items.length > 0);
        assertEquals(FSItem.TYPE_FOLDER, items[0].getType());
        assertEquals(FSItem.TYPE_FILE, items[items.length - 1].getType());
    }

    public void testGetBinFile() throws IOException {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        String s = getResponse(sdcard);
        FSItem[] items = new Gson().fromJson(s, FSItem[].class);
        assertTrue(String.format("This test needs some files in '%s'", sdcard), items.length > 0);
        FSItem item = items[items.length - 1];
        assertEquals("This test needs file...", FSItem.TYPE_FILE, item.getType());
        String fullPath = sdcard + item.getName();
        NanoHTTPD.Response httpResponse = getHttpResponse(fullPath);

        String mime = httpResponse.getMimeType();
        assertTrue(HttpTools.getMimeType(new File(fullPath)).equals(mime));

        String contentHeader = httpResponse.getHeader("Content-Disposition");
        assertNotNull(contentHeader);
        assertTrue(("inline; filename=" + item.getName()).equals(contentHeader));

        byte[] bytes = IOUtils.toByteArray(httpResponse.getData());
        assertTrue(bytes.length > 0);
        byte[] raw = IOUtils.toByteArray(new FileInputStream(fullPath));
        assertArrays(bytes, raw);
    }

    private static void assertArrays(byte[] arr1, byte[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0, len = arr1.length; i < len; i++) {
            assertEquals(arr1[i], arr2[i]);
        }
    }

    private String getResponse(String path) throws IOException {
        NanoHTTPD.Response httpResponseResponse = getHttpResponse(path);
        return IOUtils.toString(httpResponseResponse.getData());
    }

    private NanoHTTPD.Response getHttpResponse(String path) throws IOException {
        if (path != null) {
            path = "path=" + path;
        }
        final String fPath = path;
        FileStoragePlugin plugin = new FileStoragePlugin(getContext());

        NanoHTTPD.IHTTPSession session = new NanoHTTPD.IHTTPSession() {
            @Override public void execute() throws IOException { }
            @Override public Map<String, String> getParms() { return null; }
            @Override public Map<String, String> getHeaders() { return null; }
            @Override public String getUri() { return null; }
            @Override public String getQueryParameterString() { return fPath; }
            @Override public NanoHTTPD.Method getMethod() { return null; }
            @Override public InputStream getInputStream() { return null; }
            @Override public NanoHTTPD.CookieHandler getCookies() { return null; }
            @Override public void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException { }
        };

        NanoHTTPD.Response response = plugin.serveFile(plugin.files()[0], null, session, null, plugin.mimeType());
        return response;
    }
}
