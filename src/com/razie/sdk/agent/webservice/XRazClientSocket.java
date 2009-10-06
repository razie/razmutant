package com.razie.sdk.agent.webservice;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.razie.agent.webservice.Server;

/** Raz's stupid authentication wrapped as a Socket
 * 
 * TODO detailed docs
 *@deprecated can delete
 * @author razvanc
 */
public class XRazClientSocket extends Socket {
    public XRazClientSocket(String h, int port) throws IOException {
        super();
        this.connect(new InetSocketAddress(h, port));
        PrintStream out;
        out = new PrintStream(getOutputStream());
        out.println(Server.PWD);
    }

    public XRazClientSocket(String h, int port, int timeout) throws IOException {
        super();
        // timeout the connection quickly
        this.connect(new InetSocketAddress(h, port), timeout);
        PrintStream out;
        out = new PrintStream(getOutputStream());
        out.println(Server.PWD);
    }
}
