package com.razie.agent.webservice;

import java.util.Map;

import com.razie.agent.network.Device;
import com.razie.agent.network.Devices;
import com.razie.agent.upnp.TempAgentUpnpService;
import com.razie.pub.agent.Agent;
import com.razie.pub.agent.AgentService;
import razie.base.ActionItem;
import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import com.razie.pubstage.life.Worker;

/**
 * generic updater thread, will keep pinging devices on a timer and updating their info if different
 * 
 * <p>
 * will keep track of peers and keep their IPs up to date
 * 
 * <p>
 * will look for updates and update local or remote agents
 * 
 * TODO mechanism to re-ping  devices later on, manually - 
 * 
 * TODO trigger manual network scans
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class UpdaterWrkRq extends Worker {

   private String          firstping = "firstping";

   // TODO find an icon for this
   static final ActionItem ME        = new ActionItem("AgentUpdater");

   public UpdaterWrkRq(ExecutionContext threadCtx) {
      super(ME, threadCtx);
   }

   @Override
   public void process() {
      // TempAgentUpnpService aus = (TempAgentUpnpService)
      // AgentService.locate(TempAgentUpnpService.class);
      TempAgentUpnpService aus = (TempAgentUpnpService) Agent.instance().locateService("AgentUpnpService");

      while (!this.dying) {
         updateAllMutants();

         if (this.dying)
            break;

         if (aus != null)
            aus.search();
         else
            Log.logThis("UdaterWrkRq: AgentUpnpService not available, cannot search upnp devices...");

         firstping = "ping";

         if (this.dying)
            break;

         try {
            Thread.sleep(5 * 60 * 1000);
         } catch (InterruptedException e) {
         }
      }
   }

   public void updateAllMutants() {
      Map<String, Device> devices = Devices.getInstance().copyOfDevices();
      int loops = 0;

      // retry the excluded devices
      for (Device device : devices.values())
         if (device.getStatus().equals(AgentHandle.DeviceStatus.EXCLUDED)) {
            Log.logThis("NETWORK_RECONSIDER excluded device : " + device.getHandle());
            device.setStatus(AgentHandle.DeviceStatus.UNKOWN);
         }

      // my aim is that after a scan, all hosts are either known to be down or
      // up
      boolean stillHaveUnknowns = false;
      do {
         loops++;

         stillHaveUnknowns = false;
         for (Device device : devices.values()) {
            if (device.getStatus().equals(AgentHandle.DeviceStatus.EXCLUDED))
               continue;

            if (this.dying)
               break;

            // FUCK DNS LOOKUPS
            // updateDeviceIP(device);
            if (this.dying)
               break;

            Devices.getInstance().updateDeviceStatus(device, firstping);

            // ignore non-agents
            if (device.getStatus().equals(AgentHandle.DeviceStatus.UNKOWN) && device.getPort().length() > 0) {
               stillHaveUnknowns = true;
               Log.logThis("WARN_NETWORK: device still unknown: " + device.getHandle());
            }
         }

         if (stillHaveUnknowns && loops % 3 == 0) {
            Log.logThis("ERR_NETWORK: devices still unknown after more than 3 scans: ");

            // mark the unkown devices as excluded - otherwise this loops like crazy when two
            // devices are configured with the same IP and one is down - can't swap their IPs :)
            for (Device device : devices.values())
               if (device.getStatus().equals(AgentHandle.DeviceStatus.UNKOWN)
                     && device.getPort().length() > 0) {
                  Log.logThis("ERR_NETWORK: MARK_EXCLUDED device still unknown: " + device.getHandle());
                  device.setStatus(AgentHandle.DeviceStatus.EXCLUDED);
               }
         }

         // after the first time there should be no more unknowns
      } while (stillHaveUnknowns && "firstping".equals(firstping));
   }
}
