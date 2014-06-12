package com.scurab.android.anuitor.extract;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.scurab.android.anuitor.C;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * Created by jbruchanov on 20/05/2014.
 */
@Config(manifest = C.MANIFEST, emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ViewDetailExtractorTest {

    @Before
    public void setUp() {
        ViewDetailExtractor.resetToDefault();
    }

    @Test
    public void testGetParentClassExtractor() {
        HelpTextView v = new HelpTextView(Robolectric.application);
        ViewExtractor extractor = ViewDetailExtractor.getExtractor(v);
        assertEquals(TextViewExtractor.class, extractor.getClass());
    }

    @Test
    public void testGetDeeperParentClassExtractor() {
        HelpTextView2 v = new HelpTextView2(Robolectric.application);
        ViewExtractor extractor = ViewDetailExtractor.getExtractor(v);
        assertEquals(TextViewExtractor.class, extractor.getClass());
    }

    @Test
    public void testInfiniteLoopProblem() {
        ViewDetailExtractor.unregisterExtractor(View.class);
        ViewDetailExtractor.unregisterExtractor(TextView.class);

        ViewExtractor extractor = ViewDetailExtractor.getExtractor(new HelpTextView(Robolectric.application));
        assertNull(extractor);

        ViewDetailExtractor.resetToDefault();
    }

    private static class HelpTextView extends TextView {
        public HelpTextView(Context context) {
            super(context);
        }
    }

    private static class HelpTextView2 extends HelpTextView {
        public HelpTextView2(Context context) {
            super(context);
        }
    }
}
