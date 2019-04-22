package com.scurab.android.anuitor.tools;

/**
 * Created by jbruchanov on 23/06/2014.
 */
public class NetToolsTest extends AndroidTestCase {

    public void testGetLocalIp() {
        String ip = NetTools.getLocalIpAddress();
        assertNotNull(ip);
        assertTrue(NetTools.isIPv4(ip));
    }
}
