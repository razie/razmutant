package com.razie.assets.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.razie.assets.AssetImpl;
import com.razie.assets.AssetInventory;
import com.razie.assets.InventoryAssetMgr;
import com.razie.dist.db.AgentDb;
import com.razie.media.ToDeleteMediaInventory;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.draw.Drawable;
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;
import com.razie.pub.media.players.SdkPlayer;
import com.razie.pub.resources.RazIcons;

/**
 * links are saved in "links" database
 */
public class XmlAssetInventory implements AssetInventory {
   String clsName;

   /** use the base and add details */
   public Map<AssetKey, AssetBrief> find(String type, AssetLocation env, boolean recurse) {
      Map<AssetKey, AssetBrief> ret = new HashMap<AssetKey, AssetBrief>();
   
      for (Element link : AgentDb.db(clsName).xml().listEntities("/db/" + clsName)) {
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
      AssetBrief b = new AssetBrief();

      b.setKey(new AssetKey(clsName, link.getAttribute("key"), new AssetLocation()));
      b.player = "internet";
      b.setFileName("");
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
      List<Element> links = AgentDb.db("links").xml().listEntities("/db/link[@url='" + ref.getId() + "']");

      if (links.size() > 0) {
         return brief(links.get(0));
      }

      return null;
   }

   public ActionItem[] getSupportedActions(AssetKey ref) {
      return defaultCmds;
   }

   public Object playAsset(String prefPlayerNm, AssetKey ref) {
      return ToDeleteMediaInventory.simplePlayAsset(prefPlayerNm, ref);
   }

   private static final ActionItem[] defaultCmds = { AssetBrief.PLAY, AssetBrief.DELETE };

   @Override
   public Drawable details(AssetBrief asset) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object doAction(String cmd, AssetKey ref, ScriptContext ctx) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object get(AssetKey ref) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void init(Meta meta) {
      // nothing to init
   }
}
