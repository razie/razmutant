package com.razie.valueadd.security

import razie.base._
import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.valueadd.security._
import com.razie.pub.comms._
import com.razie.pub.agent._
import java.security.cert._
import java.security._
import java.security.spec._
import com.razie.agent._
import com.razie.agent.network._
import com.razie.pub.media._
import com.razie.pub.assets._
import razie.assets._

class MyDevices extends Devices {
  override def makeComputer(name:String , ttype:String ) :Computer = {
    val c = new ComputerScala(new AssetKey(Device.sCLASSNM_DEVICE, name), Computer.Type
                           .valueOf(ttype.toUpperCase()));
    return c;
  }
}

/** make sure the seculightauth works... */
class TestSecuLightAuth extends JUnit3Suite {
   val agents = razie.SimpleAgent.localCloud(4446 to 4448)

   override def setUp = {
      ExecutionContext.resetJVM
      agents.foreach ( agent => agent inContext {
         LightAuthBase.init(new ResetOnlyLightAuth("./keys/"+agent.handle().name, "mutant", null, true))
         Devices.init(new MyDevices, agent.handle, agent.homeCloud)
         agent.onInit.onStartup
         
         AssetMgr.init (new razie.assets.InventoryAssetMgr)
         agent register (new com.razie.agent.network.NewAgentNetworkService)
         
         val c:ComputerScala = new ComputerScala (new AssetKey ("Device", agent.handle.name), Computer.Type.LAPTOP)
         c.setHandle (agent.handle)
         
         //reinitialize the security
         c.resetSecurity("pass")
      })
     
   }

   override def tearDown = {
      agents.foreach (_.onShutdown.join)
      super.tearDown
   }

      // 0 and 1 do trust eachother
   def testTrust = expect (true) {
      agents(0) inContext {
         val c = new ComputerScala (new AssetKey ("Device", agents(1).handle.name), Computer.Type.LAPTOP)
         c.setHandle (agents(1).handle)
         c.accept("pass")
      }
      agents(1) inContext {
         val c = new ComputerScala (new AssetKey ("Device", agents(0).handle.name), Computer.Type.LAPTOP)
         c.setHandle (agents(0).handle)
         c.accept("pass")
      }
      
     // send any request, from 0 to 1
      agents(0) inContext {
        val ati = new ServiceActionToInvoke (agents(1).handle.url, "control", new ActionItem("Status"))
        val resp:String = ati.act(null).asInstanceOf[String]
        resp.contains ("running OK")  
      }
   }
   
      // 0 and 1 do not trust eachother
   def testFailure = expect (false) {
     // send any request, from 0 to 1
      try {
         agents(0) inContext {
        val ati = new ServiceActionToInvoke (agents(1).handle.url, "control", new ActionItem("Status"))
        val resp:String = ati.act(null).asInstanceOf[String]
        resp.contains ("running OK")  
      }
      } catch {
         case t:Throwable => false
            // TODO 3-1 if (t.getCause().getMessage().contains("HTTP response code: 500")) false  // success
//            else throw t
      }
   }
   
}
