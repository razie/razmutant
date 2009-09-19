package com.razie.assets;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.razie.dist.db.AgentDb;
import com.razie.media.ToDeleteMediaInventory;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.comms.Agents;

/**
 * internet streams - are currently only obtained from user.xml
 * 
 */
public class SiteInventory extends ToDeleteMediaInventory {
    public static final String sCLASS_SITE = "Site";

    public Map<AssetKey, AssetBrief> find(String type, AssetLocation env, boolean recurse) {
        Map<AssetKey, AssetBrief> ret = new HashMap<AssetKey, AssetBrief>();

        for (Element stream : AgentDb.db("sites").xml().listEntities("/db/sites/site")) {
            AssetBrief b = new AssetBrief();

            String url = stream.getAttribute("url");
            b.setKey(new AssetKey("Site", stream.getAttribute("url"), AssetLocation.mutantEnv(Agents.getMyHostName(), "")));
            b.player = stream.getAttribute("player");
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
        b.setIcon("/mutant/pics/bb_mov_.png");

        b.setFileName("");
        b.setLocalDir(ref.getId());
        return b;
    }
}
