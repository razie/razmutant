package com.razie.mutant;

import com.razie.assets.CoolAsset;
import com.razie.assets.InventoryAssetMgr;
import com.razie.assets.ProxyInventory;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.assets.AssetActionToInvoke;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetMgr.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.TimeOfDay;
import com.razie.pub.comms.ActionToInvoke;
import com.razie.pub.comms.Agents;
import com.razie.pub.draw.DrawSequence;
import com.razie.pub.draw.DrawStream;
import com.razie.pub.draw.Drawable;
import com.razie.pub.draw.HttpDrawStream;
import com.razie.pub.draw.widgets.DrawLater;
import com.razie.pub.draw.widgets.DrawToString;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIcons;

/**
 * auto-registering diagnostic asset - use to test remotes, management, etc
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaAsset(meta = DiagAsset.sCLASS, descr = "diagnostic asset")
public class DiagAsset extends CoolAsset {
    public static final String     sCLASS    = "Diag.razie";
    public static final ActionItem META      = new ActionItem(sCLASS, "/mutant/pics/IceAgeScrat.png");

    public DiagAsset(String key) {
        super(new AssetKey (sCLASS, key));

        // if not reg yet, reg the class as soa
            AgentHttpService.registerSoaAsset(DiagAsset.class);

        InventoryAssetMgr.registerAsset(this, new Meta(META, "", this.getClass().getName(),
                ProxyInventory.class.getName()));
    }

    public AssetBrief getBrief() {
        super.getBrief();
        this.brief.setName("");
        this.brief.setBriefDesc(sayboo());
        this.brief.setLargeDesc("");
        return this.brief;
    }

    @SoaMethod(descr = "say boo")
    public String sayboo() {
            return "Hi, I'm the DiagAsset key: " + this.getKey().toSimpleString();
    }

    @SoaMethod(descr = "say boo", args={"msg"})
    public String sayboo2(String msg) {
            return "Hi, I'm the DiagAsset key: " + this.getKey().toSimpleString()+" msg="+msg;
    }

    /** play a given asset with a preferred player */
    public Drawable paint(ScriptContext ctx) {
        ActionToInvoke ati = new AssetActionToInvoke(Agents.instance().me().url, this.getKey(), DETAILS);
        return new DrawSequence(super.paint(ctx), new DrawLater(ati));
    }

    /** play a given asset with a preferred player */
    @SoaMethod(descr = "paint more details")
    @SoaStreamable
    public Drawable details(DrawStream out) {
        HttpDrawStream d = (HttpDrawStream) out;
        d.addMeta("<meta http-equiv=\"refresh\" content=\"10\">");
        d.addMeta("<META HTTP-EQUIV=\"Page-Enter\" CONTENT=\"blendTrans (Duration=.5)\">");

        return new DrawToString("It is " + TimeOfDay.calcvalue());
    }

    public ActionItem              DETAILS  = new ActionItem("details", RazIcons.UNKNOWN);

    public ActionItem whatAreYouDoing() {
        return META;
    }

    public ActionItem whoAreYou() {
        return META;
    }
}
