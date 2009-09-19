package com.razie.media

import com.razie.pub.agent.AgentService
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.assets.AssetKey
import com.razie.pub.base.log.Log
import com.razie.pub.draw.DrawStream
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.assets.AssetMgrScala
import com.razie.media.config.MediaConfig

/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 */
@SoaService(name = "media", bindings = Array("http"), descr = "some media services" )
class MediaService extends AgentService {

    /** the second initialization phase: the agent is starting up */
  override def onStartup() : Unit = {
     println("MediaService onStartup");
     
     MediaConfig getInstance
      
     AssetMgrScala inject new OrganizeMovies
    }
  
    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("MediaService onShutdown");
    }

    // TODO @SoaMethod() {val descr = "?", val perm = SoaMethod.PermType.VIEW, val args = Array("ref")}
    @SoaMethod(descr = "?", args = Array("ref"))
    @SoaStreamable
    def browse (out:DrawStream, ref:String):Unit = {
      val kref = AssetKey.fromString(ref);
  }
}
