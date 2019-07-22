package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.tools.HttpTools;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ActivityPluginTest {

    @Test
    public void testServeJSONWithNoActivity() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentActivity();

        ActivityPlugin ap = spy(new TestActivityPlugin(wm));
        NanoHTTPD.Response response = ap.serveFile("someuri", null, mock(NanoHTTPD.IHTTPSession.class), mock(File.class), HttpTools.MimeType.APP_JSON);
        String data = IOUtils.toString(response.getData());
        assertEquals("[]", data);
        assertEquals(HttpTools.MimeType.APP_JSON, response.getMimeType());
    }

    @Test
    public void testServeTextWithNoActivity() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentActivity();

        ActivityPlugin ap = spy(new TestActivityPlugin(wm));
        NanoHTTPD.Response response = ap.serveFile("someuri", null, mock(NanoHTTPD.IHTTPSession.class), mock(File.class), HttpTools.MimeType.TEXT_PLAIN);
        String data = IOUtils.toString(response.getData());
        assertEquals(ActivityPlugin.APPLICATION_IS_NOT_ACTIVE, data);
        assertEquals(HttpTools.MimeType.TEXT_PLAIN, response.getMimeType());
    }

    @Test
    public void testServeCallsHandleWithActivity() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        doReturn(new String[1]).when(wm).getViewRootNames();

        ActivityPlugin ap = spy(new TestActivityPlugin(wm));
        String uri = "someuri";
        Map<String, String> headers = mock(Map.class);
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        File f = mock(File.class);
        String mime = HttpTools.MimeType.APP_JSON;

        ap.serveFile(uri, headers, session, f, mime);
        verify(ap, times(1)).handleRequest(uri, headers, session, f, mime);
    }

    private static class TestActivityPlugin extends ActivityPlugin {

        protected TestActivityPlugin(WindowManager windowManager) {
            super(windowManager);
        }

        @Override
        public NanoHTTPD.Response handleRequest(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
            return mock(NanoHTTPD.Response.class);
        }

        @Override public String[] files() { return new String[0]; }
        @Override public String mimeType() { return null; }
        @Override public boolean canServeUri(String uri, File rootDir) { return true; }
    }
}
