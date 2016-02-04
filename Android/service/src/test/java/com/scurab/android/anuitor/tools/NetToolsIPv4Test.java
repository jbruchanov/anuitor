package com.scurab.android.anuitor.tools;

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
public class NetToolsIPv4Test {

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> dataSet() {
        return Arrays.asList(
                new Object[]{false, "abcde"},
                new Object[]{true, "127.0.0.1"},
                new Object[]{true, "192.168.0.1"},
                new Object[]{false, "FE80:0000:0000:0000:0202:B3FF:FE1E:8329"},
                new Object[]{false, "FE80::0202:B3FF:FE1E:8329"},
                new Object[]{false, "::1"}
        );
    }

    private boolean mIsValid;
    private String mIpValue;

    public NetToolsIPv4Test(boolean isValid, String ipValue) {
        mIsValid = isValid;
        mIpValue = ipValue;
    }

    @Test
    public void testValidIPv4() {
        assertEquals(mIsValid, NetTools.isIPv4(mIpValue));
    }
}