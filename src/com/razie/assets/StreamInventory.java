package com.razie.assets;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.razie.dist.db.AgentDb;
import com.razie.media.ToDeleteMediaInventory;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;
import com.razie.pub.media.players.SdkPlayer;

/**
 * internet streams - are currently only obtained from user.xml
 * 
 */
public class StreamInventory extends ToDeleteMediaInventory {
    public static final String sCLASS_STREAM = "Stream";

    public Map<AssetKey, AssetBrief> find(String type, AssetLocation env, boolean recurse) {
        Map<AssetKey, AssetBrief> ret = new HashMap<AssetKey, AssetBrief>();

        for (Element stream : AgentDb.db("streams").xml().listEntities("/db/streams/stream")) {
            AssetBrief b = new AssetBrief();

            String url = stream.getAttribute("url");
            b.setKey(new AssetKey(sCLASS_STREAM, stream.getAttribute("name"), new AssetLocation(url)));
            b.player = stream.getAttribute("player");
            b.setFileName("");
            b.setLocalDir(url);
            b.setIcon(stream.getAttribute("icon"));

            b.setLargeDesc(stream.getAttribute("desc"));

            b.setName(b.getKey().getId());
            ret.put(b.getKey(), b);
        }
        return ret;
    }

    @Override
    public AssetBrief getBrief(AssetKey ref) {
        AssetBrief b = super.getBrief(ref);
        b.setFileName("");
        b.setLocalDir(ref.getLocation().toHttp());
        b.setIcon("/mutant/pics/bb_mov_.png");
        return b;
    }

    @Override
    public Object playAsset(String prefPlayerNm, AssetKey ref) {
       return ToDeleteMediaInventory.simplePlayAsset(prefPlayerNm, ref);
    }

}
