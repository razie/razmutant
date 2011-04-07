/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.agent.network;

import java.io.File;

import org.w3c.dom.Element;

import com.razie.pub.UnknownRtException;
import com.razie.pub.assets._
import razie.base.ActionItem;
import razie.base.scripting.ScriptContext;
import com.razie.pub.base.data.HttpUtils;
import razie.base.data.XmlDoc;
import com.razie.pub.base.log.Log;
import razie.base._
import razie.draw._;
import razie.draw.DrawList;
import razie.draw.widgets.NavButton;
import com.razie.sdk.finders.AssetFinder

import com.razie.agent.network._

import com.razie.pub.resources._
import com.razie.pub.agent._
import com.razie.pub.lightsoa._
import razie.assets._

/**
 *  TODO comment
 */
class DeviceInventory extends BaseInventory {

  override def getAsset(ref:AssetKey):AnyRef = 
     Devices.device (ref.getId)

   override def getBrief(ref:AssetKey ) : AssetBrief = {
     Devices.device(ref.getId).getBrief()
   }

    /** list all assets of the given type at the given location */
   override def queryAll(meta:String, env:AssetLocation , recurse:Boolean , ret:AssetMap) : AssetMap = {
      val m = Devices.getInstance().copyOfDevices()
      val iter = m.values.iterator
     
      while (iter.hasNext()) {
        val x = iter.next()
        ret.put (x.getKey(), x.getBrief())
        }
        
      ret
      }

    /** execute command on asset. the asset can be local or remote */
//   override def doAction(cmd:String , ref:AssetKey , ctx:ScriptContext ):Object = {
//     cmd match {
//       case "gigi" => null
//       case _ => {
//           // TODO make this more efficient and extend to all assets
//    	   new HttpSoaBinding (getAsset(ref), "kuku").invoke(cmd, ctx)
//          }
//       }
//   }

   override def getSupportedActions(ref:AssetKey ) : Array[ActionItem] = 
     if (Devices.device(ref.getId()).getPort().length > 0) DeviceStuff.hostCOMMANDS else Array()
    
    /** initialize this instance for use with this Meta */
   override def init(meta:Meta ) = {}
}

object DeviceStuff {
    val cmdDEVICEINFO  = new ActionItem("deviceInfo", RazIcons.UNKNOWN.name);

    val cmdUPGRADETO   = new ActionItem("upgradeTo", RazIcons.UPLOAD.name);
    val cmdUPGRADEFROM = new ActionItem("upgradeFrom", RazIcons.DOWNLOAD.name);
    val cmdSTOP        = new ActionItem("Stop", RazIcons.POWER.name);
    val cmdUPGRADE     = new ActionItem("upgrade", RazIcons.UPGRADE.name);
    val cmdDIE         = new ActionItem("Die", RazIcons.POWER.name);

    val sCLASS = "Device";
    val AMETA:ActionItem       = new ActionItem(sCLASS, "device");

	val META = new Meta(AMETA, "", classOf[DeviceScala].getName(),
	    	      classOf[DeviceInventory].getName(), "razie");

     val hostCOMMANDS = Array(DeviceStuff.cmdUPGRADETO, DeviceStuff.cmdUPGRADEFROM,
           DeviceStuff.cmdSTOP,DeviceStuff.cmdUPGRADE,DeviceStuff.cmdDIE)
}
