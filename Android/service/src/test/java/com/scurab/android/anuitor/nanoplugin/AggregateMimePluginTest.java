package com.scurab.android.anuitor.nanoplugin;

import com.scurab.android.anuitor.tools.HttpTools;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jbruchanov on 12.6.2014.
 */
public class AggregateMimePluginTest {

    @Test
    public void testPluginChecksSameMimesPositive() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin("/f2");
        AggregateMimePlugin aggregateMimePlugin = new AggregateMimePlugin(bp1, bp2);
        assertEquals(HttpTools.MimeType.APP_JSON, aggregateMimePlugin.mimeType());
    }

    @Test(expected = IllegalStateException.class)
    public void testPluginChecksSameMimesNegative() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin(HttpTools.MimeType.APP_OCTET, "/f2");
        AggregateMimePlugin aggregateMimePlugin = new AggregateMimePlugin(bp1, bp2);
        assertEquals(HttpTools.MimeType.APP_JSON, aggregateMimePlugin.mimeType());
    }

    @Test
     public void testPluginMergesFiles() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin("/f2");

        AggregateMimePlugin aggregateMimePlugin = new AggregateMimePlugin(bp1, bp2);
        String[] files = aggregateMimePlugin.files();
        Arrays.sort(files);
        assertArrayEquals(new String[]{"/f1", "/f2"}, files);
    }

    @Test(expected = IllegalStateException.class)
    public void testPluginThrowsExceptionIfSameFilesForMerge() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin("/f1");

        new AggregateMimePlugin(bp1, bp2);
    }


    @Test
    public void testPluginSelectsRightPlugin() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin("/f2");
        assertTrue(bp1.canServeUri("/f1", null));

        AggregateMimePlugin aggregateMimePlugin = new AggregateMimePlugin(bp1, bp2);
        assertNull(aggregateMimePlugin.getServeCandidate("/f3", null));
        assertTrue(bp1 == aggregateMimePlugin.getServeCandidate("/f1", null));
        assertTrue(bp2 == aggregateMimePlugin.getServeCandidate("/f2", null));
    }

    @Test
    public void testPluginCallsServeForRightPlugin() {
        BasePlugin bp1 = createBasePlugin("/f1");
        BasePlugin bp2 = createBasePlugin("/f2");

        AggregateMimePlugin aggregateMimePlugin = new AggregateMimePlugin(bp1, bp2);

        String uri = "/f1";
        Map<String, String> headers = mock(Map.class);
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        File f = mock(File.class);
        String mime = HttpTools.MimeType.APP_JSON;

        aggregateMimePlugin.serveFile(uri, headers, session, f, mime);
        verify(bp1, times(1)).serveFile(uri, headers, session, f, mime);

        String uri2 = "/f2";
        Map<String, String> headers2 = mock(Map.class);
        NanoHTTPD.IHTTPSession session2 = mock(NanoHTTPD.IHTTPSession.class);
        File f2 = mock(File.class);
        String mime2 = HttpTools.MimeType.APP_JSON;

        aggregateMimePlugin.serveFile(uri2, headers2, session2, f2, mime2);
        verify(bp1, times(1)).serveFile(uri, headers, session, f, mime);
        verify(bp2, times(1)).serveFile(uri2, headers2, session2, f2, mime2);
    }

    private BasePlugin createBasePlugin(String uri) {
        return createBasePlugin(HttpTools.MimeType.APP_JSON, uri);
    }

    private BasePlugin createBasePlugin(String mime, String uri) {
        BasePlugin bp = mock(HelpBasePlugin.class);
        doCallRealMethod().when(bp).canServeUri(anyString(), any(File.class));
        doCallRealMethod().when(bp).canServeUri(anyString(), (File)isNull());
        doReturn(mime).when(bp).mimeType();
        doReturn(new String[]{uri}).when(bp).files();
        return bp;
    }

    private abstract static class HelpBasePlugin extends BasePlugin{
        @Override
        public boolean canServeUri(String uri, File rootDir) {
            return uri.equals(files()[0]);
        }
    }
}
