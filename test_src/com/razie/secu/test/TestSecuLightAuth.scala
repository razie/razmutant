package com.razie.secu.test

import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.secu._
import com.razie.pub.comms._
import com.razie.pub.agent._
import java.security.cert._
import java.security._
import java.security.spec._
import com.razie.agent._
import com.razie.agent.network._
import com.razie.pub.assets._


/** make sure the seculightauth works... */
class TestSecuLightAuth extends JUnit3Suite {
   val  me = new AgentHandle("testagent1", "localhost", "127.0.0.1", "4446", "http://localhost:4446", "?", ".");
   
   def testA = expect (true) {
      // start agent
     new SimpleAgent (me, new AgentCloud(me))
     
    val c:ComputerScala = new ComputerScala (new AssetKey ("Device", me.name), Computer.Type.LAPTOP)
     c.setHandle (me)
    
     //reinitialize the security
   c.resetSecurity("pass")

    // send a request
   val ati = new ServiceActionToInvoke (me.url, "control", new ActionItem("Status"))
   val resp:String = ati.act(null).asInstanceOf[String]
   resp.contains ("running OK")  
   }
   
}
