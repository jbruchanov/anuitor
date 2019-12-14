package com.scurab.android.uitor.hierarchy;

import com.scurab.android.uitor.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jbruchanov on 12.6.2014.
 */
@SuppressWarnings("KotlinInternalInJava")
@RunWith(RobolectricTestRunner.class)
public class IdsHelperTest {

    @Test
    public void testLoadValues() {
        IdsHelper.getData$service_debug().clear();
        assertEquals(0, IdsHelper.getData$service_debug().size());
        IdsHelper.loadValues(R.class);
        assertTrue(IdsHelper.getData$service_debug().size() > 0);
        int count = 0;
        boolean hasAndroid = false;
        boolean hasOwn = false;
        for (String s : IdsHelper.getData$service_debug().keySet()) {
            count += IdsHelper.getData$service_debug().get(s).size();
            hasAndroid |= s.contains("android.R.");
            hasOwn |= s.startsWith("R.");
        }

        assertTrue(count > 0);
        assertTrue(hasAndroid);
        assertTrue(hasOwn);
    }
}
