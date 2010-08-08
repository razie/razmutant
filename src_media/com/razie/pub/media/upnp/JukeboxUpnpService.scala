package com.razie.pub.media.upnp;

import razie.base._
import com.razie.pub.agent.AgentService;
import razie.draw._
import razie.draw.Drawable
import com.razie.pub.lightsoa._
import razie.assets._
import com.razie.pub.comms._
import com.razie.pub.assets._
import com.razie.pub.base._
import com.razie.pub.media._
import com.razie.pub.resources._

/**
 * starts a UPNP device for the agent
 * 
 * @author razvanc
 * @version $Id$
 */
@SoaService(name = "jukebox", bindings = Array("http"), descr = "upnp media services" )
class JukeboxUpnpService extends AgentService {
    var device1 : JukeboxUpnpDevice = null;
    var device2 : Option[JukeboxUpnpDevice] = None;

    protected override def onStartup() {
        device1=new JukeboxUpnpDevice(JukeboxUpnpDevice.DESCRIPTION_FILE_NAME, "MutantJukebox1");
//        device2=Some(new JukeboxUpnpDevice(JukeboxUpnpDevice.DESCRIPTION_FILE_NAME2, "MutantJukebox2"))
        
        device1.start();
        device2 map {_.start()}
    }

    protected override def onShutdown() {
        device1.stop();
        device2 map {_.stop()}
    }
    
    // TODO @SoaMethod() {val descr = "?", val perm = SoaMethod.PermType.VIEW, val args = Array("ref")}
    @SoaMethod(descr = "?")
    @SoaStreamable
    def showTree (out:DrawStream):Unit = {
      out.write(razie.media.upnp.UpnpServer.root)
    }
    
//      out.write(device1.server.BrowseImpl("0", "BrowseMetadata", "", "0", "100", new Next("debugNext")))
//      out.write(device1.server.BrowseImpl("0", "BrowseXXXXX", "", "0", "100", new Next("debugNext")))
//      out.write(device1.server.BrowseImpl("0", "BrowseMetadata", "", "0", "100", NoAffordance()))
//      out.write(device1.server.BrowseImpl("0", "BrowseXXXXX", "", "0", "100", NoAffordance()))
    
    // TODO @SoaMethod() {val descr = "?", val perm = SoaMethod.PermType.VIEW, val args = Array("ref")}
    @SoaMethod(descr = "?")
    @SoaStreamable
    def browseRoot (out:DrawStream):Unit = {
      out.write(Aff ("browseNext", device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext"))))
//      out.write(device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext")))
      out.write (razie.Draw.hBar)
//      out.write(device1.server.BrowseImpl("0", "BrowseXXXXX", "", "0", "100", Next("BrowseNext")))
  }
   
   def print (c:JUpnpContainer, out:DrawStream, aff:Affordance) {
      out.write(device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext")))
   }
    
    @SoaMethod(descr = "?", perm = PermType.VIEW, args = Array("ref"))
    @SoaStreamable
    def browseNext (out:DrawStream, ref:String):Unit = {
      val c = device1.server.findContainer(ref, "BrowseMetadata", "", "0", "100", Next("BrowseNext"))
      c.refresh
      c.containers.foreach (x=>out.write(Aff("browseNext", x)))
      out.write (razie.Draw.hBar)
      c.items.foreach (out.write(_))
      out.write (razie.Draw.hBar)
      out.write (device1.server.toDidl(c, ref,"BrowseMetadata", "", "0", "100", Next("BrowseNext")))
      out.write (razie.Draw.hBar)
      out.write (device1.server.toDidl(c, ref, "BrowseXXX", "", "0", "100", Next("BrowseNext")))
      
  }
    
    // TODO @SoaMethod() {val descr = "?", val perm = SoaMethod.PermType.VIEW, val args = Array("ref")}
    @SoaMethod(descr = "?")
    @SoaStreamable
    def upnp (out:DrawStream):Unit = {
      out.write(Aff ("browseNext", device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext"))))
//      out.write(device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext")))
      out.write (razie.Draw.hBar)
//      out.write(device1.server.BrowseImpl("0", "BrowseXXXXX", "", "0", "100", Next("BrowseNext")))
  }
   
    // TODO @SoaMethod() {val descr = "?", val perm = SoaMethod.PermType.VIEW, val args = Array("ref")}
    @SoaMethod(descr = "?")
    @SoaStreamable
    def upnpNext (out:DrawStream):Unit = {
      out.write(Aff ("browseNext", device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext"))))
//      out.write(device1.server.findContainer("0", "BrowseMetadata", "", "0", "100", Next("BrowseNext")))
      out.write (razie.Draw.hBar)
//      out.write(device1.server.BrowseImpl("0", "BrowseXXXXX", "", "0", "100", Next("BrowseNext")))
  }
   
}

case class Next (what:String) extends razie.assets.Affordance {
   override def make (k:AssetKey, o:AnyRef) = 
      Array (new ServiceActionToInvoke("jukebox", new ActionItem(what, k.getId()), "ref", k.toUrlEncodedString))
}

case class AffList[T<:IReferenceable] (what:String, o:List[T]) (implicit m:scala.reflect.Manifest[T]) extends Drawable with razie.assets.Affordance {
   override def make (k:AssetKey, ol:AnyRef) = 
      Array (new ServiceActionToInvoke("jukebox", new ActionItem(what, RazIcons.FOLDER.name, k.getId(), ""), "ref", k.toUrlEncodedString))

   override def render(t:Technology, out:DrawStream ) = {
      razie.Draw.list( for (ob <- o) yield razie.Draw.list(ob, for (a <- make (ob.key, ob)) yield a.render(t,out)))
   }
}

case class Aff[T<:JUpnpContainer] (what:String, o:T) (implicit m:scala.reflect.Manifest[T]) extends Drawable with razie.assets.Affordance {
   override def make (k:AssetKey, oo:AnyRef) = 
      Array (new ServiceActionToInvoke("jukebox", new ActionItem(what, RazIcons.FOLDER.name, k.getId(), ""), "ref", o.upnpID))

   override def render(t:Technology, out:DrawStream ) = {
      razie.Draw.list(for (a <- make (o.key, o)) yield a).render(t,out)
   }
}
