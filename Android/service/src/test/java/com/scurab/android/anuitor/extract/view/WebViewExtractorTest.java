package com.scurab.android.anuitor.extract.view;

import android.os.Build;
import android.webkit.WebView;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.extract.Translator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, sdk = Build.VERSION_CODES.JELLY_BEAN_MR2)
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

        final WebView wv = spy(new WebView(RuntimeEnvironment.application));
        final WebViewExtractor wve = new WebViewExtractor(new Translator());

        HashMap<String, Object> data = new HashMap<>();
        doAnswer(mCheckAnswer).when(wv).getTitle();
        doAnswer(mCheckAnswer).when(wv).getSettings();
        wve.fillValues(wv, data, null);
    }

    private boolean amIPosted() {
        //11 is value based on stack trace
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0, n = stackTrace.length; i < n; i++) {
            if ("post".equals(stackTrace[i].getMethodName())) {
                return true;
            }
        }
        return false;
    }

    private Answer mCheckAnswer = new Answer() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            assertTrue(amIPosted());
            return null;
        }
    };
}
