package com.scurab.android.anuitor.extract;

import android.os.Build;
import android.os.Looper;
import android.webkit.WebView;

import com.scurab.android.anuitor.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, emulateSdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class WebViewExtractorTest {

    /**
     * This is pretty dump test. Because of webview's getters whose need to be accessed in main thread,
     * i have to post the extracting code to run it in main thread. Unfortunately robolectric posts in same
     * thread to have better async handling, so i'm just checking by {@link #amIPosted()} if current stack trace
     * has post method in execution.
     * @throws InterruptedException
     */
    @Test
    public void testExtractingInMainThread() throws InterruptedException {

        final WebView wv = spy(new WebView(Robolectric.application));
        final WebViewExtractor wve = new WebViewExtractor();

        HashMap<String, Object> data = new HashMap<String, Object>();
        doAnswer(mCheckAnswer).when(wv).getTitle();
        doAnswer(mCheckAnswer).when(wv).getSettings();
        wve.fillValues(wv, data, null);
    }

    private boolean amIPosted(){
        //11 is value based on stack trace
        return "post".equals(Thread.currentThread().getStackTrace()[11].getMethodName());
    }

    private Answer mCheckAnswer = new Answer() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            assertTrue(amIPosted());
            return null;
        }
    };
}
