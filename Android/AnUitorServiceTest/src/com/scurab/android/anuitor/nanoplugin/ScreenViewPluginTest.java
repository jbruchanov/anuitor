package com.scurab.android.anuitor.nanoplugin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.reflect.WindowManager;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ScreenViewPluginTest {

    @Test
    public void testCorrectPath() {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        assertArrayEquals(new String[]{"screen.png"}, svp.files());
        assertTrue(svp.canServeUri("/screen.png", null));
    }

    @Test
    public void testNullView() throws IOException {
        WindowManager wm = mock(WindowManager.class);
        doReturn(null).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        NanoHTTPD.Response response = svp.handleRequest(null, null, null, null, null);
        byte[] data = IOUtils.toByteArray(response.getData());
    }

    @Test
    public void testViewWithTopLeftPosition() throws IOException {
        View v = createView(0, 0, 100, 100);
        Bitmap bitmap = spy(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        doReturn(bitmap).when(v).getDrawingCache();

        WindowManager wm = mock(WindowManager.class);
        doReturn(v).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = new ScreenViewPlugin(wm);
        NanoHTTPD.Response response = svp.handleRequest(null, null, null, null, null);

        verify(v).destroyDrawingCache();
        verify(v).buildDrawingCache(anyBoolean());
        verify(bitmap).recycle();

        byte[] data = IOUtils.toByteArray(response.getData());
        assertNotNull(data);
        assertTrue(data.length > 0);
        Bitmap resultBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        assertNotNull(resultBitmap);
    }

    @Test
    public void testViewWithNoTopLeftPosition() throws IOException {
        View v = createView(150, 150, 150, 150);
        WindowManager wm = mock(WindowManager.class);
        doReturn(v).when(wm).getCurrentRootView();

        ScreenViewPlugin svp = spy(new ScreenViewPlugin(wm));
        final Canvas[] canvas = new Canvas[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Canvas c = spy(new Canvas((Bitmap) invocation.getArguments()[0]));
                canvas[0] = c;

                return c;
            }
        }).when(svp).onCreateCanvas(any(Bitmap.class));

        NanoHTTPD.Response response = svp.handleRequest(null, null, null, null, null);

        final Canvas c = canvas[0];
        InOrder order = inOrder(c, v);
        order.verify(c).drawRect(0, 0, 300, 300, svp.getClearPaint());
        order.verify(c).translate(150, 150);
        order.verify(v).draw(c);

        byte[] data = IOUtils.toByteArray(response.getData());
        assertNotNull(data);
        assertTrue(data.length > 0);
        BitmapFactory.Options op = new BitmapFactory.Options();

        Bitmap resultBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, op);
        assertNotNull(resultBitmap);
        //TODO: why is it returning some weird w/h ?
//        assertEquals(200, resultBitmap.getWidth());
//        assertEquals(200, resultBitmap.getHeight());
        assertEquals("1st pixel is not transparent!", 0, resultBitmap.getPixel(0,0));
    }

    private View createView(final int x, final int y, int w, int h) {
        View v = mock(View.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                int[] pos = (int[]) invocation.getArguments()[0];
                pos[0] = x;
                pos[1] = y;
                return null;
            }
        }).when(v).getLocationOnScreen(any(int[].class));
        doReturn(w).when(v).getWidth();
        doReturn(h).when(v).getHeight();
        return v;
    }
}
