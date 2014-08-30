package com.scurab.android.anuitor.sample;

import android.content.Context;
import android.test.AndroidTestCase;

import com.scurab.android.anuitor.TestHelper;
import static org.mockito.Mockito.mock;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class SimpleTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        TestHelper.initMockito(getContext());
        super.setUp();
    }

    public void testSimple() {
        assertNotNull(getContext());
    }

    public void testMockSimple() {
        Context c = mock(Context.class);
        assertNotNull(c);
    }
}
