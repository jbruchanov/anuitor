package com.scurab.android.anuitor.extract2.view;

import android.os.Build;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.extract2.ViewExtractingTests;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@Config(manifest = C.MANIFEST, sdk = Build.VERSION_CODES.JELLY_BEAN_MR1)
@RunWith(RobolectricTestRunner.class)
@Ignore("To slow and multiple platforms throw PermGen out ouf memory")
public class ViewExtractingTestsAPI17 {

    @Test
    public void testExtracting() throws ClassNotFoundException {
        new ViewExtractingTests().doTests();
    }
}
