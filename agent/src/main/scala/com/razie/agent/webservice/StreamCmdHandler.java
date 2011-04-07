package com.razie.agent.webservice;

import java.io.IOException;
import java.util.Properties;

import razie.draw.DrawStream;
import razie.draw.HttpDrawStream;
import razie.draw.SimpleDrawStream;
import razie.draw.Technology;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.MyServerSocket;
import razie.draw.JsonDrawStream;
import com.razie.pub.http.SocketCmdHandler;

/**
 * this bridges to execute the command on streams rather than plain sockets...
 * 
 * @author razvanc
 */
public abstract class StreamCmdHandler implements SocketCmdHandler {

	public Object execute(String cmdName, String protocol, String args,
			Properties parms, DrawStream out) throws AuthException {
		String m = "execute cmdName=" + cmdName + ", protocol=" + protocol
				+ ", args=" + args;
		logger.log(m);
		return args;
	}

	public Object execServer(String cmdName, String protocol,
			String args, Properties parms, MyServerSocket socket)
			throws AuthException {
		DrawStream out;
		try {
			if ("http".equals(protocol))
				out = new HttpDrawStream(socket.from, socket.getOutputStream());
			else if ("json".equals(protocol))
				out = new JsonDrawStream(socket);
			else
				out = new SimpleDrawStream(Technology.TEXT, socket
						.getOutputStream());
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}

		return execute(cmdName, protocol, args, parms, out);
	}

	public abstract String[] getSupportedActions();

	static final Log logger = Log.factory.create("", Impl.class.getName());
}