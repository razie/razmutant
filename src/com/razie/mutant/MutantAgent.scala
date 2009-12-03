package com.razie.mutant;


import razie.SimpleAgent;

import com.razie.agent.network.Devices;
import com.razie.agent.pres.AgentNetworkService;
import com.razie.agent.upnp.AgentUpnpService;
import com.razie.dist.db.AgentDbService;
import com.razie.pub.agent.Agent;
import com.razie.pub.base.NoStaticSafe;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.webui.MutantPresentation;
import com.razie.pub.base.data._
import com.razie.pub.base._

object MutantAgent {
   /**
    * get the current instance (can be many per JVM)
    */
   def getInstance() = Agent.instance() match {
          case x : Agent => x.asInstanceOf[MutantAgent]
          case _ => throw new IllegalStateException("agent NOT intiialized");
   }
}

/**
 * this is the full-blown mutant agent, with its specific services etc
 * 
 * @author razvanc
 */
@NoStaticSafe
class MutantAgent (myHandle:AgentHandle , homeGroup:AgentCloud ) extends SimpleAgent (myHandle, homeGroup) {
      razie.FullAgent.apply (this);

   /**
    * called when main() starts up but before onStartup(). Initialize all
    * services from the configuration file
    */
   override def onInit() = {
      super.onInit();
      getContext().enter();

      MutantPresentation.addPresentation(MutantPresentation.XMLDOC);
      
      // initialize rest in separate thread to speed up startup response time
      razie.Threads.fork {
         inContext {
            register(new AgentDbService());
         }
      }
      
      this
   }
}
