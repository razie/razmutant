package com.razie.agent

import com.razie.pub.base._
import com.razie.agent.webservice._
import com.razie.agent._
import com.razie.agent.network._
import com.razie.agent.upnp._
import com.razie.agent.pres._
import com.razie.pub.comms._

/**
 * this agent manages assets
 * 
 * @author razvanc
 */
@NoStaticSafe
class RazAgent (h:AgentHandle, c:AgentCloud, pluginsLocations:List[String]) extends AssetAgent (h,c) {

   override def onInit = inContext[RazAgent] {
         super.onInit
         		
//      MutantPresentation.addPresentation(MutantPresentation.XMLDOC);

//      register(new TempAgentNetworkService())
//      register(new AgentUpnpService(Devices.getInstance()));
        
      // initialize the JS support - takes a while...
      razie.Threads.run {
            // TODO ANOW reinstate this back when scala works
//          ScriptFactory.make(null, "1+2").eval(ScriptContext.Impl.global())
      }

     this
   }
   
}