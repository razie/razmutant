package com.razie.play.proxy

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream
import razie.draw.widgets._
import com.razie.pub.assets._
import com.razie.pub.comms._
import razie.base._

import com.razie.assets._

/** just a sample agent service written in SCALA */
@SoaService(name = "proxy", bindings=Array("http"), descr = "simple proxy to other mutant" )
class AgentProxyService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      razie.Log("SCALA service onstartup");
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      razie.Log("SCALA service onshutdown");
    }

    @SoaMethod(descr = "serve proxied page", args = Array( "url", "via" ))
    def serve (url:String, via:String) =  {
       // TODO stream this... as read line by line ?
       val page = com.razie.pubstage.comms.HtmlContents.justBody(com.razie.pub.comms.Comms.readUrl (url))
       
       val loc = new razie.assets.AssetLocation (url)
       val target = "http://"+loc.getHost+":"+loc.getPort
       
       var newpage = page.replaceAll ("src=\\\"", "src=\""+via+"/mutant/proxy/serve?via="+via+"&url="+target)
       newpage = newpage.replaceAll ("href=\\\"", "href=\""+via+"/mutant/proxy/serve?via="+via+"&url="+target)
       // take care of duplicates with full url
       newpage = newpage.replaceAll (target+"http://", "http://")
       // now the local (relative) links
       newpage = newpage.replaceAll (target+"[^&/]", target+"/")
       
//       val m2 = java.util.regex.Pattern.compile ("(.*)(href=\\\")(.*)").matcher(newpage)
       
       new DrawToString (newpage)
    }
    
    @SoaMethod(descr = "start serving mutant page", args = Array( "url", "via" ))
    def start (agentName:String) =  {
       val via = Clouds.via (Agents.me, Agents.handleOf(agentName)) 
    }
}

object Clouds {
   def proxy (ati:ActionToInvoke) = 
      razie.Service ("proxy") action ("serve", razie.AA ("via", Agents.me.url, "url", ati.makeActionUrl))
      
   def proxy (url:String) = 
         razie.Service ("proxy") action ("serve", razie.AA ("via", Agents.me.url, "url", url))

   def via (from:AgentHandle, to:AgentHandle):AgentHandle = {
      // on net / off net: there's two possibilities: target and me are colocated or not
      val onnet = to.isUp // TODO 3-2 CLOUD should be smarter :) this could change when I start maintaining cloud state

      // TODO 1-2 CLOUD figure out the network target is on and use its proxy
      val via = if (onnet) to else Agents.agent ("kk")
      
     null 
   }
}
