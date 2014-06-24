package com.scurab.android.anuitor.extract.view;

import android.os.Build;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.extract.ViewExtractingTests;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, emulateSdk = Build.VERSION_CODES.JELLY_BEAN, reportSdk = Build.VERSION_CODES.JELLY_BEAN)
@RunWith(RobolectricTestRunner.class)
@Ignore("To slow and multiple platforms throw PermGen out ouf memory")
public class ViewExtractingTestsAPI16 {

    @Test
    public void testExtracting() {
        new ViewExtractingTests().doTests();
    }
}
