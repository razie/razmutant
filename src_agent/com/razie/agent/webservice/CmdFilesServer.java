package com.razie.agent.webservice;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

import com.razie.pub.agent.AgentFileService;
import com.razie.pub.comms.CommRtException;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.comms.PermType;
import com.razie.pub.http.SocketCmdHandler;

public class CmdFilesServer extends SocketCmdHandler.Impl implements SocketCmdHandler {

    public Object execServer(String cmdName, String protocol, String args, Properties parms, MyServerSocket server) {
        String m = "execute cmdName=" + cmdName + ", protocol=" + protocol + ", args=" + args;
        logger.log(m);

        try {
            if ("copyFrom".equals(cmdName)) {
        	server.auth(PermType.WRITE);
                logger.log("SERVER_EXEC_CMD copyFrom...");
                logger.log("   copyFromMe file[" + args + "]");
                // copy from me, args = localfilename
                String urlSrc = args;
                
                // special mutantdir replacement
                if (urlSrc.startsWith("/mutantdir")) {
                    String mutantdir = AgentFileService.getInstance().basePath();
                    urlSrc = urlSrc.replaceFirst("/mutantdir", mutantdir);
                }

                PrintStream out = new PrintStream(server.getOutputStream());

                URL aurl = null;
                if (urlSrc.startsWith("file:") || urlSrc.startsWith("http:")) {
                    aurl = new URL(urlSrc);
                } else {
                    File file = new File(urlSrc);
                    aurl = file.toURL();
                }
                
                InputStream s = aurl.openStream();

                AgentFileService.copyStream(s, out);

                return null;
            } else if ("copyTo".equals(cmdName)) {
        	server.auth(PermType.WRITE);
                logger.log("SERVER_EXEC_CMD copyTo...");
                logger.log("   copyToMe file[" + args + "]");
                // copy to me, args = localfilename
                String fileDest = args;

                DataInputStream in = new DataInputStream(server.getInputStream());

                File destTmpFile = new File(fileDest + ".TMP");
                if (!destTmpFile.getParentFile().exists())
                    destTmpFile.getParentFile().mkdirs();
                destTmpFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(destTmpFile);

                AgentFileService.copyStream(in, fos);

                File destFile = new File(fileDest);
                if (!destTmpFile.renameTo(destFile)) {
                    destFile.delete();
                    if (!destTmpFile.renameTo(destFile)) {
                        throw new CommRtException("Cannot rename...");
                    }
                }
            } else {
                logger.log("UNKNOWN_CMD");
            }
        } catch (IOException e1) {
            throw new CommRtException("command failed: " + cmdName + " " + args);
        }

        return args;
    }

    public String[] getSupportedActions() {
        return COMMANDS;
    }

    static final String[] COMMANDS = { "copyFrom", "copyTo"};
    static final Log      logger   = Log.factory.create(CmdFilesServer.class.getName());

}
