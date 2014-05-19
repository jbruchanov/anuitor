package com.scurab.android.anuitor.nanoplugin;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.scurab.android.anuitor.model.FSItem;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

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

    private String getResponse(String path) throws IOException {
        FileStoragePlugin plugin = new FileStoragePlugin(getContext());

        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        doReturn(path).when(session).getQueryParameterString();

        NanoHTTPD.Response response = plugin.serveFile(plugin.files()[0], null, session, null, plugin.mimeType());

        InputStream data = response.getData();
        return IOUtils.toString(data);
    }
}
