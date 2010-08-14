package razie.upnp

import org.cybergarage.upnp._
import com.razie.pub.base.data._

/** bridging the scala stuff with the cybergarage library 
 * 
 * The scala device will dynamically create the XMl which is parsed back into the associated cybergarage device.
 * 
 * 
 * 
 */
class DeviceBridge (val sdevice:UpnpDevice) extends DeviceBridgeJ {
   loadDescription (sdevice.toFullUpnpXml.toString) 

   override def razscpdStr (id:String):String = { 
	   sdevice.service(id).toFullUpnpXml.toString
   }
   
   override def getService(name:String) = super.getService (name)

//   override def getServiceList():ServiceList = {
//        val s = svcs
        // TODO optimize after scala 2.8 compiler fixed
//        val services = RazElement.tolist(oldservices)
       
        // TODO use the RazElement.tolist
//        for (i <- 0 to services.size-1)
//           services.get(i)
	   
//        svcs
//		   null 
//   }

//   override def getServiceList():ServiceList =  {
//        val serviceList = super.getServiceList()
        
//        RazElement.tolist(serviceList).foreach ()
        
//        val serviceListNode = getDeviceNode().getNode(ServiceList.ELEM_NAME);
//        if (serviceListNode == null)
//            return serviceList;
//        val nNode = serviceListNode.getNNodes();
//        for (val n <- 0 to nNode-1) {
//            val node = serviceListNode.getNode(n);
//            if (Service.isServiceNode(node) != false) {
//            val u = if (this.descriptionURL == null) null else this.descriptionURL.toExternalForm().replaceFirst("/[^/]+$", "")
//            val service = new Service(node, u);
//            serviceList.add(service);
//            }
//        } 

//        return serviceList;
//    }
}
