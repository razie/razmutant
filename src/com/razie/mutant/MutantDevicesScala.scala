package com.razie.mutant

import com.razie.agent.network._
import com.razie.pub.assets._

class MutantDevicesScala extends MutantDevices {
    
  override def makeComputer(name:String , ttype:String ) :Computer = {
    val c = new ComputerScala(new AssetKey(Device.sCLASSNM_DEVICE, name), Computer.Type
                           .valueOf(ttype.toUpperCase()));
    return c;
  }

}
