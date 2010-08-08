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
import com.razie.pub.webui._
import com.razie.agent.webservice._
import com.razie.pub.base._
import razie.base._
import razie.base.scripting._

/**
 * this is a fully functional agent, with servce/asset/cloud management
 * 
 * @author razvanc
 */
@NoStaticSafe
object FullAgent {

   def apply (a:SimpleAgent, pluginsLocations:List[String] = List()) = a.inContext[SimpleAgent] {
      AssetAgent.apply(a)
      a register(new com.razie.agent.pres.AgentNetworkService())
      a register(new com.razie.agent.upnp.AgentUpnpService({ Devices.getInstance } ));
      a register(new com.razie.pub.agent.AgentMsgService())

      // TODO install pugins
      
      // initialize the JS support - takes a while...
      razie.Threads.fork {
         // TODO this
          ScriptFactory.init(new ScriptFactoryScala(ScriptFactory.singleton, false));
          ScriptFactory.make(null, "1+2").eval(ScriptContextImpl.global())
      }

      // TODO load plugins
      
     a
   }
   
}
