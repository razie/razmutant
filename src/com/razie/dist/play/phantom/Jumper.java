package com.razie.dist.play.phantom;

import org.w3c.dom.Element;

import com.razie.assets.MutantAssetMgr;
import com.razie.assets.ProxyInventory;
import com.razie.assets.cool.CoolAsset;
import com.razie.dist.db.AgentDb;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.assets.AssetActionToInvoke;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.AssetMgr.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.TimeOfDay;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ActionToInvoke;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.draw.DrawSequence;
import com.razie.pub.draw.DrawStream;
import com.razie.pub.draw.Drawable;
import com.razie.pub.draw.HttpDrawStream;
import com.razie.pub.draw.widgets.DrawLater;
import com.razie.pub.draw.widgets.DrawToString;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaAssetHasState;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIcons;
import com.razie.pubstage.life.Breather;

/**
 * this is a freaky being...likes to jump from agent to agent doing stuff...
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaAsset(meta = "Jumper.razie", base = "Funky.razie", descr = "simple xml distributed agent database")
@SoaAssetHasState
public class Jumper extends CoolAsset implements Breather {
    public static final String     sCLASS    = "Jumper.razie";
    public static String           EVT_SMTH  = "newdb.Jumper";
    public static final ActionItem META      = new ActionItem(sCLASS, "/mutant/pics/IceAgeScrat.png");

    // the jumper will only come alive the first time on this host...i need this until i implement
    // negociation
    public static final String     MAIN_HOST = "TorLp-Razvanc";

    //AssetKey                       key;
    private long                   laps      = 0;
    private long                   hops;
    private String                 myHost;
    private int counter=0;

    private static Jumper          singleton = null;

    public static Jumper instance() {
        if (singleton == null)
            load();
        return singleton;
    }

    private Jumper(AssetKey ref) {
        super(ref);
        this.key = ref;

        // if not reg yet, reg the class as soa
            AgentHttpService.registerSoaAsset(Jumper.class);

        MutantAssetMgr.registerAsset(this, new Meta(META, "", Jumper.class.getName(),
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
        if (Agents.getMyHostName().equals(myHost))
            return "Hi, I'm the Jumper, I'm HERE... (" + myHost + "), took " + laps + " jumps and " + hops
                    + " hops";
        else
            return "Hi, the Jumper is on (" + myHost + "), took " + laps + " jumps and " + hops + " hops";
    }

    public void breathe() {
        if (counter++ < 5)
            return;
        counter = 0;
        if (Agents.getMyHostName().equals(myHost)) {
            logger.log("JUMPER is breathing... " + ++laps + " laps");
            if (laps > 5) {
                String jumpto;
                AgentHandle[] devices = Agents.homeCloud().agents().values().toArray(new AgentHandle[0]);
                AgentHandle d = devices[(int) (Math.random() * devices.length)];

                jumpto = d.name;
                logger.trace(1,"prepare to jump to: " + jumpto);

//                if (new MutantProvider(jumpto).isUp() && !jumpto.equals(myHost)) {
                if (Agents.agent(jumpto).isUpNow() && !jumpto.equals(myHost)) {
                    laps = 0;
                    //jump(jumpto);
                } else
                    logger.trace(1,"AHHH, it's not up: " + jumpto);
            }
        } else {
            if (laps > 20) {
                // been idle for long, should reload db
                // TODO remove this when notifications are working properly...
                AgentDb.reload("assets");
                load();
            }
            logger.log("JUMPER is not here... " + ++laps + " jumps");
        }
    }

    @SoaMethod(descr = "jumper jumping to another agent", args = { "host" })
    public String jump(String host) {
        if (Agents.getMyHostName().equals(myHost)) {
            if (Agents.agent(host) != null) {
                if (Agents.agent(host).isUpNow()) {
                    singleton.myHost = host;
                    singleton.hops++;
                    singleton.ipersist();
                    logger.log("JUMPER jumped on host: " + host);
                    return "OK... now alive on " + host;
                } else
                    return "ERR_ Can't jump there...Host is not up... " + host;
            } else
                return "ERR_ Can't jump there...DONT even know host... " + host;
        } else
            return "I don't live here...find me!";
    }

    @SoaMethod(descr = "come here")
    public String comehere() {
        if (Agents.getMyHostName().equals(myHost)) {
            return "I'm here already...!";
        } else {
            AssetActionToInvoke action = new AssetActionToInvoke(Agents.agent(myHost).url,
                    new AssetKey(sCLASS, this.key.getId(), AssetLocation.mutantEnv(myHost)), new ActionItem(
                            "jump"), "host", Agents.getMyHostName());
            String result = (String) action.act(null);
            return result;
        }
    }

    public ActionItem whatAreYouDoing() {
        status.label = sayboo();
        return status;
    }

    public void persist() {
        // jumper is persisted when it jumps...
        if (myHost.equals(Agents.getMyHostName())) {
            ipersist();
        }
    }

    protected void ipersist() {
        // jumper is persisted when it jumps...
        AgentDb db = AgentDb.db("assets");

        Element jumper = db.xml().getEntity("/db/" + sCLASS);
        if (jumper != null) {
            db.xml().setAttr("/db/" + sCLASS, "key", "theone", "laps", singleton.laps, "hops",
                    singleton.hops, "myHost", singleton.myHost);
            db.save(false, true);
        }
    }

    public static void load() {
        AgentDb db = AgentDb.db("assets");
        Element jumper = db.xml().getEntity("/db/" + sCLASS);

        if (jumper == null) {
            // this must hapen only once ever...
            singleton = new Jumper(new AssetKey(sCLASS, "theone", AssetLocation.mutantEnv(Agents
                    .getMyHostName(), "")));
            // birth on main host only
            singleton.myHost = MAIN_HOST;
            if (Agents.getMyHostName().equals(MAIN_HOST)) {
                db.xml().add("/db", sCLASS, "key", "theone", "laps", singleton.laps, "hops", singleton.hops,
                        "myHost", singleton.myHost);
                db.save(false, true);
            }
        } else {
            if (singleton == null)
                singleton = new Jumper(new AssetKey(sCLASS, "theone", AssetLocation.mutantEnv(Agents
                        .getMyHostName(), "")));
            singleton.laps = Long.parseLong(jumper.getAttribute("laps"));
            singleton.hops = Long.parseLong(jumper.getAttribute("hops"));
            singleton.myHost = jumper.getAttribute("myHost");
        }
    };

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

        return new DrawToString("It is " + TimeOfDay.calcvalue() + " time...\n "
                + messages[(int) (messages.length * Math.random())]);
    }

    public ActionItem whoAreYou() {
        return ME;
    }

    public ActionItem              DETAILS  = new ActionItem("details", RazIcons.UNKNOWN);
    public ActionItem              status   = new ActionItem("JUMPING JACK", RazIcons.UNKNOWN);
    public static final ActionItem ME       = new ActionItem("Jumper", RazIcons.UNKNOWN);

    public String[]                messages = { "I'm just fine...", "You're kind'a nasty",
            "What a freaking nut...it's huge!", "Where's my nut?", "Did you see my nuts?", "Jumper is HAPPY!" };
    protected final static Log logger = Log.Factory.create(Jumper.class.getSimpleName());
}
