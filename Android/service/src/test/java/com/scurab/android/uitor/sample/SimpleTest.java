package com.scurab.android.uitor.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by jbruchanov on 15.5.14.
 */
@RunWith(RobolectricTestRunner.class)
public class SimpleTest {

    @Test
    public void testSimple() {
        assertNotNull(RuntimeEnvironment.application);

        Robolectric.getBackgroundThreadScheduler().unPause();
    }
}
