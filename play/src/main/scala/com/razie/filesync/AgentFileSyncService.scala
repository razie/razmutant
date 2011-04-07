package com.razie.filesync

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log._
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream

/** 
 * will sync a list of folders with their contents
 */
@SoaService(name = "filesync", descr = "sync files service" )
class AgentFileSyncService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      Log.logThis("filesync service onstartup");

      AgentHttpService.registerSoa(new HttpSoaBinding(this));
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
    }


//TODO     @SoaMethod(){val descr = "returns a list: contents of one folder", val args = Array( "folder" )}
    @SoaMethod(descr = "returns a list: contents of one folder", args = Array( "folder" ))
    def ListDir(folder:String) = {
      val fd = new java.io.File (folder)
      val fas = for (f <- fd.listFiles) yield FileAbstract.fromFile (f)
     
      fas
    }

    @SoaMethod(descr = "returns a list: contents of one folder")
    def TestListDir() = {
      ListDir ("c:/video/razmutant")
    }

    @SoaMethod(descr = "sync all folders on all mutants")
    def SyncAll() = {
    }

    @SoaMethod(descr = "sync one folder", args = Array( "folder", "fromHost" ))
    def SyncOne (name:String,fromHost:String) =  {
    }

    def sync (srcFolder:String,destFolder:String)={
      
    }
}
