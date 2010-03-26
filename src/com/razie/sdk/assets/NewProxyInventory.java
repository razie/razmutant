/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.sdk.assets;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.base.data.XmlDoc;

import com.razie.assets.FileInventory;
import com.razie.pub.base.log.Log;

/**
 * proxy generic invnetory, for smart assets. It will keep instances in a map...
 * 
 * TODO detailed docs
 * 
 * @author razvanc
 * 
 */
public class NewProxyInventory extends FileInventory {

    Map<AssetKey, AssetBase> assets = new HashMap<AssetKey, AssetBase>();

    public NewProxyInventory() {
        Log.logThis("INIT proxyiinv");
    }

    /** find this inventory in assetmgr and register each object */
    public void register(AssetKey k, AssetBase o) {
        assets.put(k, o);
    }

    /** use the base and add details */
    public AssetMap queryAll(String meta, AssetLocation env, boolean recurse, AssetMap ret) {

        for (AssetKey k : assets.keySet())
            if (k.getType().equals(meta))
                ret.put(k, assets.get(k).getBrief());

        return ret;
    }

    public AssetBrief getBrief(AssetKey ref) {
        return getAsset(ref).getBrief();
    }

    @Override
    public AssetBase getAsset(AssetKey ref) {
        return assets.get(ref);
    }

    static void init(XmlDoc file) {
        for (Element e : file.xpl("/config/newassets/assets")) {
            String xpath = e.getAttribute("xpath");

            for (Element o : file.xpl(xpath)) {

            }
        }
    }
}
