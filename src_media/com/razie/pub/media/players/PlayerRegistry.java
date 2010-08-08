/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import razie.base.data.XmlDoc;

import com.razie.media.config.MediaConfig;
import com.razie.pub.UnknownRtException;
import com.razie.pub.base.log.Log;
import com.razie.pub.media.MediaErrors;

/**
 * register all players declared in the configuration
 * 
 * TODO reinitialize when xml file changed...
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class PlayerRegistry {
   protected static PlayerRegistry instance() {
      return singleton;
   }

   /**
    * get the player by name
    * 
    * @param name name of player, as configured in players.xml
    * @param platform operating system, as configured
    * @param hostname - hostname...some hosts may override the default players
    * @return
    */
   public static SdkPlayer getPlayer(String name, String platform, String hostname) {
      Map<String, SdkPlayer> ret = instance().byHost.get(hostname);

      if (ret == null)
         ret = instance().byPlatform.get(platform);

      if (ret == null)
         throw new IllegalArgumentException(
               "Platform/os of host doesn't match configured players...platform=" + platform);

      return ret.get(name);
   }

   /**
    * list the players
    * 
    * @param platform operating system, as configured
    * @param hostname - hostname...some hosts may override the default players
    * @return Map<String, SdkPlayer> = <name, player>
    */
   public static Map<String, SdkPlayer> getPlayers(String platform, String hostname) {
      Map<String, SdkPlayer> ret = instance().byHost.get(hostname);

      if (ret == null)
         // no host overrides, return platform directly
         ret = instance().byPlatform.get(platform);
      else {
         // host override: copy and override
         // TODO i can probably optimize this to hapen on startup only?
         ret = new HashMap<String, SdkPlayer>();
         ret.putAll(instance().byPlatform.get(platform));
         ret.putAll(instance().byHost.get(hostname));
      }

      return ret;
   }

   public SdkPlayer annotateWithTag(SdkPlayer p, Element e) {
      return p;
   }

   /** load all metas and instantiate all inventories */
   public static void init(PlayerRegistry p) {
      singleton = p;

      for (Element eplatform : MediaConfig.getInstance().xpl("/config/players/platform")) {
         for (Element eplayer : XmlDoc.xpl(eplatform, "player")) {
            String classnm = eplayer.getAttribute("class");
            String name = eplayer.getAttribute("name");
            String xpath = "/config/players/platform[";
            
                     if (eplatform.hasAttribute("os")) 
                        xpath += "@os=\""+eplatform.getAttribute("os");
                      else if (eplatform.hasAttribute("host")) 
                        xpath += "@host=\""+eplatform.getAttribute("host");

                     xpath += "\"]/player[@name=\""+name+"\"]";
                     
            try {
               SdkPlayer player = null;

               if (eplayer.hasAttribute("aliasto")) {
                  try {
                     String aliasto = eplayer.getAttribute("aliasto");

                     if (eplatform.hasAttribute("os"))
                        player = instance().byPlatform.get(eplatform.getAttribute("os")).get(aliasto);
                     else if (eplatform.hasAttribute("host"))
                        player = instance().byHost.get(eplatform.getAttribute("host")).get(aliasto);
                  } catch (Exception e) {
                     String m = MediaErrors.ERR_MEDIA_CONFIG + "aliased player name=" + name
                           + " MAKE sure it's defined AFTER the aliasto player, in the same <platform> eh?";
                     throw new UnknownRtException(m);
                  }
               } else {
                  player = (SdkPlayer) Class.forName(classnm).newInstance();

                  // TODO should initialize players only if this is its
                  // platform...
                  player.init(MediaConfig.getInstance(), xpath);

                  if (XmlDoc.xpl(eplayer, "actionables").size() > 0) {
                     player = instance().annotateWithTag(player, XmlDoc.xpe(eplayer, "actionables"));
                  }
               }

               if (player == null)
                  throw new UnknownRtException(MediaErrors.ERR_MEDIA_CONFIG + "could not find the player...");

               if (eplatform.hasAttribute("os"))
                  addMap(instance().byPlatform, eplatform.getAttribute("os"), name, player);
               else if (eplatform.hasAttribute("host"))
                  addMap(instance().byHost, eplatform.getAttribute("host"), name, player);
               else
                  throw new UnknownRtException(MediaErrors.ERR_MEDIA_CONFIG
                        + "media.xml:/config/players/platform tag must have either os or host attribute set");

               Log.logThis("CREATE_PLAYER " + name + ", class=" + classnm);
            } catch (Exception e) {
               Log.logThis(MediaErrors.ERR_CANT_CREATE_PLAYER + "name=" + name + " class=" + classnm, e);
            }
         }
      }
   }

   private static void addMap(Map<String, Map<String, SdkPlayer>> m, String key, String name, SdkPlayer p) {
      Map<String, SdkPlayer> mm = m.get(key);
      if (mm == null)
         mm = new HashMap<String, SdkPlayer>();
      mm.put(name, p);
      m.put(key, mm);
   }

   private Map<String, Map<String, SdkPlayer>> byHost     = new HashMap<String, Map<String, SdkPlayer>>();
   private Map<String, Map<String, SdkPlayer>> byPlatform = new HashMap<String, Map<String, SdkPlayer>>();
   protected static PlayerRegistry             singleton;
}
