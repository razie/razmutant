package com.razie.agent;

import java.io.ByteArrayOutputStream;
import java.io.File;

import razie.assets.AssetLocation;

import com.razie.agent.webservice.CmdFilesClient;
import com.razie.pub.agent.Agent;
import com.razie.pub.agent.AgentFileService;
import com.razie.pub.agent.AgentService;
import razie.base.ActionItem;
import razie.base.AttrAccess;
import razie.draw.DrawStream;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.SimpleActionToInvoke;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.PermType;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.events.PostOffice;
import com.razie.pub.lightsoa.SoaAllParms;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pubstage.UserPrefs;

/**
 * control commands
 * 
 * @author razvanc
 */
@SoaService(name = "control", bindings = "http", descr = "generic remote control")
public class AgentControlService extends AgentService {
   public static boolean UpgradeServiceupgraded = false;
   
   // TODO 2-1 fix this - copy from upgradeTo
   @SoaMethod(descr = "upgrade mutant", args = { "ip" }, perm=PermType.ADMIN)
   public String XXupgradeFrom(String ip) {
      // me upgrade from remote host
//      AssetLocation target = AssetLocation.mutantEnv(ip, "");

//      String targetdir = Agents.agentByIp(ip).localdir;
//      String mutantdir = AgentConfig.getMutantDir();

      // upgrade procedure:
      // 1. copy new files to
      //TODO

      // TODO start copy
      if ((new File("./upgrade/razmutant.jar")).exists()) {
         UpgradeServiceupgraded = false;
         Agent.instance().onShutdown();
         return "Upgraded and restarting the server...";
      } else {
         return "Upgrade not present - cannot restart...";
      }
   }

   @SoaMethod(descr = "upgrade mutant", args = { "ip", "forced" }, perm=PermType.ADMIN)
   public String upgradeTo(String ip, String forced) {
      // remote upgrade from me
      boolean forceIt = Boolean.parseBoolean(forced);
      AssetLocation target = AssetLocation.mutantEnv(ip, "");

      String mutantdir = AgentConfig.getMutantDir();
      String targetdir = Agents.agentByIp(ip).localdir;

      // 1. issue "backup" to restart new server
      String reply;
      try {
         reply = (String) new ServiceActionToInvoke(target.toHttp(), "control", new ActionItem("Backup")).act(null);
      } catch (Throwable e) {
         logger.alarm("Can't backup remote agent "+Agents.agentByIp(ip), e);
         if (! forceIt)
            throw new RuntimeException ("Cannot backup remote agent...to force the upgrade, change forced flag to \"true\" in this URL and submit", e);
      }

      // 2. copy what's in my upgrade...
      CmdFilesClient.remoteCopyDir(mutantdir + "/upgrade", target, targetdir + "/upgrade", true);

      // 3. issue "upgrade" to restart new server
      reply = (String) new ServiceActionToInvoke(target.toHttp(), "control", new ActionItem("upgrade2")).act(null);

      try {
         // fast fucker, should restart in 1 sec
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         // ignore
         e.printStackTrace();
      }

      reply = (String) new ServiceActionToInvoke(target.toHttp(), "control", new ActionItem("Status")).act(null);
      
      return reply;
   }

   @SoaMethod(descr = "upgrade mutant", perm=PermType.ADMIN)
   public String upgrade() {
      // client already copied the new jar file here
      // i just have to start the copy and stop myself

      // TODO start copy
      UpgradeServiceupgraded = true;
      Agent.instance().onShutdown();
      return "Upgraded and restarting the server...";
   }

   @SoaMethod(descr = "upgrade mutant", perm=PermType.ADMIN)
   public String upgrade2() {
      // client already copied the new jar file here
      // i just have to start the copy and stop myself

      // TODO start copy
      if ((new File(AgentConfig.getMutantDir() + "/upgrade/razmutant.jar")).exists()) {
         UpgradeServiceupgraded = false;
         Agent.instance().onShutdown();
         return "Upgraded and restarting the server...";
      } else {
         return "Upgrade not present - cannot restart...";
      }
   }

   @SoaMethod(descr = "return the configuration xml", args={"file"}, perm=PermType.ADMIN)
   @SoaStreamable
   public void config(DrawStream out, String file) {
      String s = "";
      ByteArrayOutputStream outs = new ByteArrayOutputStream();
         AgentFileService.copyStream(ClassLoader.getSystemResourceAsStream(file), outs);
         s += outs.toString();
      out.write(s);
   }

   @SoaMethod(descr = "set a user preference", args = { "name", "value" }, perm=PermType.CONTROL)
   public String setpref(String name, String value) {
      UserPrefs.getInstance().setPref(name, value);
      return "Ok..." + name + "==" + value;
   }

   @SoaMethod(descr = "notification service")
   @SoaAllParms
   public String Notify(AttrAccess parms) {
      String name = (String) parms.getAttr("name");
      String srcAgentNm = (String) parms.getAttr("srcAgentNm");
      PostOffice.shoutLocal(srcAgentNm, name, parms);
      return "Ok";
   }

   @SoaMethod(descr = "get some log lines", args = { "howMany" }, perm=PermType.CONTROL)
   public String GetLog(String howMany) {
      int many = 100;
      if (howMany != null && howMany.length() > 0) {
         many = Integer.parseInt(howMany);
      }
      return "Last logs: " + (String) Log.tryToString("   ", Log.getLastLogs(many)) + "\n{ENDS}";
   }

   @SoaMethod(descr = "stop mutant", perm=PermType.CONTROL)
   public String Stop() {
      Agent.instance().onShutdown();
      return "Stopping the server...";
   }

   @SoaMethod(descr = "kill/abort/kaput", perm=PermType.CONTROL)
   public void Die() {
      System.exit(0);
   }

   static final Log logger = Log.factory.create("", AgentControlService.class.getName());
}
