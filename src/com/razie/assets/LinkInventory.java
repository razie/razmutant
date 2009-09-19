package com.razie.assets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.razie.dist.db.AgentDb;
import com.razie.media.ToDeleteMediaInventory;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;
import com.razie.pub.media.players.SdkPlayer;
import com.razie.pub.resources.RazIcons;

/**
 * links are saved in "links" database
 * 
 */
public class LinkInventory extends ToDeleteMediaInventory {
    public static final String sCLASS_Link = "Link";

    /** use the base and add details */
    public Map<AssetKey, AssetBrief> find(String type, AssetLocation env, boolean recurse) {
        Map<AssetKey, AssetBrief> ret = new HashMap<AssetKey, AssetBrief>();

        for (Element link : AgentDb.db("links").xml().listEntities("/db/link")) {
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

        String url = link.getAttribute("url");
        b.setKey(new AssetKey(sCLASS_Link, link.getAttribute("url"), new AssetLocation(url)));
        b.player = "internet";
        b.setFileName("");
        b.setLocalDir(url);
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
        List<Element> links = AgentDb.db("links").xml().listEntities(
                "/db/link[@url='" + ref.getId() + "']");

        if (links.size() > 0) {
            return brief(links.get(0));
        }

        return null;
    }

    public ActionItem[] getSupportedActions(AssetKey ref) {
        return defaultCmds;
    }

    @Override
    public void delete(AssetKey ref) {
            AgentDb.db("links").xml().delete("/db", "link", new AttrAccess.Impl("url", ref.getId()));
            AgentDb.db("links").save(false, true);
    }

    public Object playAsset(String prefPlayerNm, AssetKey ref) {
       return ToDeleteMediaInventory.simplePlayAsset(prefPlayerNm, ref);
    }

    public static final ActionItem    cmdUPDATESERIES = new ActionItem("updateseries", RazIcons.POWER);
    private static final ActionItem[] defaultCmds     = { AssetBrief.PLAY, AssetBrief.DELETE };
}
