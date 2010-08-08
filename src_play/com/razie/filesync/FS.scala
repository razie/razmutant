package com.razie.filesync

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log._
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream


/** an action on a file: copy,move,compare,diff,remove */
abstract class FSAct (val src:FLoc, val dest:FLoc, val path:String, val act:String) {
  def doit;
}

//case class FSActCopyTo (src:FLoc, dest:FLoc, path:String) extends FSAct (src, dest, path, "copyto") {
// TODO this override IS IDIOT...read the freaking book!
case class FSActCopyTo (override val src:FLoc, override val dest:FLoc, override val path:String) extends FSAct (src, dest, path, "copyto") {
  def doit = {
    
  }
}

/** 
 * will sync a list of folders with their contents
 * 
*/
class FS extends FileSyncUseCases {

   def listDir (src:FLoc, dir:String) : List [FileAbstract] = {
//     if (src.location != null && src.location.length>0 && src.location != Agents.me().name) {
      // delegate remote 
 //    }
     null
   }
   
   def findDiffs (src:FLoc, dir:String)={}

   def kickFullSync (src:FLoc, dest:FLoc) = {
     
   }
   
   def notifiedFileChanged (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = {}
   
   
   
   /** UC1 - initial sync between two points */
   def uc1InitialSync (l1:FLoc, l2:FLoc) = {}

   /** UC2 - a file in source has been updated*/
   def uc2detectupdate (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = {}
   
   /** UC2 - a file in source has been updated*/
   def uc2updatefile (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = {}

   /** UC3 - a file in dest has been updated*/
   def uc3updatefile (l1:FLoc, l2:FLoc, pathindest:FLoc) = {}

   /** UC4 - a file in both src and dest has been updated*/
   def uc4conflict (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = {}

}
