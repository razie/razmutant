/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.agent.test

import com.razie.pub.lightsoa._ 
import com.razie.pub.agent._ 
import com.razie.pub.comms._
import com.razie.pub.base._
import org.scalatest.junit._

class AhoiService extends AgentService with AgentMsgHandler {
   override def receive (from:AgentHandle, m:Any) = {
     "ahoi, ahoi service: " + razie.Agent.me.port + m.toString
   }
}

/** test asset definition samples, complete with bindings */
class TestMsgService extends JUnit3Suite {

   var agents:Array[razie.SimpleAgent] = null

   override def setUp = {
      NoStatics.resetJVM()
      LightAuth.init("mutant")
      agents = razie.SimpleAgent.localCloud(4446 to 4448).toArray
      // pay attention to this inContext - otherwise you'll waste hours debugging code :) :) :)
      for (a <- agents) a.onInit.onStartup.inContext {
         a register new AgentMsgService 
         a register new AhoiService
      }
   }

   // send to one agent
   def testSendTo = expect (true) {
      val resp = ServiceMsg("AhoiService", new java.lang.Float(1234.56)).sendTo (agents(0).getHandle)
      resp.contains("ahoi") && resp.contains("1234.56")
   }

   // send to one agent
   def testSendTo2 = expect (true) {
      val resp = razie.Service ("AhoiService").msg (new java.lang.Float(1234.56)).sendTo (agents(0).getHandle)
      resp.contains("ahoi") && resp.contains("1234.56")
   }

   // TODO 2-1 add an asset message test
   
   def testSendToAll = expect (true) {
      val resp = AgentMsg.sendTo (".*", "AhoiService", new java.lang.Float(1234.56))
      resp.size == 3 && resp.foldLeft (true)((x,y) => x && y.contains("ahoi") && y.contains("1234.56")) 
   }

   override def tearDown = {
      agents.foreach (_.onShutdown.join)
      agents = null
      super.tearDown
   }
}

