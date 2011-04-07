package scala

import com.razie.pub.comms._
import com.razie.agent.network._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.agent._

//object Mutant {
//    
//    def on (agentName:String) (f : => Unit) = {
//    }
//
//    def apply (agentName:String) = {
//        new MutantHandle (Agents.agent(agentName))
//    }
//    
//    def apply () = {
//        new MutantHandle (Agents.me())
//    }
//    
//    def apply (f : => Unit) = {
//        f
//    }
//}
//
//class MutantHandle (val agent:AgentHandle) {
//    
//    def apply (f : => Unit) = {
//        // TODO i don't know how to do this
//        throw new UnsupportedOperationException("needs implemented")
//    }
//
//    def stop  () = new ServiceActionToInvoke(agent.url, "control", Device.cmdSTOP) act ScriptContext.Impl.global()
//    def upgrade () =  new ServiceActionToInvoke(agent.url, "control", Device.cmdUPGRADE) act ScriptContext.Impl.global()
//    def die() = new ServiceActionToInvoke(agent.url, "control", Device.cmdDIE) act ScriptContext.Impl.global()
//
//   def run (script:String) = {
//        //TODO
//    } 
//
////    def services () : List[String] = {
////       val list = for (s <- RazElement.tolist(Agent.instance().copyOfServices()).sort(_.getClass().getSimpleName()<_.getClass().getSimpleName())) yield s.getClass.getSimpleName()
////       list
////    }
//}