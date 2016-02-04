package com.scurab.android.anuitor;

import org.robolectric.annotation.Config;

/**
 * Created by jbruchanov@gmail.com
 *
 * @since 2014-05-15.
 */
public final class C {

    public static final String MANIFEST = Config.NONE;

    static{
        TestHelper.initMockito("Mockito-Cache");
    }
}
