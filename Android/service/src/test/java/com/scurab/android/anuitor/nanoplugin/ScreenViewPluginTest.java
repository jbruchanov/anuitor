package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.reflect.WindowManager;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import fi.iki.elonen.NanoHTTPD;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ScreenViewPluginTest {

    private static String URL = "x?screenIndex=1";
    @Test
    public void testCorrectPath() {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        assertArrayEquals(new String[]{"screen.png"}, svp.files());
        assertTrue(svp.canServeUri("/screen.png", new File("/")));
    }

    @Test
    public void testNullView() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        NanoHTTPD.Response response = svp.handleRequest(URL, Collections.emptyMap(), session, new File(""), "");
        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void testViewWithTopLeftPosition() throws IOException {
        View v = createView(0, 0, 100, 100);
        Bitmap bitmap = spy(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        doReturn(bitmap).when(v).getDrawingCache();

        WindowManager wm = mock(WindowManager.class);
        doReturn(v).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        doReturn(URL).when(session).getQueryParameterString();
        NanoHTTPD.Response response = svp.handleRequest(URL, Collections.emptyMap(), session, new File(""), "");

        verify(v).destroyDrawingCache();
        verify(v).buildDrawingCache(anyBoolean());
        verify(bitmap).recycle();

        byte[] data = IOUtils.toByteArray(response.getData());
        assertNotNull(data);
        assertTrue(data.length > 0);
        Bitmap resultBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        assertNotNull(resultBitmap);
    }

    private View createView(final int x, final int y, int w, int h) {
        View v = mock(View.class);
        doAnswer(invocation -> {
            int[] pos = (int[]) invocation.getArguments()[0];
            pos[0] = x;
            pos[1] = y;
            return null;
        }).when(v).getLocationOnScreen(any(int[].class));
        doReturn(w).when(v).getWidth();
        doReturn(h).when(v).getHeight();
        return v;
    }
}
