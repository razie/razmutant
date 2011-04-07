/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.playground.xx;

import java.util.Map;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.base.ActionItem;
import razie.base.scripting.ScriptContext;
import razie.draw.Drawable3;

/**
 * inventories manage entities - basic functionality is locating assets
 * 
 * inventories should not be accessed directly, but via the AssetMgr.
 * 
 */
public interface AssetInventory {
   /** get an asset by key */
   public Object get(AssetKey ref);

   /**
    * get/make the brief for an asset given its key. The idea around briefs is that I don't always need the
    * full asset - often i can get around by just a proxy brief
    */
   public AssetBrief getBrief(AssetKey ref);

   /** list all assets of the given type at the given location */
   public Map<AssetKey, AssetBrief> find(String type, AssetLocation env, boolean recurse);

   /** execute command on asset. the asset can be local or remote */
   public Object doAction(String cmd, AssetKey ref, ScriptContext... ctx);

   public ActionItem[] getSupportedActions(AssetKey ref);

   /** get some extra details about an asset */
   public Drawable3 details(AssetBrief asset);

}
