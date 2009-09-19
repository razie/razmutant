package com.razie.mutant

import com.razie.pub.agent.AgentHttpService;

import com.razie.pub.agent.AgentService;

import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import com.razie.pub.draw.DrawStream

import com.razie.assets._

/** just a sample agent service written in SCALA */
@SoaService(name = "scala", descr = "sample scala service" )
class SampleScalaAgentService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("SCALA service onstartup");

      AgentHttpService.registerSoa(new HttpSoaBinding(this));
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("SCALA service onshutdown");
    }

    @SoaMethod(descr = "no parms")
    def testCAT() =  {
     //(new PaintCategories("/mutant/cmd/find?type=Movie&", "category")).paint
    }
    
    @SoaMethod(descr = "no parms")
    def Method1() = "what up?"

    @SoaMethod(descr = "1 parm", args = Array( "msg" ))
    def Method2 (msg:String) =  "SCALA Method2 msg="+msg
    
    @SoaMethod(descr = "1 parm streamable", args = Array( "msg" ))
    @SoaStreamable
    def Method3 (out:DrawStream, msg:String):Unit = out write ("SCALA Method3 msg="+msg)
    
}
