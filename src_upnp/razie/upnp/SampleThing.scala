package razie.upnp

import com.razie.pub.lightsoa._
import com.razie.pub.upnp._
import com.razie.pub.base.data._
import java.net._

/** a sample device with embedded devices and services */
class RazSampleDevice extends DefaultUpnpDevice {
   val deviceType="urn:schemas-upnp-org:device:razsampledevice:1"
   val friendlyName="RazSampleDevice"
   val manufacturer="Razvan C"
   val manufacturerURL="http://www.razie.com"
   val modelDescription="Test UPNP Device"
   val modelName="Test"
   val modelNumber="1.0"
   val modelURL="http://www.razie.com"
   val serialNumber="1234567890"
   val UPC="123456789012"
  
   // special NOTE about UDN. I believe it's a good idea to generate UDNs that don't change, per instance, for example:
   override val UDN = "uuid:"+"RazSampleDevice-"+InetAddress.getLocalHost().getHostName();
   
   val devices = List(new RazSampleDevice2)
   
//   val services = List(
//		   UpnpServiceFactory.fromAnnotation(new SampleAnnotatedService, new UpnpServiceAttrs {
//			   val xmlns="urn:schemas-upnp-org:service-1-0"
//				   val serviceType = "urn:schemas-upnp-org:service:atest:1"
//				   val serviceId = "urn:schemas-upnp-org:serviceId:atest:1"
//				   val SCPDURL = "/service/agent-description.xml"
//				   val controlURL = "/service/timer/control"
//				   val eventSubURL = "/service/timer/eventSub"
//		   }), 
//		   new DerivedAnnotatedService())
//)
val services=List[UpnpService]()
}

class RazSampleDevice2 extends DefaultUpnpDevice {
	   val deviceType="urn:schemas-upnp-org:device:razsampledevice2:1"
	   val friendlyName="RazSampleDevice2"
	   val manufacturer="Razvan C"
	   val manufacturerURL="http://www.razie.com"
	   val modelDescription="Test UPNP Device"
	   val modelName="Test"
	   val modelNumber="1.0"
	   val modelURL="http://www.razie.com"
	   val serialNumber="1234567890"
	   val UPC="123456789012"
	   
	   // special NOTE about UDN. I believe it's a good idea to generate UDNs that don't change, per instance, for example:
	   override val UDN = "uuid:"+"RazSampleDevice2-"+InetAddress.getLocalHost().getHostName();
	   
	   // no embedded devices
	   val devices = List()
   val services = List(new DerivedAnnotatedService())
	}

/** a sample scala upnp service, which doesn't know how it's bound to upnp... does that make it reusable? */
@SoaService(name = "scala", bindings=Array("upnp"), descr = "sample scala service" )
class SampleAnnotatedService {

    @SoaMethod(descr = "no parms")
    def Method1() = "what up?"

    @SoaMethod(descr = "1 parm", args = Array( "msg" ))
    def Method2 (msg:String) =  "SCALA Method2 msg="+msg
}

/** just a sample agent service written in SCALA */
@SoaService(name = "scala", bindings=Array("upnp"), descr = "sample scala service" )
class DerivedAnnotatedService extends UpnpService {
   val xmlns="urn:schemas-upnp-org:service-1-0"
   val serviceType = "urn:schemas-upnp-org:service:anothertest:1"
   val serviceId = "urn:schemas-upnp-org:serviceId:anothertest:1"
//   val SCPDURL = "/service/agent-description.xml"
	   // device is the http server for embedded devices and services. 
	   // Service must recognize its own scpdurl...
   val SCPDURL = "/service/" + serviceId + "/description.xml"
   val controlURL = "/service/timer/control"
   val eventSubURL = "/service/timer/eventSub"
				
   override val binding = new UpnpSoaBinding (this, "?")
	
 @SoaMethod(descr = "no parms")
   def Method1() = "what up?"

   @SoaMethod(descr = "1 parm", args = Array( "msg" ))
   def Method2 (msg:String) =  "SCALA Method2 msg="+msg
}

/** sample upnp devices and services - copy paste and modify... */
object SampleThing {
  def main(args : Array[String]) : Unit = {
	val d=new RazSampleDevice
	println (d.toFullUpnpXml.toString)
	
	d.services.foreach (x => println("--------------------------\n"+x.toFullUpnpXml))
	
	d.start
	
	Thread.sleep(10000)
  }
}

