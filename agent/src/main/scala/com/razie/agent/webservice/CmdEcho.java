package com.razie.agent.webservice;

import java.util.Properties;

import razie.draw.DrawStream;

import com.razie.pub.base.log.Log;

/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 * 
 */
public class CmdEcho extends StreamCmdHandler {

    public Object execute(String cmdName, String protocol, String args, Properties parms, DrawStream out) {
        String m = "execute cmdName=" + cmdName + ", protocol=" + protocol + ", args=" + args;
        logger.log(m);
        return args;
    }

    public String[] getSupportedActions() {
        return COMMANDS;
    }

    static final String[] COMMANDS = { "echo" };
    static final Log      logger   = Log.factory.create("", CmdEcho.class.getName());
}
