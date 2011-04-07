/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie

import com.razie.pub.comms._
import com.razie.agent._
import com.razie.agent.network._
import com.razie.pub.agent._
import com.razie.pub.base._
import com.razie.pub.http._
import com.razie.agent.webservice._

object SimpleAgent {
	/** create a local agent running on the given port
	 * 
	 * @param port the port to listen to
	 */
   def local(port:Int) = {
	   val h = razie.Agent.local(port.toString)
	   new SimpleAgent (h, new AgentCloud(h))
   }

	/** create a local agent running on the given port
	 * 
	 * @param port the port to listen to
	 */
   def local(port:String) = {
	   val h = razie.Agent.local(port)
	   new SimpleAgent (h, new AgentCloud(h))
   }

	/** create a set of local agents running in the given port range
	 * 
	 * @param ports the port to listen to
	 */
   def localCloud (ports:Iterable[Int]) = {
	   val handles = (for (port <- ports) yield razie.Agent.local(port.toString)).toList
	   val cloud = new AgentCloud ()
	   for (h <- handles) cloud.put(h)
	   (for (h <- handles) yield new SimpleAgent (h, cloud)).toList
   }

   /** create a simple agent with the given handle, in a cloud of 1 */
   def apply (h:AgentHandle) = 
	   new SimpleAgent (h, new AgentCloud(h))
   
   def apply (h:AgentHandle, c:AgentCloud) = 
	   new SimpleAgent (h,c)
}

/** a simple agent with the basic services - use this unless you need to tweak advanced functionality 
 * 
 * The agent has a set of customizable features and steps in its initialization. These are fixed here in the most likely sequence.
 * 
 * Initializing an agent implies these components: LightAuth, Agents, Devices, NoStatics
 */
class SimpleAgent (h:AgentHandle, c:AgentCloud) extends com.razie.pub.agent.Agent (h,c) {
   val disabledSvc = AgentConfig.instance xpa "/config/clouds/cloud[@name='home']/*/@disabled"
      
   /** execute a piece of code in my context - used during initialization normally 
    * 
    * You can safely embedd these calls
    */
   def inContext[A] (f: => A) : A = {
      // use my thread context now
      val old = getContext().enter();
      val res = f 
      ExecutionContext.exit(old); //agent.getMainContext().exit(null);
      res 
      }
   
   override def onStartup = { super.onStartup(); this }
   
   override def onInit = inContext[SimpleAgent] {
      // we need a server with cmdget to accept bindings for services. You
      // should do this for any agent
      val server = new LightServer(Integer.parseInt(me.port), 20, getContext(), new LightContentServer());

      // initListeners
      val g = new CmdGET();
      server.registerHandler(g);
      server.registerHandler(new LightCmdPOST(g));
      server.registerHandler(new CmdEcho());
      server.registerHandler(new CmdFilesServer());

      server.registerHandler(new LightCmdACT());
       
      register(new AgentHttpService(this, server));
      register(new AgentFileService(AgentConfig.getMutantDir()));
      register(new AgentControlServiceScala());

      initialized = true
 
//      onStartup();
      
      // if already initialized, this init ignored
      Devices.init(new Devices, h, this.homeCloud)
   
      this
   }
   
   override def register(name:String , l:AgentService ) {
      if (disabledSvc contains name)
         razie.Log audit "AUDIT_SVC_DISABLED " + name
      else super.register(name, l)
   }
}
