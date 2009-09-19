package com.razie.comm.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.w3c.dom.Element;

import com.razie.agent.config.AgentConfig;
import com.razie.assets.AssetService;
import com.razie.media.MediaUtils;
import com.razie.media.SeriesInventory;
import com.razie.media.config.MediaConfig;
import com.razie.pub.FileUtils;
import com.razie.pub.agent.AgentFileService;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.AssetMgr;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.base.data.XmlDoc.Reg;
import com.razie.pub.base.exceptions.CommRtException;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.AuthException;
import com.razie.pub.comms.LightAuth;
import com.razie.pub.comms.MyServerSocket;
import com.razie.pub.draw.DrawStream;
import com.razie.pub.draw.Drawable;
import com.razie.pub.draw.HttpDrawStream;
import com.razie.pub.draw.JsonDrawStream;
import com.razie.pub.draw.SimpleDrawStream;
import com.razie.pub.draw.Renderer.Technology;
import com.razie.pub.draw.widgets.DrawToString;
import com.razie.pub.http.SocketCmdHandler;
import com.razie.pub.http.StreamConsumedReply;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.pubstage.comms.StrFilter;
import com.razie.sdk.TempFilesRazie;
import com.razie.sdk.assets.providers.MutantProvider;

/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 * 
 *         TODO turn into a SOA service - break into player and asset cmd
 * 
 *         TODO complete migration to AssetService
 */
@SoaService(name = "oldassets", descr = "asset management")
public class CmdAssets extends SocketCmdHandler.Impl {

   public CmdAssets() {
   }

   public Object execServer(String cmdName, String protocol, String args, Properties parms,
           MyServerSocket socket) throws AuthException {
      String reply = "";

      DrawStream out;
      try {
         if ("http".equals(protocol)) {
            out = new HttpDrawStream(socket);
         } else if ("json".equals(protocol)) {
            out = new JsonDrawStream(socket);
         } else {
            out = new SimpleDrawStream(Technology.TEXT, socket.getOutputStream());
         }
      } catch (IOException e2) {
         throw new RuntimeException(e2);
      }

      if ("list".equals(cmdName) || "listAll".equals(cmdName)) {
         socket.auth(LightAuth.PermType.PUBLIC);
         list(cmdName, protocol, args, parms, out);
         out.close();
         return new StreamConsumedReply();
      } else if ("browse".equals(cmdName)) {
         socket.auth(LightAuth.PermType.PUBLIC);
         String sref = HttpUtils.fromUrlEncodedString(parms.getProperty("ref"));
         AssetKey ref = AssetKey.fromString(sref);
         MediaUtils.browse(protocol, ref, out);
         out.close();
         return new StreamConsumedReply();
      } else if ("invcmd".equals(cmdName)) {
         socket.auth(LightAuth.PermType.CONTROL);
         String cmd = parms.getProperty("cmd");
         AssetKey ref = AssetKey.fromString(parms.getProperty("ref"));
         Object d = AssetMgr.doAction(cmd, ref, new ScriptContext.Impl(parms));
         out.write(d == null ? "<NULL>" : d);
         out.close();
         return new StreamConsumedReply();
      } else if ("play".equals(cmdName)) {
         socket.auth(LightAuth.PermType.CONTROL);
         String player = parms.getProperty("player");
         AssetKey ref = AssetKey.fromString(parms.getProperty("ref"));
         out.write(playLocal(player, ref));
         out.close();
         return new StreamConsumedReply();
      } else if ("playepisode".equals(cmdName)) {
         socket.auth(LightAuth.PermType.CONTROL);
         String player = parms.getProperty("player");
         AssetKey ref = AssetKey.fromString(parms.getProperty("ref"));
         AssetKey series = AssetKey.fromString(parms.getProperty("series"));
         out.write(playLocal(player, ref));
         SeriesInventory.seriesold(series, ref);
         out.close();
         return new StreamConsumedReply();
      } else if ("remoteDetails".equals(cmdName)) {
         String sref = parms.getProperty("ref");
         AssetKey ref = AssetKey.fromString(sref);
         out.write(remoteDetails(ref, parms.getProperty("host"), parms.containsKey("series") ? AssetKey.fromString(parms.getProperty("series")) : null));
         out.close();
         return new StreamConsumedReply();
      } else if ("saveJpg".equals(cmdName)) {
         socket.auth(LightAuth.PermType.WRITE);
         String[] largess = args.split("&", 2);
         if (largess.length > 1 && largess[1].length() > 0) {
            String[] ss = largess[0].split("/", 2);
            AssetKey ref = new AssetKey(ss[0], FileUtils.fileNameFromPath(ss[1]), new AssetLocation(
                    TempFilesRazie.getPathFromFile(ss[1])));
            reply = saveJpg(ref, largess[1]);
         } else {
            return "Append the url of the JPG to this url (after =) and hit enter...";
         }
      }
      return reply;
   }

   private String saveJpg(AssetKey ref, String url) {
      // 1. make up the picture's name
      AssetBrief movie = AssetMgr.brief(ref);

      // TODO the remote paths come here without a / - fix that!
      if (movie.getLocalDir().startsWith("/")) {
         movie.setLocalDir("/" + movie.getLocalDir());
      }

      // TODO check and confirm if it already had one...

      String fname = movie.getFileName();
      fname = fname.replaceFirst("\\.[a-zA-Z0-9]+$", ".jpg");

      String fileDest = movie.getLocalDir() + fname;

      try {

         if (new File(fileDest).exists()) {
            new File(fileDest).delete();
         }

         // 2. get the remote
         // 3. save as local

         URL aurl = null;
         if (url.startsWith("file:") || url.startsWith("http:")) {
            aurl = new URL(url);
         } else {
            File file = new File(url);
            aurl = file.toURL();
         }
         InputStream in = aurl.openStream();

         // TODO copy/paste code - refactor
         File destTmpFile = new File(fileDest + ".TMP");
         if (!destTmpFile.getParentFile().exists()) {
            destTmpFile.getParentFile().mkdirs();
         }
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

         return "Ok...go back to details and refresh to check...";
      } catch (Exception e) {
         return e.toString();
      }
   }

   /**
    * @param cmdName
    * @param protocol
    * @param args
    * @param socket
    * @param reply
    * @return
    */
   void list(String cmdName, String protocol, String args, Properties parms, DrawStream out) {
      // format: type/location
      String type = parms.getProperty("type", "Movie");
      String location = parms.getProperty("location", "");
      String category = parms.getProperty("category", "");

      if (location.length() <= 0 && ("Movie".equals(type) || "Series".equals(type))) {
         // now it's interesting - if location not present, get my defaults
         for (Element e : Reg.doc(MediaConfig.MEDIA_CONFIG).listEntities(
                 "/config/storage/host[@name='" + Agents.me().name + "']/media")) {
            location = e.getAttribute("localdir");

            if (category != null && category.length() > 0 && !("All".equals(category))) {
               // browse all first level folders and follow only the
               // category...if present
               File f = new File(location);
               File[] entries = f.listFiles();

               if (entries != null) {
                  for (File entry : entries) {
                     if (entry.isDirectory()) {
                        String cat = entry.getName();
                        String newLoc = location + "\\" + cat;

                        if (cat.equals(category) || ("Rest".equals(category) && !MediaConfig.getInstance().getCategories().containsKey(cat))) {
                           if (!"json".equals(protocol)) {
                              out.write(newLoc + "\n");
                           }
                           AssetService.listLocal(type, newLoc, true, out);
                        }
                     }
                  }
               }
            } else {
               // no cat - just list all at the location
               if (!"json".equals(protocol)) {
                  out.write(location + "\n");
               }
               AssetService.listLocal(type, location, true, out);
            }
         }
      } else {
         AssetKey loc = AssetKey.fromString(location);
         if (loc.getLocation().isLocal() || "".equals(location)) {
            AssetService.listLocal(type, loc.getId(), true, out);
         } else {
            MutantProvider mutant = new MutantProvider(loc.getLocation().getHost());
            if (mutant.isUp()) {
               String otherList = (String) mutant.list(type, category, null, null, loc.getId()).read();
               out.write(new DrawToString(loc.getLocation().getHost() + ":<br>" + otherList));
            } else {
               out.write(loc.getLocation().getHost() + " - not reacheable...<br>");
            }
         }
      }

      location = parms.getProperty("location", "");
      if ("listAll".equals(cmdName)) {
         // browse all other hosts...
         // for (Element e :
         // Reg.doc(AgentConfig.AGENT_CONFIG).listEntities("/config/clouds/cloud/host"))
         // {
         // String n = e.getAttribute("name");
         // if (!me.equals(n)
         // && (e.getAttribute("type").equals("laptop") ||
         // e.getAttribute("type").equals("desktop"))) {
         for (AgentHandle e : Agents.homeCloud().agents().values()) {
            if (!Agents.me().name.equals(e.name)) {
               MutantProvider mutant = new MutantProvider(e.name);
               if (mutant.isUp()) {
                  String otherList = (String) mutant.list(type, category, null, null, location).read();
                  out.write(new DrawToString(e.name + ":\n" + otherList));
               } else {
                  out.write(e.name + " - not reacheable...\n");
               }
            }
         }
      }
   }

   /** play a given asset with a preferred player */
   private Object playLocal(String player, AssetKey ref) {
      Object o = AssetMgr.doAction("play/" + player, ref, (ScriptContext) null);
      return o;
   }

   /** play a given asset with a preferred player */
   private Drawable remoteDetails(AssetKey ref, String remoteHost, AssetKey series) {
      String myip = Agents.me().ip;

      // i'm sure it's up:
      Element e = Reg.doc(AgentConfig.AGENT_CONFIG).getEntity(
              "/config/clouds/cloud/*[@name='" + remoteHost + "']");
      String remoteIp = Agents.agent(remoteHost).ip;
      String remoteUrl = Agents.agent(remoteHost).url;

      // all good - can go there
      String targetcmd = remoteUrl + "/mutant/assets/";
      String url = targetcmd + "details?ref=" + ref.toUrlEncodedString();
      if (series != null) {
         url += "&series=" + series.toUrlEncodedString();
      }

      StrFilter f1 = MutantProvider.makeAssetsFilter(remoteIp, remoteHost, myip);

      String otherList = new HtmlContents(url, f1).readAll();

      // now, find remote drive mappings
      // TODO find an idea to use unique paths and replace them on the fly
      // when needed or something
      for (Element maping : XmlDoc.listEntities(e, "media")) {
         if (maping.hasAttribute("remote")) {
            String s1 = maping.getAttribute("localdir").replaceAll("\\\\", "/");
            String s2 = "//" + remoteHost + "/" + maping.getAttribute("remote");

            otherList = otherList.replaceAll(s1, s2);
         }
      }

      return new DrawToString(otherList);
   }

   public String[] getSupportedActions() {
      return COMMANDS;
   }
   static final String[] COMMANDS = {"list", "listAll", "browse", "play", "playepisode", "invcmd", "player",
      "remoteDetails", "updateseries", "saveJpg", "testing"};
   static final Log logger = Log.Factory.create("", CmdAssets.class.getName());
}
