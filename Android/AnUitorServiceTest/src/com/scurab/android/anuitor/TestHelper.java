package com.scurab.android.anuitor;

import android.app.Instrumentation;
import android.content.Context;

import java.io.File;

import static junit.framework.Assert.assertTrue;

/**
 * Created by jbruchanov on 19/05/2014.
 */
public class TestHelper {

    /**
     * Init 'dexmaker.dexcache' for proper mockito functionality
     */
    public static void initMockito(Instrumentation instrumentation) {
        initMockito(instrumentation.getTargetContext().getCacheDir().getAbsolutePath());
    }

    /**
     * Init 'dexmaker.dexcache' for proper mockito functionality
     */
    public static void initMockito(Context context) {
        initMockito(context.getCacheDir().getAbsolutePath());
    }

    public static void initMockito(String cacheFolder) {
        File f = new File(cacheFolder);
        if (!f.exists()) {
            assertTrue("Unable to create cache folder for mockito!", f.mkdirs());
        }
        System.setProperty("dexmaker.dexcache", cacheFolder);
    }
}
