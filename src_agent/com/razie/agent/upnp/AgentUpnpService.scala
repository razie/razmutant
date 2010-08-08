package com.razie.agent.upnp;

import org.cybergarage.util.Debug

import com.razie.pub.agent.AgentDeviceStatusListener
import com.razie.pub.agent.AgentService
import com.razie.pub.base.ExecutionContext
import com.razie.pub.base.log.Log
import com.razie.pub.upnp.UpnpSoaBinding
import com.razie.pub.agent._

import razie.upnp._

/**
 * the statics...
 */
object AgentUpnpService {
   def registerSoa(upnpSoaBinding:UpnpSoaBinding ) = {
       AgentService.locate(classOf[AgentUpnpService]).asInstanceOf[AgentUpnpService]
         .device.bindings.add(upnpSoaBinding)
   }
}
/**
 * use UPNP to discover agents - this contains a device and a control point
 * 
 * @param deviceMgr - lazy required onStartup
 * @author razvanc
 */
class AgentUpnpService (deviceMgr : => AgentDeviceStatusListener) extends TempAgentUpnpService {
   var cpoint : AgentControlPoint = null // lazy
   val device = new AgentUpnpDevice();
   
   Log.logThis("CREATE_SERVICE " + "AgentUpnpService");

   override def onStartup() = {
      cpoint = new AgentControlPoint(ExecutionContext.instance(), deviceMgr);
      UpnpControlPoint init cpoint
		cpoint.start();
		device.start();

	
		// maybe registering these assets should be in a generic UpnpPlugin
//		razie.Metas.add (Upnp.deviceMeta)
//		razie.Metas.add (Upnp.serviceMeta)

		razie.Assets.manageClass[AUpnpDevice]
		razie.Assets.manageClass[AUpnpService]
		
		Debug.off();

		search();
	}

   override def onShutdown() = {
		// TODO how do i shutdown the upnp stuff?
		cpoint.stop();
		device.stop();
	}

   def search() = cpoint.search()
}
