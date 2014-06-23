package com.scurab.android.anuitor.tools;

import com.scurab.android.anuitor.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@Config(manifest = C.MANIFEST, emulateSdk = 18)
@RunWith(ParameterizedRobolectricTestRunner.class)
public class HttpToolsMimeTest {

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> dataSet() {
        return Arrays.asList(
                new Object[]{new File("file.jpg"), HttpTools.MimeType.IMAGE_JPG},
                new Object[]{new File("file.jpeg"), HttpTools.MimeType.IMAGE_JPG},
                new Object[]{new File("file.png"), HttpTools.MimeType.IMAGE_PNG},
                new Object[]{new File("file.json"), HttpTools.MimeType.APP_JSON},
                new Object[]{new File("file.xml"), HttpTools.MimeType.APP_XML},
                new Object[]{new File("file.gif"), HttpTools.MimeType.IMAGE_GIF},
                new Object[]{new File("file.txt"), HttpTools.MimeType.TEXT_PLAIN},
                new Object[]{new File("file."), HttpTools.MimeType.APP_OCTET},
                new Object[]{new File("file.pdf"), HttpTools.MimeType.APP_OCTET},
                new Object[]{new File("file.whatever"), HttpTools.MimeType.APP_OCTET}
        );
    }

    private File mFile;
    private String mMime;

    public HttpToolsMimeTest(File file, String mime) {
        mFile = file;
        mMime = mime;
    }

    @Test
    public void testMimeType() {
        assertEquals(mMime, HttpTools.getMimeType(mFile));
    }
}