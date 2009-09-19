package com.razie.dist.play.phantom;

import com.razie.assets.MutantAssetMgr;
import com.razie.assets.ProxyInventory;
import com.razie.assets.cool.CoolAsset;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.AssetMgr.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.draw.Drawable;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.resources.RazIcons;
import com.razie.pubstage.life.Breather;

/**
 * this is a freaky being...likes to jump from agent to agent doing stuff...
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaAsset(meta = "Phantom.razie", base = "Funky.razie", descr = "simple xml distributed agent database")
public class Phantom extends CoolAsset implements Breather {
    public static final String sCLASS   = "Phantom.razie";
    public static String       EVT_SMTH = "newdb.Phantom";

    AssetKey                   key;

    static Phantom             singleton;

    public static Phantom instance() {
        if (singleton == null)
            singleton = new Phantom(new AssetKey(sCLASS, "theone", AssetLocation.mutantEnv(Agents
                    .getMyHostName(), "")));
        return singleton;
    }

    private Phantom(AssetKey ref) {
        super(ref);
        this.key = ref;

        // if not reg yet, reg the class as soa
            AgentHttpService.registerSoaAsset(Phantom.class);

        MutantAssetMgr.registerAsset(this, new Meta(META, "", Phantom.class.getName(), ProxyInventory.class
                .getName()));
    }

    public AssetBrief getBrief() {
        super.getBrief();
        this.brief.setBriefDesc(sayboo());
        return this.brief;
    }

    @SoaMethod(descr = "say boo")
    public String sayboo() {
        return "BOO-HOO-HOO I'm the scary phantom...right now inside " + Agents.getMyHostName();
    }

    /** play a given asset with a preferred player */
    public Drawable paint(ScriptContext ctx) {
        return super.paint(ctx);
    }

    public void breathe() {
        Log.logThis("Phantom is breathing...");
    }

    public ActionItem whatAreYouDoing() {
        return status;
    }

    public ActionItem whoAreYou() {
        return ME;
    }

    public ActionItem              status = new ActionItem("BOO-HOO", RazIcons.UNKNOWN);
    public static final ActionItem ME     = new ActionItem("Phantom", RazIcons.UNKNOWN);
    public static final ActionItem META   = new ActionItem(sCLASS, "/mutant/pics/cold.png");
}
