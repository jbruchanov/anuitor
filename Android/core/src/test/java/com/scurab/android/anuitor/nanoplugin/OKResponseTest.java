package com.scurab.android.anuitor.nanoplugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jbruchanov on 12.6.2014.
 */
public class OKResponseTest {

    @Test
    public void testReturnsCorrectValues() {
        OKResponse response = new OKResponse("mime", "");
        assertEquals(response.getStatus().getRequestStatus(), NanoHTTPD.Response.Status.OK.getRequestStatus());
        assertEquals(response.getStatus().getDescription(), NanoHTTPD.Response.Status.OK.getDescription());
    }
}
