package razie.media.upnp

import razie.base._
import com.razie.pub.assets._
import com.razie.pub.base._
import razie.assets._
import com.razie.pub.media._

object UpnpNodes {
   private val cache = new scala.collection.mutable.HashMap[String, java.lang.ref.SoftReference[UpnpContainer]]()
  
   def add (c:UpnpContainer) {
      if (c != null) UpnpNodes.cache.put(c.upnpID, new java.lang.ref.SoftReference[UpnpContainer](c))
   }
   
   def get (s:String) : Option[UpnpContainer] = cache.get(s) match {
      case Some(f) => if (f.get() != null) Some(f.get) else None
      case _ => None
   }
}

class UpnpContainer (title:ActionItem, ref:AssetKey, parentID:String, children:Int, _assetType:String) 
extends JUpnpContainer (title, ref, parentID, children, "") {
   
   this.assetType=_assetType

   UpnpNodes.add(this)
  
   // translate from asset type to upnp content type
   this.contents = assetType match {
      case "Movie" => "movie" 
      case "Photo" => "photo" 
      case "Music" => "music" 
      case s:String => s // maybe it was already a upnp tyype - play nice, don't get pissed.
   }
   
   def this (title:String, ref:AssetKey, parentID:String, children:Int, _assetType:String) = 
      this (razie.AI(title), ref, parentID, children, _assetType)

   def containers : List[UpnpContainer] = 
      (for (c <- razie.M.apply(super.getContainers())) yield c.asInstanceOf[UpnpContainer]).toList
   def items : List[AssetBrief] = 
      (for (c <- razie.M.apply(super.getItems())) yield c).toList
}
