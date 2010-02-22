package com.razie.assets;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.FileAssetBrief;
import razie.assets.FileAssetBriefImpl;

import com.razie.dist.db.AgentDb;
import com.razie.media.ScalaMediaInventoryBik;
import com.razie.pub.comms.Agents;

/**
 * internet streams - are currently only obtained from user.xml
 * 
 */
public class SiteInventory extends ScalaMediaInventoryBik {
    public static final String sCLASS_SITE = "Site";

    public AssetMap queryAll(String type, AssetLocation env, boolean recurse, AssetMap toUse) {
        AssetMap ret = new AssetMap();

        for (Element stream : AgentDb.db("sites").xml().listEntities("/db/sites/site")) {
            FileAssetBriefImpl b = new FileAssetBriefImpl();

            String url = stream.getAttribute("url");
            b.setKey(new AssetKey("Site", stream.getAttribute("url"), AssetLocation.mutantEnv(Agents.getMyHostName(), "")));
            b.setPlayer ( stream.getAttribute("player"));
            b.setFileName("");
            b.setLocalDir(url);
            b.setIcon(stream.getAttribute("icon"));

            b.setBriefDesc(stream.getAttribute("desc"));
            b.setLargeDesc(stream.getAttribute("desc"));

            b.setName(stream.getAttribute("name"));
            ret.put(b.getKey(), b);
        }
        return ret;
    }

    public AssetBrief getBrief(AssetKey ref) {
        AssetBrief b = super.getBrief(ref);
        FileAssetBrief fb = (FileAssetBrief)b;
        b.setIcon("/public/pics/bb_mov_.png");

        fb.setFileName("");
        fb.setLocalDir(ref.getId());
        return b;
    }
}
