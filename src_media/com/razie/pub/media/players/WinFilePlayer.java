/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import razie.actionables.util.WinExec;
import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetBriefImpl;
import razie.assets.FileAssetBrief;
import razie.base.data.XmlDoc;

import com.razie.media.config.MediaConfig;
import com.razie.pub.base.log.Log;

/**
 * standard file player: will simply execute the file. You need to map the actual windows player for
 * the file type somewhere
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class WinFilePlayer implements SdkPlayer {
   private static AssetBrief brief   = new AssetBriefImpl();
   protected String xpath;
   protected XmlDoc doc;

   static {
      brief.setName("WindowsFilePlayer");
      brief.setBriefDesc("Windows File Player");
      brief.setIcon("c:/video/wmp.PNG");
   }

   public boolean canPlay(AssetBrief m) {
      return true;
   }

   public AssetBrief getBrief() {
      return brief;
   }

   public PlayerHandle play(AssetBase m) {
      FileAssetBrief fab = (FileAssetBrief)m.getBrief();
      String fullpath = fab.getFullPath();

      if (fullpath.contains(" "))
         fullpath = "\"" + fullpath + "\"";
     
      String p = program().replaceAll("\\$\\{asset.path\\}", fullpath);
      p = p.replaceAll("\\$\\{asset.url\\}", m.getBrief().getUrlForStreaming().makeActionUrl());
      p = p.replaceAll("\\$\\{asset.smbpath\\}", fullpath.replaceAll("//", "smb://"));
         
      String reply = "";

      try {
         WinExec.execCmd(shell(), p);
      } catch (Exception e) {
         Log.logThis(e.toString());
         reply += e.toString();
         return new WinPlayerHandle(reply, m.getBrief());
      }
      return new WinPlayerHandle(PlayerHandle.PLAYING, m.getBrief());
   }

   public PlayerHandle makeHandle() {
      return new WinPlayerHandle(PlayerHandle.PLAYING, null);
   }

   // TODO this is static - should reload all the time. pass the document and xpath instead of element
   public void init(XmlDoc doc, String xpath) {
      this.xpath=xpath;
      // TODO i just ignore the doc sent in...
      this.doc = MediaConfig.getInstance();
      String program = doc.xpe(xpath).getAttribute("program");
      String shell = doc.xpe(xpath).getAttribute("shell");
      
      if (program.length() <= 0 || shell.length() <= 0) {
         Log.logThis("ERR_PLAYER_CONFIG WinFilePlayer needs attribute program or shell... name=" + doc.xpe(xpath).getAttribute("name"));
      }
   }
   
   public String program() { init(doc,xpath); return doc.xpe(xpath).getAttribute("program");}
   public String shell() { init(doc,xpath); return doc.xpe(xpath).getAttribute("shell");}
}
