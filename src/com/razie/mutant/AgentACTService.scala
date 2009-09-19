package com.razie.mutant

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.lightsoa._
import com.razie.pub.base._

/** ensure that shared drives stay shared, every time the mutant starts - sometimes windows loses the shares of external drives 
 
 TODO implement this
 */

//TODO @SoaService(){val name = "ACT", val descr = "implementation of ACT on assets" }
@SoaService(name = "ACT", descr = "implementation of ACT on assets" )
class AgentACTService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("onStartup");

      AgentHttpService.registerSoa(new HttpSoaBinding(this));
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("ACT service onshutdown");
    }

    @SoaMethodSink()
    //TODO @SoaMethod(){val descr = "1 parm", val args = Array( "msg" )}
    @SoaMethod(descr = "1 parm", args = Array( "msg" ))
//    @SoaAllParms
    def sinkall (parms:AttrAccess) = {
      // TODO complete
    }
}
