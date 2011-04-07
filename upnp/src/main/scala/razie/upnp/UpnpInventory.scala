package razie.upnp

import razie.assets._
import razie.base.scripting._
import razie.base._
import com.razie.pub.base._
import com.razie.pub.lightsoa._
import razie.draw._
import scala.collection.JavaConversions._

/** some statics */
object Upnp {
   org.cybergarage.util.Debug.on()
   final val Device = "Upnp.Device"
   final val MutantDevice = "Upnp.MutantDevice"
   final val Service = "Upnp.Service"
   final val DeviceHasServices = "DeviceHasServices"
      
   def uc = UpnpControlPoint.instance
   
   def deviceMeta = new MetaSpec (
     new Meta (razie.AI cmdicon(Upnp.Device, "device"), null, 
         classOf[UpnpInventory].getName))
   def serviceMeta = new MetaSpec (
     new Meta (razie.AI cmdicon(Upnp.Device, "device"), null, 
         classOf[UpnpInventory].getName))
   
   def makeDevice (d:UpnpDevice) = d.deviceType match {
      case "urn:schemas-upnp-org:device:MediaServer:1" => new AUpnpMediaServer (d)
      case "urn:schemas-upnp-org:device:mutant:1" => new AUpnpMutant (d)
      case _ => new AUpnpDevice (d)
   }
}

/** stands in for a upnpdevice */
@SoaAsset(meta=Upnp.Device, descr="a upnp device")
@AssetMeta(name=Upnp.Device, inventory=classOf[UpnpInventory],   descr="a upnp device")
class AUpnpDevice (val actual:UpnpDevice) extends AssetBaseImpl (NoAssetBrief) with DrawAsset {
   this.brief = new AssetBriefImpl (new AssetKey (Upnp.Device, actual.friendlyName), 
         name=actual.friendlyName, icon="device", briefDesc=actual.deviceType)
   
   val services = for (s <- actual.services) yield new AUpnpService (this, s)
   
   override def render(t:Technology , stream:DrawStream ) : AnyRef = razie.Draw.seq(
      super.render(t,stream),
      razie.Draw.text("-------------services--------------"),
      AssetMgr.pres().toDrawable(razie.RSJ.list(services.map(_.brief)), null, NoAffordance),
      razie.Draw.text("-------------my xml--------------"),
      razie.Draw.xml (actual.toUpnpXml.toString),
      razie.Draw.text("-------------original xml--------------"),
      razie.Draw.xml (actual.cyberDevice.getRootNode().toString),
      razie.Draw.text("-------------done--------------")
      )
}

class AUpnpMutant (actual:UpnpDevice) extends AUpnpDevice (actual) {
   override def render(t:Technology , stream:DrawStream ) : AnyRef = razie.Draw.seq(
      super.render(t,stream)
      )
}

class AUpnpMediaServer (actual:UpnpDevice) extends AUpnpDevice (actual) {
   
   override def render(t:Technology , stream:DrawStream ) : AnyRef = razie.Draw.seq(
      super.render(t,stream),
//      razie.Draw.text("-------------services--------------"),
//      AssetMgr.pres().toDrawable(razie.RSJ.list(services.map(_.brief)), null, NoAffordance),
//      razie.Draw.text("-------------browsing--------------"),
//      
//      
//      
//      
//      razie.Draw.text("-------------my xml--------------"),
//      razie.Draw.xml (actual.toUpnpXml.toString),
//      razie.Draw.text("-------------original xml--------------"),
//      razie.Draw.xml (actual.cyberDevice.getRootNode().toString),

      razie.Draw.text("-------------Browsing--------------"),
      
      browse ("0"),
      
      razie.Draw.text("-------------done--------------")
      )

   def browse (id:String) = {
      val seq = razie.Draw.seq()
      seq write razie.Draw.text("-------------meta--------------")
      val rma = Browse (id, "BrowseMetadata", "0", "100")
      seq write rma
      
      seq write razie.Draw.text("-------------data--------------")
      val rda = Browse (id, "BrowseDirectChildren", "0", rma.outArgs.sa("TotalMatches"))
      seq write rda
      seq
   }
   
   def Browse(ObjectId: String , BrowseFlag:String, StartingIndex:String, RequestedCount:String) = {
      val directory = actual.services.filter (_.serviceType == "urn:schemas-upnp-org:service:ContentDirectory:1").head
      val action = directory.actions.filter (_.name == "Browse").head
      action.inArgs.setAttr("ObjectID", ObjectId, "BrowseFlag", BrowseFlag, "StartingIndex", StartingIndex, "RequestedCount", RequestedCount)   
      action.execute
      val aa = action.outArgs 
      action
   }
}

/** stands in for a upnpdevice */
@SoaAsset(meta=Upnp.Service, descr="a upnp service")
@AssetMeta(name=Upnp.Service, inventory=classOf[UpnpInventory], descr="a service of a upnp device")
@AssetAssoc(name=Upnp.DeviceHasServices, a=Upnp.Device, z=Upnp.Service, descr="a upnp service")
class AUpnpService (val device:AUpnpDevice, val actual:UpnpService) extends AssetBaseImpl (NoAssetBrief) with DrawAsset {
   this.brief = new AssetBriefImpl (new AssetKey (Upnp.Service, device.key.id+"///"+actual.serviceId), 
         name=actual.serviceId, icon="service", briefDesc=actual.serviceType)
   
   override def render(t:Technology , stream:DrawStream ) : AnyRef = razie.Draw.seq(
      super.render(t,stream),
      razie.Draw.text("-------------my xml--------------"),
      razie.Draw.xml (actual.toFullUpnpXml.toString),
      razie.Draw.text("-------------original xml--------------"),
      razie.Draw.xml (new String(actual.cyberService.getSCPDData())),
      razie.Draw.text("-------------done--------------")
      )
}

class UpnpInventory extends BaseInventory {

    override def getAsset(key : AssetKey) = key.meta match {
       case Upnp.Device  => 
          Upnp.uc.devices.filter(_.friendlyName == key.id).map(Upnp.makeDevice(_)).head
       case Upnp.Service => {
          val pat = """(.*)///(.*)""".r
          val pat (d,s) = key.id
          val de = Upnp.uc.devices.filter(_.friendlyName == d).map(Upnp.makeDevice (_)).head
          val sl = de.services
          val se = de.services.filter (_.key.id == key.id).head
         se 
       }
       case _ => None
    }
   
    override def getBrief(key : AssetKey) : AssetBrief = 
       getAsset (key).asInstanceOf[AssetBase].brief

    override def doAction(cmd : String, key : AssetKey, ctx : ActionContext) : AnyRef = "nothing"

    override def getSupportedActions(key : AssetKey) : Array[ActionItem] = Array()

//    def init (meta : Meta) = proxy.init(meta)
   
   /** queries can run in the background, they are multithreaded safe etc */
   /** default only directs to queryAll ... */
   override def query(criteria:QueryCriteria, env:AssetLocation , recurse:Boolean , toUse:AssetMap) : AssetMap = {
      val ret = if (toUse != null) toUse else new AssetMap
      
      criteria match {
         case AllOfType (Upnp.Device) => 
            Upnp.uc.devices.map (Upnp.makeDevice(_)).foreach (x => ret.put(x.key, x.brief))
         case ByAssoc (d, Upnp.DeviceHasServices) => 
            getAsset(d).asInstanceOf[AUpnpDevice].services.foreach  (x => ret.put(x.key, x.brief))
         case _ => 
            throw new IllegalArgumentException ("I only support AllOfType queries right now")
      }
      ret
    }

    /** queries can run in the background, they are multithreaded safe etc */
    override def queryAll(meta:String, env:AssetLocation , recurse:Boolean , toUse:AssetMap) : AssetMap = {
      val ret = if (toUse != null) toUse else new AssetMap
//       
//      meta match {
//          Upnp.uc.devices.map (new AUpnpDevice (_)).foreach (x => ret.put(x.key, x.brief))
//       case Upnp.Service => {
//       }
//       }
//       
       ret
    }
}

object RunMe extends Application {
   org.cybergarage.util.Debug.on()
   val cp = new UpnpControlPoint()
   cp.start

     
   Thread.sleep(2000)
     
   println ("Devices: " + cp.devices.mkString("\n"))

   val d = cp.devices.filter (_.friendlyName=="whs : TVersity Media Server").map(Upnp.makeDevice(_)).head
   val da = d.asInstanceOf[AUpnpMediaServer]

   val bb = { x:String => {
      println (x+"--------------------")
      var rma = da.Browse (x, "BrowseMetadata", "0", "100")
      println (rma.outArgs.sa ("Result"))
      println ("--------------------")
      rma = da.Browse (x, "BrowseDirectChildren", "0", "100")
      println (rma.outArgs.sa ("Result"))
   }}

   b("0")
   b("0/3")
   b("0/3/40")
   b("0/3/40/41")
   b("0/3/40/41/42")
   b("0/3/40/41/42/34")

   cp.stop
   
   def b (x:String) = {
      println (x+"--------------------")
      var rma = da.Browse (x, "BrowseMetadata", "0", "100")
      println (rma.outArgs.sa ("Result"))
      println ("--------------------")
      rma = da.Browse (x, "BrowseDirectChildren", "0", "100")
      println (rma.outArgs.sa ("Result"))
   }
  
}

