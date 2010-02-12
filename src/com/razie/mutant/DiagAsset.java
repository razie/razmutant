package com.razie.mutant;

import razie.JAS;
import razie.assets.AssetActionToInvoke;
import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.Meta;
import razie.assets.ProxyInventory;
import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.draw.DrawSequence;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.HttpDrawStream;
import razie.draw.Technology;
import razie.draw.widgets.DrawToString;

import com.razie.assets.CoolAsset;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.base.TimeOfDay;
import com.razie.pub.comms.Agents;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIcons;
import com.razie.pub.webui.DrawLater;

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

        JAS.manage(this, new Meta(META, "", this.getClass().getName(),
                ProxyInventory.class.getName(), ""));
    }

    public AssetBrief getBrief() {
        super.getBrief();
        this.brief().setName("");
        this.brief().setBriefDesc(sayboo());
        this.brief().setLargeDesc("");
        return this.brief();
    }

    @SoaMethod(descr = "say boo")
    public String sayboo() {
            return "Hi, I'm the DiagAsset key: " + this.getKey().toString();
    }

    @SoaMethod(descr = "say boo", args={"msg"})
    public String sayboo2(String msg) {
            return "Hi, I'm the DiagAsset key: " + this.getKey().toString()+" msg="+msg;
    }

    @Override
    public Object render(Technology t, DrawStream stream) {
        ActionToInvoke ati = new AssetActionToInvoke(Agents.instance().me().url, this.getKey(), DETAILS);
        return new DrawSequence(super.render(t, stream), new DrawLater(ati));
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

    public ActionItem              DETAILS  = new ActionItem("details", RazIcons.UNKNOWN.name());

    public ActionItem whatAreYouDoing() {
        return META;
    }

    public ActionItem whoAreYou() {
        return META;
    }
}
