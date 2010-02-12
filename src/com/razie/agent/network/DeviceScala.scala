package com.razie.agent.network

import com.razie.pub.assets._
import com.razie.agent.network._
import com.razie.agent.network.Device.Impl
import com.razie.pub.lightsoa._
import razie.assets._
import razie.draw._

@SoaAsset(meta = "Device", descr = "?")
class DeviceScala (key:AssetKey) extends Impl(key) {
   
    /** get some extra details about an asset */
   override def render(t:Technology , out:DrawStream ) : AnyRef = {
     Devices.device(getKey.getId) match {
       case null => 
//                out.write(new DrawError("Cannot find device info for " + who));
         throw new IllegalArgumentException ("Cannot find device info for " + getKey);
       case x => {
         val out = new DrawSequence();
         val o = x.render (Technology.HTML, out);
         if (o != null) out.write(o)

         out
         }
     } 
   }

   //    /** get some extra details about an asset */
//   override def getDetails(asset:AssetBrief ) : Drawable = { 
//     Devices.device(asset.getKey.getId) match {
//       case null => 
////                out.write(new DrawError("Cannot find device info for " + who));
//         throw new IllegalArgumentException ("Cannot find device info for " + asset.getKey);
//       case x => {
//         val out = new DrawSequence();
//         val o = x.render (Renderer.Technology.HTML, out);
//         if (o != null) out.write(o)
//
//         out
//         }
//     } 
//   }

}
