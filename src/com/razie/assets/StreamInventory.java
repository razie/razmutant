package com.razie.assets;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetImpl;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.FileAssetBrief;
import razie.assets.FileAssetBriefImpl;

import com.razie.dist.db.AgentDb;
import com.razie.media.ScalaMediaInventoryBik;

/**
 * internet streams - are currently only obtained from user.xml
 * 
 */
public class StreamInventory extends ScalaMediaInventoryBik {
    public static final String sCLASS_STREAM = "Stream";

    public AssetMap queryAll(String type, AssetLocation env, boolean recurse, AssetMap ret) {

        for (Element stream : AgentDb.db("streams").xml().listEntities("/db/streams/stream")) {
            FileAssetBriefImpl b = new FileAssetBriefImpl();

            String url = stream.getAttribute("url");
            b.setKey(new AssetKey(sCLASS_STREAM, stream.getAttribute("name"), new AssetLocation(url)));
            b.setPlayer ( stream.getAttribute("player"));
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
        ((FileAssetBrief)b).setFileName("");
        ((FileAssetBrief)b).setLocalDir(ref.getLocation().toHttp());
        b.setIcon("/public/pics/bb_mov_.png");
        return b;
    }

    @Override
    public Object playRef(String prefPlayerNm, AssetKey ref) {
       return playAsset("internet", new AssetImpl(getBrief(ref)));
    }

}
