package com.scurab.android.anuitor.hierarchy;

import com.scurab.android.anuitor.C;
import com.scurab.android.anuitor.service.AnUitorService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by jbruchanov on 23/06/2014.
 */
@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AnUitorServiceTest {

    @Test
    @Ignore("Doesn't have any context of the app")
    public void testLoadsIdsOnCreate() {
        AnUitorService ser = new AnUitorService();
        IdsHelper./*protected*/VALUES.clear();
        ser.onCreate();
        assertTrue(IdsHelper.VALUES.size() > 0);
    }
}
