package com.razie.agent.webservice;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/** Raz's stupid authentication wrapped as a Socket
 * 
 * TODO 3 remove
 * @deprecated to delete
 * 
 * @author razvanc
 */
public class RazClientSocket extends Socket {
	public RazClientSocket(String h, int port) throws IOException {
		super();
		this.connect(new InetSocketAddress(h, port));
//		PrintStream out;
//		out = new PrintStream(getOutputStream());
//      out.println(Server.PWD);
	}

	public RazClientSocket(String h, int port, int timeout) throws IOException {
		super();
		// timeout the connection quickly
		this.connect(new InetSocketAddress(h, port), timeout);
//		PrintStream out;
//		out = new PrintStream(getOutputStream());
//      out.println(Server.PWD);
	}
}
