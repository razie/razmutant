/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.assets.xml;

import java.util.List;

import org.w3c.dom.Element;

import razie.assets.AllOfType;
import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetBriefImpl;
import razie.assets.AssetImpl;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.FileAssetBrief;
import razie.assets.Meta;
import razie.assets.QueryCriteria;
import razie.base.ActionContext;
import razie.base.ActionItem;

import com.razie.dist.db.AgentDb;
import com.razie.media.ScalaMediaInventoryBik;
import com.razie.pub.assets.JavaAssetInventory;
import com.razie.pub.assets.JavaAssetInventoryBase;
import com.razie.pub.resources.RazIcons;

/**
 * links are saved in "links" database
 */
public class XmlAssetInventory extends JavaAssetInventoryBase implements JavaAssetInventory {
   String clsName;

   /** use the base and add details */
   public AssetMap query(QueryCriteria c, AssetLocation env, boolean recurse, AssetMap ret) {
      AssetMap r = ret;
     if (c instanceof AllOfType) {
        r = queryAll (((AllOfType)c).meta(), env, recurse, ret);
     }
     return r;
   }
   public AssetMap queryAll(String meta, AssetLocation env, boolean recurse, AssetMap ret) {
      for (Element link : AgentDb.db(clsName).xml().xpl("/db/" + clsName)) {
         AssetBrief b = brief(link);
         ret.put(b.getKey(), b);
      }
   
      return ret;
   }

   /**
    * @param link
    * @return
    */
   private AssetBrief brief(Element link) {
      AssetBriefImpl b = new AssetBriefImpl();

      b.setKey(new AssetKey(clsName, link.getAttribute("key")));
      b.setPlayer ( "internet");
      ((FileAssetBrief)b).setFileName("");
      //b.setLocalDir(url);
      b.setBriefDesc(link.getAttribute("type"));
      b.setLargeDesc(link.getAttribute("desc"));

      if (link.getAttribute("type").equals("Movie")) {
         b.setImage(RazIcons.TYPE_MOVIE.name());
      } else if (link.getAttribute("type").equals("Channel")) {
         b.setImage(RazIcons.TYPE_CHANNEL.name());
      } else if (link.getAttribute("type").equals("Site")) {
         b.setImage(RazIcons.TYPE_SITE.name());
      } else if (link.getAttribute("type").equals("Stream")) {
         b.setImage(RazIcons.TYPE_STREAM.name());
      }

      b.setName(b.getKey().getId());

      return b;
   }

   public AssetBrief getBrief(AssetKey ref) {
      // use the list to prevent idiotic issues with duplicates...shit hapens...
      List<Element> links = AgentDb.db("links").xml().xpl("/db/link[@url='" + ref.getId() + "']");

      if (links.size() > 0) {
         return brief(links.get(0));
      }

      return null;
   }

   public ActionItem[] getSupportedActions(AssetKey ref) {
      return defaultCmds;
   }

   public Object playAsset(String prefPlayerNm, AssetKey ref) {
      return ScalaMediaInventoryBik.playAsset("internet", new AssetImpl(getBrief(ref)));
   }

   private static final ActionItem[] defaultCmds = { AssetBrief$.MODULE$.PLAY(), AssetBrief$.MODULE$.DELETE() };

   @Override
   public Object doAction(String cmd, AssetKey ref, ActionContext ctx) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getAsset(AssetKey ref) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void init(Meta meta) {
      // nothing to init
   }
}
