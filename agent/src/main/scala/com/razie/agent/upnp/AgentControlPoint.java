/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.agent.upnp;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.w3c.dom.Element;

import razie.base.data.XmlDoc;

import com.razie.pub.agent.AgentDeviceStatusListener;
import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.Comms;

/**
 * a simple control point to discover other agents
 * 
 * @author razvanc
 * 
 */
public class AgentControlPoint extends ControlPoint implements NotifyListener, DeviceChangeListener,
      SearchResponseListener {
   private AgentDeviceStatusListener deviceMgr;
   private ExecutionContext threadContext;

   public AgentControlPoint(ExecutionContext tc, AgentDeviceStatusListener deviceMgr) {
      this.threadContext = tc;
      addNotifyListener(this);
      addDeviceChangeListener(this);
      this.deviceMgr = deviceMgr;
   }

   public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
      setupCtx();

      String uuid = ssdpPacket.getUSN();
      String nt = ssdpPacket.getNT();
      String nts = ssdpPacket.getNTS();
      String location = ssdpPacket.getLocation();

      // somebody is alive...who?
      if (nts.equals("ssdp:alive")) {
         // Log.logThis("UPNP_DEVICENOTIFY: \n " + uuid + "\n " + nt + "\n "
         // + nts + "\n " +
         // location
         // + "\n " + ssdpPacket.getServer() + "\n" + ssdpPacket.toString());

         // hey, it's a buddy mutant
         if (nt.equals("urn:schemas-upnp-org:device:mutant:1")) {
            if (location.contains("http://[")) {
               // TODO ipv6 support
               Log.alarmOnce("ERROR_UPNP_IPV6NOTSUPPORTED",
                     "ERROR_UPNP_IPV6NOTSUPPORTED You have IPV6 UPNP notifications - DON't SUPPRT IPV6 yet... "
                           + location);
               Log.traceThis("ERROR_UPNP_IPV6NOTSUPPORTED can't reach mutant's descrition at " + location);
            } else
               try {
                  // get the description and get the presentationURL -
                  // that's the
                  // actual url for the mutant...
                  String description = Comms.readUrl(location);
                  XmlDoc doc = XmlDoc.createFromString("temp", description);
                  doc.addPrefix("bibi", "urn:schemas-upnp-org:device-1-0");
                  String newurl = doc.getOptNodeVal("/bibi:root/bibi:device/bibi:presentationURL");

                  String l = newurl.replaceFirst("http://", "");
                  String ipport = l.split("/", 2)[0];
                  // TODO is there always a port? I guess nobody would run at the default 8080...
                  String port = ipport.substring(ipport.lastIndexOf(":") + 1);
                  String srcIp = ipport.substring(0, ipport.lastIndexOf(":"));

                  deviceMgr.notifiedUp(null, null, srcIp, port);
               } catch (Exception e) {
                  Log.logThis("ERROR_UPNP_MUTANT can't reach mutant's descrition at " + location);
                  Log.traceThis("ERROR_UPNP_MUTANT exception: ", e);
               }
         }
      } else {
         // Log.logThis("UPNP_DEVICENOTIFY: " + nts);
      }
   }

   public void deviceAdded(Device dev) {
      setupCtx();

      Log.logThis("UPNP_DEVICEADD: " + dev.getFriendlyName());

      if (dev.getDeviceType().equals("urn:schemas-upnp-org:device:mutant:1")) {
         // a new mutant was discovered...
         String srcHost = dev.getFriendlyName();
         if (!srcHost.startsWith("MutantAgent:")) {
            throw new IllegalArgumentException("ERR_UPNP_DEVICENOTAGENT");
         }
         srcHost = srcHost.replaceFirst("MutantAgent:", "");

         // it's so convoluted because IPv6 has colons...
         String l = dev.getPresentationURL().replaceFirst("http://", "");
         String ipport = l.split("/", 2)[0];
         // TODO is there always a port? I guess nobody would run at the
         // default 8080...
         String port = ipport.substring(ipport.lastIndexOf(":") + 1);
         String srcIp = ipport.substring(0, ipport.lastIndexOf(":"));

         Log.logThis("UPNP_MUTANTADD: " + srcHost + " @ " + srcIp + ":" + port);

         deviceMgr.notifiedUp(null, null, srcIp, port);

         // update my network...
         // TODO copy/paste

      }
   }

   public void deviceRemoved(Device dev) {
      setupCtx();

      Log.logThis("UPNP_DEVICEREMOVED: " + dev.getFriendlyName());

      if (dev.getDeviceType().equals("XXXXXXXXXXXXXurn:schemas-upnp-org:device:mutant:1")) {
         // a new mutant was discovered...
         String srcHost = dev.getFriendlyName();
         if (!srcHost.startsWith("MutantAgent:")) {
            throw new IllegalArgumentException("ERR_UPNP_DEVICENOTAGENT");
         }
         srcHost = srcHost.replaceFirst("MutantAgent:", "");

         // update my network...

         AgentHandle d = Agents.agent(srcHost);

         if (d != null) {
            deviceMgr.notifiedDown(srcHost, srcHost, null, null);
            Log.logThis("UPNP_MUTANTREMOVED: " + srcHost);
         }
      }

   }

   public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
      setupCtx();

      String uuid = ssdpPacket.getUSN();
      String target = ssdpPacket.getST();
      String subType = ssdpPacket.getNTS();
      String location = ssdpPacket.getLocation();
      Log.logThis("UPNP_DEVICESEARCHRESPONSE: \n   " + uuid + "\n   " + target + "\n   " + subType + "\n   "
            + location);
   }

   public void setupCtx() {
      this.threadContext.enter();
   }
}
