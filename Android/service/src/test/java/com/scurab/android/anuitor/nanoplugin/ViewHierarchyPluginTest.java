package com.scurab.android.anuitor.nanoplugin;

import android.view.View;

import com.google.gson.Gson;
import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.model.ViewNode;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.File;

import fi.iki.elonen.NanoHTTPD;

import static java.util.Collections.emptyMap;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ViewHierarchyPluginTest {

    private static final String EMPTY_STRING = "";
    private static final File EMPTY_FILE = new File("");

    @Test
    public void testCorrectPath() {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ViewHierarchyPlugin viewHierarchyPlugin = new ViewHierarchyPlugin(wm);
        assertArrayEquals(new String[]{"viewhierarchy.json"}, viewHierarchyPlugin.files());
        assertTrue(viewHierarchyPlugin.canServeUri("/viewhierarchy.json", EMPTY_FILE));
    }

    @Test
    public void testEmptyResultForNullView() {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ViewHierarchyPlugin viewHierarchyPlugin = new ViewHierarchyPlugin(wm);
        NanoHTTPD.Response response = viewHierarchyPlugin.handleRequest(EMPTY_STRING, emptyMap(), mock(NanoHTTPD.IHTTPSession.class), EMPTY_FILE, EMPTY_STRING);
        assertEquals(HttpTools.MimeType.APP_JSON, response.getMimeType());
        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, response.getStatus());
    }

    @Test
    public void testNonEmptyResultForNullView() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        View inflate = View.inflate(RuntimeEnvironment.application, android.R.layout.two_line_list_item, null);
        doReturn(inflate).when(wm).getCurrentRootView();

        ViewHierarchyPlugin viewHierarchyPlugin = new ViewHierarchyPlugin(wm);
        NanoHTTPD.Response response = viewHierarchyPlugin.handleRequest(EMPTY_STRING, emptyMap(), mock(NanoHTTPD.IHTTPSession.class), EMPTY_FILE, EMPTY_STRING);
        assertEquals(HttpTools.MimeType.APP_JSON, response.getMimeType());
        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, response.getStatus());
    }
}
