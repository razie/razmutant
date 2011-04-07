package com.razie.pub.upnpscala

import razie.upnp._

object TestUpnpStuff {
  def main(args : Array[String]) : Unit = {
     testDevices
  }
  
  def testDevices () = {
     val cp = new UpnpControlPoint()
     cp.start
     cp.search
     
     Thread.sleep(2000)
     
     println ("Devices: " + cp.devices.mkString("\n"))
  }
}
