package com.razie.mutant

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream
import com.razie.pub.base.log._

/** ensure that shared drives stay shared, every time the mutant starts - sometimes windows loses the shares of external drives 
 
 TODO implement this
 */
@SoaService(name = "sharing", descr = "keep drives shared" )
class ShareAgentService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("onStartup");

      AgentHttpService.registerSoa(new HttpSoaBinding(this));
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("SCALA service onshutdown");
    }

    @SoaMethod(descr = "no parms")
    def Method1() = "what up?"

    @SoaMethod(descr = "1 parm", args = Array( "msg" ))
    def Method2 (msg:String) =  "SCALA Method2 msg="+msg
    
    @SoaMethod(descr = "1 parm streamable", args = Array( "msg" ))
    @SoaStreamable
    def Method3 (out:DrawStream, msg:String):Unit = out write ("SCALA Method3 msg="+msg)
}
