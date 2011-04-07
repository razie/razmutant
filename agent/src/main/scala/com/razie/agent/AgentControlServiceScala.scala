package com.razie.agent

import com.razie.pub.lightsoa._
import com.razie.pub.base.log._
import com.razie.pub.comms._
import com.razie.pub.agent._
import com.razie.agent.webservice._

class AgentControlServiceScala extends AgentControlService {
   
   @SoaMethod(descr = "see status of mutant", perm=PermType.CONTROL)
   def Status() = razie.Draw.seq (
         "Mutant Daemon status: running OK... ver " + Version.getVersion(),
      // TODO get their classnames only
         "\nListeners: " + Log.tryToString("   ", AgentHttpService.getHandlers()),
         "\nBindings: " + Log.tryToString("   ", AgentHttpService.getBindings()),
         "\nSecurity: " + LightAuthBase.instance(),
         razie.Draw.html ("<p>Config files:"+pf("cfg/user.xml","cfg/agent.xml", "cfg/assets.xml", "cfg/media.xml")))

   @SoaMethod(descr = "backup mutant", perm=PermType.CONTROL)
   def Backup() = {
      val mutantdir = AgentConfig.getMutantDir();
      val targetdir = mutantdir + "/backup-"+String.valueOf(java.lang.System.currentTimeMillis);

      // copy what's in my upgrade...up
      CmdFilesScala.copyDir(mutantdir, targetdir, true, Array("log", "lib", "upgrade", "backup-.*"))

      "OK - Backed up to "+targetdir;
   }

   @SoaMethod(descr = "backup mutant", perm=PermType.CONTROL)
   def Recover() = {
      val files = new java.io.File(AgentConfig.getMutantDir()).listFiles
      val ai = razie.AI("RecoverFrom")

      razie.Draw.seq ("WARNING - this will NOT backup your current files, just recover...",
      razie.Draw.seq (files.filter(_.isDirectory).map (
            (f:java.io.File) => {new ServiceActionToInvoke("control", ai, "dir", f.getCanonicalPath)}
            ) :_*)
      )
   }

   @SoaMethod(descr = "backup mutant", args=Array("dir"), perm=PermType.CONTROL)
   def RecoverFrom(dir:String) = {
      val files = new java.io.File(AgentConfig.getMutantDir()).listFiles
      val ai = razie.AI("RecoverFrom")
      
      razie.Draw.seq (files.filter(_.isDirectory).map (
            (f:java.io.File) => {new ServiceActionToInvoke("control", ai, "dir", f.getCanonicalPath)}
            ) :_*)
   }

  def pf (s:String*) = for (f <-s) yield " <a href=\"config?file="+f+"\">"+f+"</a>"

}
