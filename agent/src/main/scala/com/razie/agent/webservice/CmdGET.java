package com.razie.agent.webservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import razie.assets.AssetKey;
import razie.draw.widgets.DrawError;

import com.razie.agent.AgentConfig;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.Comms;
import com.razie.pub.comms.HttpHelper;
import com.razie.pub.comms.LightAuthBase;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.comms.PermType;
import com.razie.pub.comms.SedFilter;
import com.razie.pub.http.LightCmdGET;
import com.razie.pub.http.SocketCmdHandler;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.webui.MutantPresentation;

/**
 * specific mutant conventions are hardcoded here...
 * 
 * @author razvanc
 */
public class CmdGET extends LightCmdGET {

    /**
     * overload this to implement authentication/authorization etc
     * 
     * @param path is the path the user wants: the entire url after GET
     * @return null to disable serving or the path to serve. normally you remove auth tokens and
     *         resolve virtual mappings etc
     * @throws AuthException
     */
    @Override
    protected String shouldServe(MyServerSocket socket, String path) throws AuthException {
       // /public is my convention for classpath 
        if (path.startsWith("/public/") || path.equals("/favicon.ico")) {
            return path;
        }

        return LightAuthBase.unwrapUrl(path);
    }

    /**
     * overload this to serve only what you want so you only serve those files...MAKE sure you avoid
     * stupid attacks like "/../../windows/windows.exe" or etc. THIS class is not intended for
     * connections open to the internet...
     * 
     * @param path is the path the user wants: the entire url after GET, with all ?& parms removed
     * @return null to disable serving or the URL of the file to serve. This is where you resolve
     *         mappings and disable access to folders. for instance, map "/" to "c:/myserver/public"
     *         so you only serve those files...MAKE sure you avoid stupid attacks like
     *         "/../../windows/windows.exe" or etc.
     * @throws MalformedURLException
     * @throws AuthException
     */
    @Override
    protected URL findUrlToServe(MyServerSocket socket, String path, Properties parms)
            throws MalformedURLException, AuthException {
        // TODO 1-1 i'm not protected against /mutant/xxx/../../../../windows.exe
        // TODO make the URL canonical and check again
       
        if (path.startsWith("razGetPic") && HttpHelper.isImage(path)) {
            // special plugin only for jpg files
            String p = path.replaceFirst("razGetPic/", "");
            return new File(p).toURI().toURL();
        } else if (path.startsWith("razStream")) {
           // special service to stream files
            socket.auth(PermType.VIEW);
            String r = parms.getProperty("ref");
            AssetKey ref = AssetKey.fromString(HttpUtils.fromUrlEncodedString(r));
            String p = ref.getLocation().getLocalPath() + ref.getId();
            // TODO 1-1 what if it's a n asset (File , c:\\autoexec.bat) ?
            return new File(p).toURI().toURL();
        } else if (path.equals("") || path.equals("/favicon.ico") || path.startsWith("/public/") || path.startsWith ("local/")) {
            String filenm = path;
            if ((path.equals(""))) {
                filenm = "/public/index.html";
            } else if (path.equals("/favicon.ico")) {
                filenm = "/public/favicon.ico";
                try {
                    // TODO I have an issue on some web browsers waiting for
                    // this thing
                    // blocked...don't remember which ones
                    socket.close();
                } catch (IOException e) {
                    // nothing i can do - don't care
                }
                return null;
            } else {
               // in case someone uses /public without having to...
//                filenm = path.replaceFirst("/mutant/public", "/public");
//                filenm = filenm.replaceFirst("/mutant", "/public");
            }

            URL url = null;

            if (path.startsWith("local")) {
                filenm = path.replaceFirst("local", AgentConfig.getMutantDir());
                try {
                    url = new File(filenm).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                if (filenm.matches("/public/page\\..*\\.html")) {
                    url = new URL("http://" + filenm.replaceFirst("/public/", ""));
                } else
                   // TODO 2-1 reverse this and have the public folder overwrite the classpath if users don't like my pics
                    url = this.getClass().getResource(filenm);
                
                if (url == null && filenm.startsWith ("/public")) {
                   // try one last thing: the "public" folder
                   // TODO 1-1 SECU normalize and protect against ../../../
                   filenm = filenm.replaceFirst("/public", AgentConfig.getMutantDir()+"/public");
                   try {
                       File f = new File(filenm);
                       if (! f.getCanonicalPath().startsWith(AgentConfig.getMutantDir()))
                          throw new IllegalArgumentException ("he he he");
                       url = new File(filenm).toURI().toURL();
                   } catch (MalformedURLException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
                }
            }

            return url;
        }

        return null;
    }

//    /**
//     * overload this if you want to serve lightsoa bindings at certain urls (for isntance servlets
//     * can have a name "myservlet1/dothis" vs "myservlet2/dothis" different than serving the file
//     * "myservlet1.file"
//     * 
//     * @param path is the path the user wants: the entire url after GET, with all ?& parms removed
//     * @return null if this doesn't map to a known soa path or non-null containing the new path to
//     *         use for a soa call (the action name)
//     */
//    @Override
//    protected String findSoaToCall(MyServerSocket socket, String path, Properties parms) {
//      String newpath = path;
//      if (path.startsWith(".text/") || path.startsWith(".json/")) 
//        newpath = path.replaceAll("[^/]*/", "");
//
//      for (HttpSoaBinding c : AgentHttpService.getBindings()) {
//        if (newpath.startsWith(c.getServiceName())) {
//          return newpath;
//        }
//      }
//      
//      return null;
//    }

    /**
     * call the actual soa service with the command you just identified
     * 
     * @param socket
     * @param originalPath
     * @param cmd
     * @param cmdargs
     * @param parms
     * @return
     */
    @Override
    protected Object callSoa(MyServerSocket socket, String originalPath, String svc, String cmd,
            String cmdargs, Properties parms) {
        String p = "http";
        if (originalPath.startsWith(".text/"))
            p = "text";
        else if (originalPath.startsWith(".json/"))
            p = "json";

        Object reply = "";
        boolean foundOldListener = false;

        if ("cmd".equals(svc)) {
            // old style commands
            for (SocketCmdHandler c : AgentHttpService.getHandlers()) {
                for (String s : c.getSupportedActions()) {
                    if (cmd.equals(s)) {
                        logger.trace(1, "HTTP_FOUND_LISTENER_FOR_CMD: " + c.getClass().getName());
                        try {
                            return c.execServer(cmd, p, cmdargs, parms, socket);
                        } catch (AuthException e) {
                            return "Sorry...not enough karma gefunded... " + e.getLocalizedMessage();
                        } catch (Throwable e) {
                            logger.log("ERR_HTTP_INVOKING_CMD: ", e);
                            return new DrawError("ERR_HTTP_INVOKING_CMD: ", e);
                        }
                    }
                }
            }
        }

        if (!foundOldListener) {
            // try the new soa bridges
            for (HttpSoaBinding c : AgentHttpService.getBindings()) {
                if (c.getServiceName().equals(svc)) {
                    boolean callThisOne = false;

                    // TODO the /asset/ prefix is hardcoded for now...this
                    // points to some
                    // limitations...
                    if ("asset".equals(svc)) {
                        callThisOne = true;
                    } else
                        for (String s : c.getSoaMethods()) {
                            if (cmd.equals(s)) {
                                callThisOne = true;
                            }
                        }
                    if (callThisOne) {
                        logger.trace(1, "HTTP_FOUND_SOA_BRIDGE: " + c.getClass().getName());
                        try {
                            return c.execServer(cmd, p, cmdargs, parms, socket);
                        } catch (Throwable e) {
                            logger.log("ERR_HTTP_INVOKING_SOA: ", e);
                            return new DrawError("ERR_HTTP_INVOKING_SOA: ", e);
                        }
                    } else {
                    	// TODO set server error code when returning DrawError
                        logger.alarm("ERR_HTTP_INVOKING_SOA: no such service/method: " + svc + "/" + cmd);
                        return new DrawError("ERR_HTTP_INVOKING_SOA: no such service/method: " + svc + "/"
                                + cmd);
                    }
                }
            }
        }

        return reply;
    }

    /**
     * @param socket
     * @param path
     * @return
     */
    @Override
    protected Object serveFile(MyServerSocket socket, String filenm, URL url) {
        // // special test
        // if (filenm.startsWith("/public/t")) {
        // LightCmdGET.copyStream(in, out);
        // return null;
        // }

        // litle tiny JSP crap
        if (filenm.matches("http://page\\..*\\.html")) {
            String page = filenm.replaceFirst("http://", "").replaceFirst("\\.html", "");
            String ret = MutantPresentation.getInstance().page(page);
            return HttpHelper.httpWrap(HttpHelper.OK, ret, 0); // this htmlWraps() as well...
        } else if (filenm.endsWith(".html")) {
            PrintStream out = null;
            InputStream in = null;

            try {
                out = new PrintStream(socket.getOutputStream());
                in = url.openStream();
            } catch (IOException e) {
                return HttpHelper.httpWrap(HttpHelper.EXC, e.toString(), 0);
            }

            Comms.copyStreamSED(in, out, MPRES);
            return null;
        }

        return super.serveFile(socket, filenm, url);
    }
    
    static final List<SedFilter> MPRES = new ArrayList<SedFilter>();
    static {
       MPRES.add (MutantPresentation.getInstance());
    }

    static final Log logger = Log.factory.create("", CmdGET.class.getName());
}
