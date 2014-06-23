package com.scurab.android.anuitor.nanoplugin;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.scurab.android.anuitor.model.FSItem;
import com.scurab.android.anuitor.tools.HttpTools;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class FileStoragePluginTest extends AndroidTestCase {

    public void testGetRoot() throws IOException {
        String s = getResponse(null);

        FSItem[] items = new Gson().fromJson(s, FSItem[].class);
        assertNotNull(items);

        String appFolder = String.format("/data/data/%s", getContext().getPackageName());

        assertTrue(items.length > 0);
        assertTrue(appFolder.equals(items[0].getName()));
    }

    public void testGetRootSDCard() throws IOException {
        String s = getResponse("/sdcard/");
        FSItem[] items = new Gson().fromJson(s, FSItem[].class);

        assertNotNull(items);
        assertTrue(items.length > 0);
        assertEquals(FSItem.TYPE_FOLDER, items[0].getType());
        assertEquals(FSItem.TYPE_FILE, items[items.length - 1].getType());
    }

    public void testGetBinFile() throws IOException {
        String sdcard = "/sdcard/";
        String s = getResponse(sdcard);
        FSItem[] items = new Gson().fromJson(s, FSItem[].class);
        FSItem item = items[items.length - 1];
        assertEquals("This test needs file...", FSItem.TYPE_FILE, item.getType());
        String fullPath = sdcard + item.getName();
        NanoHTTPD.Response httpResponse = getHttpResponseResponse(fullPath);

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
        NanoHTTPD.Response httpResponseResponse = getHttpResponseResponse(path);
        return IOUtils.toString(httpResponseResponse.getData());
    }

    private NanoHTTPD.Response getHttpResponseResponse(String path) throws IOException {
        if (path != null) {
            path = "path=" + path;
        }
        FileStoragePlugin plugin = new FileStoragePlugin(getContext());

        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        doReturn(path).when(session).getQueryParameterString();

        NanoHTTPD.Response response = plugin.serveFile(plugin.files()[0], null, session, null, plugin.mimeType());
        return response;
    }
}
