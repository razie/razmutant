package com.razie.mutant

import java.net._
import com.razie.pub.base.data._

object FindIp {
  def main(args : Array[String]) : Unit = {
    println(list mkString " ")
  }
  
  def list() = {
    val nes =  NetworkInterface.getNetworkInterfaces()
    var li = new java.util.ArrayList[String]()
    
    while (nes.hasMoreElements) {
      val ne=nes.nextElement
      val iae = ne.getInetAddresses
      while (iae.hasMoreElements) {
        val ia=iae.nextElement
        //TODO optimize stupid statement
        li.add(ia.getHostAddress)
      }
    }

    // TODO avoid this and build a freaking scala list...
    razie.RJS apply li
  }
}
