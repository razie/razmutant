package com.razie.agent.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import razie.assets.AssetKey;

import com.razie.agent.webservice.TempAgentNetworkService;
import com.razie.pub.UnknownRtException;
import com.razie.pub.agent.Agent;
import com.razie.pub.agent.AgentDeviceStatusListener;
import com.razie.pub.base.NoStaticSafe;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.Comms;
import com.razie.pub.comms.AgentHandle.DeviceStatus;

/**
 * it contains the logic to keep track of agents status. This adds the logic of
 * maintaining groups of agents, with trust etc.
 * 
 * this is an extension of Agents
 * 
 * @author razvanc
 * @version $Id$
 */
@NoStaticSafe
public class Devices implements AgentDeviceStatusListener {
   protected static Map<String, Device> deviceByName = new HashMap<String, Device>();
   protected Computer me = null;

   public AgentHandle myHandle;

   public Agents agents;

   public static Devices getInstance() {
      Devices singleton = (Devices) NoStatics.get(Devices.class);
      if (singleton == null) {
         throw new IllegalStateException("not initialized yet...");
      }
      return singleton;
   }

   public static Devices init(Devices toUse, AgentHandle whoami, AgentCloud homeGroup) {
      Devices singleton = (Devices) NoStatics.get(Devices.class);
      if (singleton == null) {
         synchronized (deviceByName) {
            singleton = toUse;
            singleton.agents = Agents.instance();

            NoStatics.put(Devices.class, singleton);

            for (AgentHandle h : homeGroup.agents().values()) {
               String type = "laptop";
               String hostname = h.hostname;
               String name = h.name;
               String port = h.port;

               if ("laptop".equals(type) || "desktop".equals(type)) {
                  if (port != null && port.length() > 0)
                     try {
                        Integer.parseInt(port);
                     } catch (Throwable t) {
                        String msg = "PORT " + "/config/mutant/@port" + " FOR device=" + name
                              + " WRONG - please check again. Must be a 4 digit number!";
                        Log.logThis(msg);
                        throw new IllegalStateException(msg, t);
                     }

                  Computer c = getInstance().makeComputer(name, type);
                  c.setHandle(h);
                  deviceByName.put(name, c);
               }
            }

            try {
               singleton.finishinit(whoami.name, InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
               throw new UnknownRtException(e);
            }
         }
      }

      return singleton;
   }

   public Computer makeComputer(String name, String type) {
                     Computer c = new ComputerImpl(new AssetKey(Device.sCLASSNM_DEVICE, name), Computer.Type
                           .valueOf(type.toUpperCase()));
      return c;
   }

   public void finishinit(String name, String iptouse) {
      Devices singleton = (Devices) NoStatics.get(Devices.class);
      try {
         // now find me
         for (Device d : deviceByName.values()) {
            // TODO why is this here?
            if (d.getName().equalsIgnoreCase(name)) {
               singleton.me = (Computer) d;

               singleton.myHandle = singleton.me.getHandle();
               Agents.setMe(singleton.myHandle);

               String newip = d.getIp();
               // I gotta be up, eh?
               newip = iptouse;
               // WHY DID I COMMENT THIS OUT? singleton.updateDevice(d.getName(), newip, DeviceStatus.UP);
               singleton.updateDevice(d.getName(), d.getHandle().hostname, newip, d.getPort(), DeviceStatus.UP);
               break;
            }

         }
         if (singleton.me == null)
            throw new IllegalStateException("Cannot figure out who I am...My IP is: " + InetAddress.getLocalHost());
      } catch (UnknownHostException e) {
         throw new UnknownRtException(e);
      }
   }

   public static String getMyIp() throws RuntimeException {
      return device(Agents.getMyHostName()).getIp();
   }

   /**
    * update a device status in the DB...if the status changes from down to up,
    * notify agent
    * 
    * @return true if something was actually changed
    */
   protected boolean updateDevice(String name, String host, String ip, String port,
         AgentHandle.DeviceStatus status) {
      boolean updated = false;
      synchronized (deviceByName) {
         Device d = deviceByName.get(name);

         if (d == null) {
            throw new IllegalArgumentException("Device <" + name + "> is not known...");
         }

         String oldIp = d.getIp();
         String oldPort = d.getPort();
         if (port != null && !oldPort.equals(port)) {
            updated = true;
            logger.log("DEVICES_UPDATE name=" + name + " host=" + host + " oldport=" + oldPort + " newport="
                  + port);
            d.setPort(port);
            // Agents.agent(name).ip = ip;
            // Agents.agent(name).url = "http://" + ip + ":" +
            // Agents.agent(name).port;
         }
         if (ip != null && !oldIp.equals(ip)) {
            updated = true;
            logger.log("DEVICES_UPDATE name=" + name + " oldip=" + oldIp + " newip=" + ip);
            d.setIp(ip);
            // Agents.agent(name).ip = ip;
            // Agents.agent(name).url = "http://" + ip + ":" +
            // Agents.agent(name).port;
         }
         if (!d.getStatus().equals(status)) {
            updated = true;
            logger.log("DEVICES_UPDATE name=" + name + " oldstatus=" + d.getStatus() + " newstatus=" + status);
            d.setStatus(status);
         }

         // if i changed an IP - reset the other one that might have used it...
         if (ip != null && !oldIp.equals(ip)) {
            // if (ip != null) {
            for (Device otherWithSameIp : deviceByName.values()) {
               // TODO dont compare name with test, but compare port number and name or something...
               if (ip.equals(otherWithSameIp.getIp()) && !otherWithSameIp.getName().equals(name)
                     && !otherWithSameIp.getStatus().equals(AgentHandle.DeviceStatus.UP)) {
                  // &&
                  // !otherWithSameIp.getName().equals(UserConfig.TESTHOST
                  // )) {
                  otherWithSameIp.setStatus(AgentHandle.DeviceStatus.UNKOWN);
                  otherWithSameIp.setIp(oldIp);
                  logger.log("DEVICES_RESETOLDIP ip=" + ip + " for name="+otherWithSameIp.getName()
                		  +" to unused ip=" + oldIp);
                  AgentHandle other = Agents.agent(otherWithSameIp.getName());
                  if (other != null) {
                	  // it can be different for tablets
                	  // TODO why do i do this? it's inherited from the setIp above, right?
                  other.ip = oldIp;
                  other.url = "http://" + oldIp + ":" + other.port;
                  }
               }
            }
         }
      }

      return updated;
   }

   public Device deviceInfo(String name) throws RuntimeException {
      synchronized (deviceByName) {
         return deviceByName.get(name);
      }
   }

   public static Device device(String name) throws RuntimeException {
      return Devices.getInstance().deviceInfo(name);
   }

   public static Device deviceByIp(String ip, String port) throws RuntimeException {
      synchronized (deviceByName) {
         for (Device otherWithSameIp : deviceByName.values()) {
            if (ip.equals(otherWithSameIp.getIp())
                  && (port == null || port.equals(otherWithSameIp.getPort()))) {
               return otherWithSameIp;
            }
         }
      }

      return null;
   }

   public Map<String, Device> copyOfDevices() {
      synchronized (deviceByName) {
         Map<String, Device> copy = new HashMap<String, Device>();
         copy.putAll(deviceByName);
         return copy;
      }
   }

   /** will ping and update NOT SYNCHRONIZED */
   public void updateDeviceStatus(Device device, String pingcmd) {
      // make sure it's up:
      String ip = device.getIp();

      if (device.getPort().length() <= 0 ) {
         // for now ignore those that don't run agents:
         Devices.getInstance().updateDevice(device.getName(), device.getHandle().hostname, device.getIp(),
               device.getHandle().port, AgentHandle.DeviceStatus.UNKOWN);
         return;
      }
      
      AgentHandle response = TempAgentNetworkService.doPing(device.getHandle(), Agents.me(), pingcmd);

      if (response != null) {
         boolean updated = false;

         Device newDevice = device;

         if (device.getName().equals(response.name)) {
            updated = updateDevice(device.getName(), device.getHandle().hostname, ip, response.port,
                  AgentHandle.DeviceStatus.UP);
         } else {
            if (Devices.device(response.name) != null) {
               // swap IPs...
               // TODO notifyUnknown
               // change unknown;s address to the response;s old one
               updateDevice(device.getName(), device.getHandle().hostname, null, null,
                     AgentHandle.DeviceStatus.UNKOWN);
               // Devices.getInstance().updateDevice(device.getName(),
               // Devices.device(response).getIp(),
               // Device.DeviceStatus.UNKOWN);
               newDevice = Devices.device(response.name);
               updated = updateDevice(response.name, response.hostname, ip, response.port,
                     AgentHandle.DeviceStatus.UP);
            } else {
               // else means the response was garbled...what to do?
               logger.log("ERR_GARBLED_PING_RESPONSE: " + response);
            }
         }

         if (updated) {
            notifyNewDevice(newDevice.getHandle());
         }
      } else {
         // no mutant answering this IP...
         Devices.getInstance().updateDevice(device.getName(), device.getHandle().hostname, device.getIp(),
               device.getHandle().port, AgentHandle.DeviceStatus.DOWN);
      }
   }

   public void notifyNewDevice(AgentHandle device) {
      try {
         // TODO test that overriding this in the MyutantDevices actually
         // works
         Agent.instance().onConnectToOtherAgent(device);
      } catch (Exception e) {
         logger.log("some exception during update ops...", e);
      }
   }

   /**
    * call this whenever you get a notification of some kind that a device is
    * up. Host can be null - in which case i will ping it myself to make sure
    * it's an agent
    */
   public void notifiedUp(String name, String host, String ip, String port) {
      if (host == null) {
         // don't know the host - must ping it myself
         // if (!ip.equals(Devices.getMyIp())) {
         Device d = deviceByIp(ip, port);
         if (d != null) {
            if (d.getStatus().equals(AgentHandle.DeviceStatus.UP)) {
               // If this ip is known to be up, assume it didn't
               // change...it's still the
               // same
            } else {
               // must ping...
               logger.log("UPNP_DEVICENOTIFY: updating mutant!");
               // this may actually change devices and IPs...
               updateDeviceStatus(d, "ping");
            }
            // }
         } else {
            // opa - it's an agent i don't know about...or moved ip or
            // something...find and
            // update
            AgentHandle ah = TempAgentNetworkService.doPing(new AgentHandle(name, host, ip,
                  (port == null ? "4444" : port)), Agents.me(), "ping");
            host = ah == null ? null : ah.hostname;
            name = ah == null ? null : ah.name;
         }
      }

      if (name != null) {
         // host either was known or i pinged the ip and got it...
         boolean updated = false;
         Device d = Devices.device(name);

         if (d != null && !d.getIp().equals(ip)) {
            updated = updateDevice(name, host, ip, port, AgentHandle.DeviceStatus.UP);
         } else if (d != null && !d.getStatus().equals(AgentHandle.DeviceStatus.UP)) {
            updated = updateDevice(name, host, ip, port, AgentHandle.DeviceStatus.UP);
         }

         // new guy
         if (d == null) {
            notifyNewDevice(new AgentHandle(name, host, ip, (port == null ? "4444" : port)));
         } else if (updated && !host.equals(Agents.getMyHostName())) {
            // if something did change, means the device came up...
            notifyNewDevice(d.getHandle());
         }
      }
   }

   /**
    * call this whenever you get a notification of some kind that a device is
    * shutting down. Host cannot
    */
   public void notifiedDown(String name, String host, String ip, String port) {
      // Devices.getInstance().updateDevice(srcHost, null, DeviceStatus.DOWN);
      // logger.log("UPNP_MUTANTREMOVED: " + srcHost);
   }

   /**
    * call this whenever you have reason to believe a host may have a different
    * ip...i.e. if you lookup DNS or something. The other device's status is
    * unknown
    */
   public void notifiedNewIp(String host, String ip) {
      Device d = device(host);
      if (!ip.equals(d.getIp()) && !Comms.isLocalhost(ip)) {
         logger.log("DEVICES_NEWIP for host: " + d.getName() + " IP is: " + ip);
         // TODO update status as well...
         updateDevice(d.getName(), host, ip, d.getPort(), d.getStatus());
      }
   }

   protected static final Log logger = Log.factory.create("Agent", "Devices");
}
