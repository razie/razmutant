package com.razie.dist.play.phantom;

import com.razie.assets.MutantAssetMgr;
import com.razie.assets.ProxyInventory;
import com.razie.assets.cool.CoolAsset;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.AssetMgr.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
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
@SoaAsset(meta = "CrazyCar.razie", base = "Funky.razie", descr = "simple xml distributed agent database")
public class CrazyCar extends CoolAsset implements Breather {
   public static final String sCLASS = "CrazyCar.razie";
   public static final ActionItem META = new ActionItem(sCLASS, "/mutant/pics/FERRARI360MODENA.png");

   static long laps = 0;

   static CrazyCar singleton;

   public static CrazyCar instance() {
      if (singleton == null)
         singleton = new CrazyCar(new AssetKey(sCLASS, "theone", AssetLocation.mutantEnv(Agents
               .getMyHostName(), "")));
      return singleton;
   }

   private CrazyCar(AssetKey ref) {
      super(ref);
      this.key = ref;

      // if not reg yet, reg the class as soa
      AgentHttpService.registerSoaAsset(CrazyCar.class);

      MutantAssetMgr.registerAsset(this, new Meta(CrazyCar.META, "", CrazyCar.class.getName(),
            ProxyInventory.class.getName()));
   }

   @SoaMethod(descr = "say boo")
   public String sayboo() {
      return "Hi, I'm the CrazyCar, living on " + Agents.getMyHostName();
   }

   public void breathe() {
      Log.logThis("CrazyCar is driving... " + ++laps + " laps");
   }

   public ActionItem whatAreYouDoing() {
      return status;
   }

   public ActionItem whoAreYou() {
      return ME;
   }

   public ActionItem status = new ActionItem("Driving...", RazIcons.UNKNOWN);
   public static final ActionItem ME = new ActionItem("CrazyCar", RazIcons.UNKNOWN);
}
