package razie.upnp

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.w3c.dom.Element;

object UpnpControlPoint {
   def init (cp:ControlPoint) = razie.NoStaticS.put[UpnpControlPoint](new UpnpControlPoint (cp))
   def instance : UpnpControlPoint = razie.NoStaticS.get[UpnpControlPoint].orNull
}

/** represents an UPNP control point. Control points keep track of devices on the network */
class UpnpControlPoint (var cp : ControlPoint = new ControlPoint()) {

   def start = cp.start()
   def stop = cp.stop()

   def search = cp.search

   def devices () : List[UpnpDevice] = {
      val l = cp.getDeviceList
      (for (i <- razie.Range.excl(0, l.size)) yield UpnpDevice.fromCyber (l.getDevice(i))).toList
   }

   // TODO implement
   def devicesOfType (devicetype:String) : List[UpnpDevice] = devices()

   // TODO implement
   def devicesWithService (service:String) : List[UpnpDevice] = devices()
}

// TODO add notification stuff
class UpnpCPBridge (val cp : UpnpControlPoint) extends ControlPoint {
   cp.cp = this
}

