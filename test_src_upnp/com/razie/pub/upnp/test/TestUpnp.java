/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. 
 */
package com.razie.pub.upnp.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import razie.draw.HttpDrawStream;

import junit.framework.TestCase;

import com.razie.pub.base.log.Log;
import com.razie.pub.upnp.DIDLDrawStream;

public class TestUpnp extends TestCase {

    public void setUp() {
    }

    public void testAllStreams() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        HttpDrawStream http = new HttpDrawStream (bytes);
        
        // a DIDL stream will mess with the underlying, no need to proxy through it
        new DIDLDrawStream (http);
        
        http.write("11");
        assertTrue (bytes.toString().contains("DIDL"));
    }

    static final Log logger = Log.factory.create(TestUpnp.class.getName());
}
