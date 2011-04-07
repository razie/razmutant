/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.upnp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import razie.draw.DrawStream;
import razie.draw.SimpleDrawStream;
import razie.draw.Technology;


/**
 * a drawing stream for DIDL lists, used in UPNP. Will use UPNP as the rendering technology and add
 * DIDL header/footer.
 * 
 * to stream to an http socket use new DIDLDrawStream (new HttpDrawStream(socket)) to stream
 * directly to a socket use new DIDLDrawStream (new SimpleDrawStream(socket.getOutputStream)) to
 * stream to string use new DIDLDrawStream () and toString() at the end
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class DIDLDrawStream extends razie.draw.DrawStream.DrawStreamWrapper {

    boolean closed = false;

    /** use this to stream to some other stream (http etc)...) */
    public DIDLDrawStream(DrawStream wrapped) throws IOException {
        super(wrapped);
        switchTechnology(Technology.UPNP);
        (proxied).write(UpnpUtils.DIDL_BEG);
    }

    /**
     * use this to stream into a string..use toString at the end
     * 
     * @throws IOException
     */
    public DIDLDrawStream() throws IOException {
        super(new SimpleDrawStream(Technology.UPNP, new ByteArrayOutputStream()));
        // buffer = (ByteArrayOutputStream) ((SimpleDrawStream) super.proxied).out;
        // ((SimpleDrawStream) proxied).writeBytes(UpnpUtils.DIDL_BEG.getBytes());
        renderObjectToStream(UpnpUtils.DIDL_BEG);
    }

    @Override
    public void close() {
        // TODO not correct, since BG threads may still produce stuff...
        if (!closed)
            renderObjectToStream(UpnpUtils.DIDL_END);
        // ((SimpleDrawStream) proxied).writeBytes(UpnpUtils.DIDL_END.getBytes());
        closed = true;
    }

    public String toString() {
        return proxied.toString();
    }
}
