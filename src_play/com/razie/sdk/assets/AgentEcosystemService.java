/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.sdk.assets;

import razie.base.life.Lifegiver;
import razie.base.scripting.RazScript;
import razie.base.scripting.ScriptContext;
import razie.base.scripting.ScriptFactory;
import razie.draw.DrawList;
import razie.draw.Drawable;
import razie.draw.widgets.DrawToString;

import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;

/**
 * asset management
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaService(name = "ecosystem", bindings = { "http" }, descr = "ecosystem for the beings and daemons")
public class AgentEcosystemService extends AgentService {

   public AgentEcosystemService() {
   }

   protected void onStartup() {
//      Lifegiver.init(this.agent.getContext());

      // TODO persistency for the active beings, birth/deaths etc
   }

   protected void onShutdown() {
      Lifegiver.die();
   }

   @SoaMethod(descr = "list the beings and what they're doing")
   public Drawable display() {
      DrawList levels = new DrawList();

      return levels;
   }

   @SoaMethod(descr = "list the beings and what they're doing", args = { "language", "script" })
   public Drawable run(String language, String script) {
      RazScript scr = ScriptFactory.make(language, script);
      return new DrawToString(scr.eval(ScriptFactory.mkContext()));
   }

   static final Log logger = Log.factory.create("", AgentEcosystemService.class.getName());
}
