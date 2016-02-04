package com.scurab.android.anuitor.tools;

import android.graphics.Color;

import com.scurab.android.anuitor.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class HttpToolsColorTest {



    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> dataSet() {
        return Arrays.asList(
                new Object[]{"#FFFFFFFF", Color.WHITE},
                new Object[]{"#00000000", Color.TRANSPARENT},
                new Object[]{"#FFFF0000", Color.RED},
                new Object[]{"#FF00FF00", Color.GREEN},
                new Object[]{"#FF0000FF", Color.BLUE}
        );
    }

    private String mHtmlColor;
    private int mColor;

    public HttpToolsColorTest(String htmlColor, int color) {
        mHtmlColor = htmlColor;
        mColor = color;
    }

    @Test
    public void testColor() {
        assertEquals(mHtmlColor, HttpTools.getStringColor(mColor));
    }
}