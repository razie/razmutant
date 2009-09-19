package com.razie.agent.network;

import java.io.File;

import org.w3c.dom.Element;

import com.razie.pub.UnknownRtException;
import com.razie.pub.assets._
import com.razie.assets._
import com.razie.pub.assets.AssetMgr.Meta;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ActionToInvoke;
import com.razie.pub.draw._;
import com.razie.pub.draw.DrawList;
import com.razie.pub.draw.widgets.NavButton;
import com.razie.sdk.finders.AssetFinder

import com.razie.agent.network._

import com.razie.pub.resources._
import com.razie.pub.agent._
import com.razie.pub.lightsoa._

/**
 *  TODO comment
 */
class DeviceInventory extends AssetInventory with com.razie.assets.ScalaInventory {

  override def get(ref:AssetKey):AnyRef = 
     Devices.device (ref.getId)

   override def getBrief(ref:AssetKey ) : AssetBrief = {
     Devices.device(ref.getId).getBrief()
   }

    /** list all assets of the given type at the given location */
    override def xfind(ttype:String, env:AssetLocation, recurse:Boolean)
    :scala.collection.mutable.Map[AssetKey, AssetBrief] = {
      var ret = new scala.collection.mutable.HashMap[AssetKey, AssetBrief]()
     
      val m = Devices.getInstance().copyOfDevices()
      val iter = m.values.iterator
     
      while (iter.hasNext()) {
        val x = iter.next()
        ret.put (x.getKey(), x.getBrief())
        }
        
      ret
      }

    /** execute command on asset. the asset can be local or remote */
   override def doAction(cmd:String , ref:AssetKey , ctx:ScriptContext ):Object = {
     cmd match {
       case "gigi" => null
       case _ => {
           // TODO make this more efficient and extend to all assets
    	   new SoaBinding (get(ref), "kuku").invoke(cmd, ctx)
          }
       }
   }

   override def getSupportedActions(ref:AssetKey ) : Array[ActionItem] = 
     if (Devices.device(ref.getId()).getPort().length > 0) DeviceStuff.hostCOMMANDS else Array()

    /** get some extra details about an asset */
   override def details(asset:AssetBrief ) : Drawable = { 
     Devices.device(asset.getKey.getId) match {
       case null => 
//                out.write(new DrawError("Cannot find device info for " + who));
         throw new IllegalArgumentException ("Cannot find device info for " + asset.getKey);
       case x => {
         val out = new DrawSequence();
         val o = x.render (Renderer.Technology.HTML, out);
         if (o != null) out.write(o)

         out
         }
     } 
   }

    
    /** initialize this instance for use with this Meta */
   override def init(meta:Meta ) = {}
}

object DeviceStuff {
    val cmdDEVICEINFO  = new ActionItem("deviceInfo", RazIcons.UNKNOWN);

    val cmdUPGRADETO   = new ActionItem("upgradeTo", RazIcons.UPLOAD);
    val cmdUPGRADEFROM = new ActionItem("upgradeFrom", RazIcons.DOWNLOAD);
    val cmdSTOP        = new ActionItem("Stop", RazIcons.POWER);
    val cmdUPGRADE     = new ActionItem("upgrade", RazIcons.UPGRADE);
    val cmdDIE         = new ActionItem("Die", RazIcons.POWER);

    val sCLASS = "Device";
    val AMETA:ActionItem       = new ActionItem(sCLASS, "device");

	val META = new AssetMgr.Meta(AMETA, "", classOf[DeviceScala].getName(),
	    	      classOf[DeviceInventory].getName());

     val hostCOMMANDS = Array(DeviceStuff.cmdUPGRADETO, DeviceStuff.cmdUPGRADEFROM,
           DeviceStuff.cmdSTOP,DeviceStuff.cmdUPGRADE,DeviceStuff.cmdDIE)
}
