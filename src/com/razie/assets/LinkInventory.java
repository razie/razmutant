/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.assets;

import java.util.List;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetImpl;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.FileAssetBrief;
import razie.assets.FileAssetBriefImpl;

import com.razie.dist.db.AgentDb;
import com.razie.media.ScalaMediaInventoryBik;
import razie.base.ActionItem;
import razie.base.AttrAccessImpl;
import com.razie.pub.resources.RazIcons;

/**
 * links are saved in "links" database
 * 
 */
public class LinkInventory extends ScalaMediaInventoryBik {
    public static final String sCLASS_Link = "Link";

    /** use the base and add details */
    public AssetMap queryAll(String meta, AssetLocation env, boolean recurse, AssetMap ret) {

        for (Element link : AgentDb.db("links").xml().xpl("/db/link")) {
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
        FileAssetBriefImpl b = new FileAssetBriefImpl();

        String url = link.getAttribute("url");
        b.setKey(new AssetKey(sCLASS_Link, link.getAttribute("url"), new AssetLocation(url)));
        b.setPlayer( "internet");
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
        List<Element> links = AgentDb.db("links").xml().xpl(
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
            AgentDb.db("links").xml().delete("/db", "link", new AttrAccessImpl("url", ref.getId()));
            AgentDb.db("links").save(false, true);
    }

    public Object playRef(String prefPlayerNm, AssetKey ref) {
       return playAsset("internet", new AssetImpl(getBrief(ref)));
    }

    public static final ActionItem    cmdUPDATESERIES = new ActionItem("updateseries", RazIcons.POWER.name());
    private static final ActionItem[] defaultCmds     = { AssetBrief$.MODULE$.PLAY(), AssetBrief$.MODULE$.DELETE() };
}
