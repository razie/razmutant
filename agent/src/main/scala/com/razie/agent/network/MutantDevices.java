/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.agent.network;

import org.w3c.dom.Element;

import razie.base.data.XmlDoc;
import razie.base.data.XmlDoc.Reg;

import com.razie.agent.AgentConfig;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;

/**
 * bad name - this is the main configuration for the agent network
 * 
 * @author razvanc
 * @version $Id$
 */
public class MutantDevices extends Devices {

   /** read home cloud from agent.xml file */
   public static Devices init(Devices toUse, String myName, String iptouse) {
      Devices singleton = (Devices) NoStatics.get(Devices.class);
      if (singleton == null) {
         AgentCloud homeCloud = new AgentCloud();

         // make sure it's initialized
         AgentConfig.instance();

         NoStatics.put(Agents.class, new Agents(homeCloud, null));

         synchronized (deviceByName) {
            singleton = toUse;
            NoStatics.put(Devices.class, singleton);

            Reg.doc("AgentConfig");
            for (Element host : XmlDoc.listEntities(Reg.doc("AgentConfig").xpe(
                  "/config/clouds/cloud[@name='home']"))) {
               // TODO 1-2 FUNC actually identify the home group...not just add everything to it...
               readDevice(homeCloud, host);
            }

            singleton.finishinit(myName, iptouse);

            Agents.homeNetPrefix = Reg.doc("AgentConfig").xpa("/config/network[@home='true']/@ipPrefix");
         }
      }

      return singleton;
   }

   /**
    * @param homeGroup
    * @param host
    * @throws IllegalStateException
    */
   private static void readDevice(AgentCloud homeCloud, Element host) throws IllegalStateException {
      String n = host.getNodeName();
      String type = n;
      String name = host.getAttribute("name");
      Computer.Type ty;

      try {
         ty = Computer.Type.valueOf(type.toUpperCase());
      } catch (Throwable t) {
         String msg = "TYPE " + "/config/clouds/cloud/{DEVICE}" + " FOR device=" + name
               + " WRONG - please check again. Must be one of " + Computer.Type.values();
         Log.logThis(msg);
         throw new IllegalStateException(msg, t);
      }

      String hostname = host.hasAttribute("hostname") ? host.getAttribute("hostname") : name;
      String ip = host.hasAttribute("ip") ? host.getAttribute("ip") : "";
      if (ip.length() == 0) ip = "";
      String port = host.hasAttribute("port") ? host.getAttribute("port") : "4444";
      if (port.length() == 0) port = "4444";
      String os = host.hasAttribute("os") ? host.getAttribute("os") : "";
      String localdir = host.hasAttribute("localdir") ? host.getAttribute("localdir") : "";
      localdir = localdir == null ? "" : localdir.replaceAll("\\\\", "/");

      if (port.length() > 0)
         try {
            @SuppressWarnings("unused")
            int p = Integer.parseInt(port);
         } catch (Throwable t) {
            String msg = "PORT " + "/config/clouds/cloud/{DEVICE}/@port" + " FOR device=" + name
                  + " WRONG - please check again. Must be a 4 digit number!";
            Log.logThis(msg);
            throw new IllegalStateException(msg, t);
         }

      // if ("laptop".equals(type) || "desktop".equals(type) ||
      // "server".equals(type) || "proxy".equals(type)) {
      Computer c = getInstance().makeComputer(name, ty.name());
      c.setHandle(new AgentHandle(name, hostname, ip, port, "http://" + ip + ":" + port, os, localdir));

      if (host.hasAttribute("icon"))
         c.getBrief().setIcon(host.getAttribute("icon"));
      
      if ("laptop".equals(type) || "desktop".equals(type) || "server".equals(type) || "proxy".equals(type))
         homeCloud.put(c.getHandle());
      deviceByName.put(name, c);
      // }
   }
}
