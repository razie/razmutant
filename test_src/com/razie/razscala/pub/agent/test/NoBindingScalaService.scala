package com.razie.razscala.pub.agent.test

import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream

/** just a sample agent service written in SCALA */
@SoaService(name = "testservice", descr = "sample scala service" )
class NoBindingScalaService extends AgentService {

    def ECHOMSG () = "TEST ECHO msg="
    
	/** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("SCALA service onstartup");
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("SCALA service onshutdown");
    }

    @SoaMethod(descr = "echo service", args = Array( "msg" ))
    def echo (msg:String) =  ECHOMSG+msg
    
}
