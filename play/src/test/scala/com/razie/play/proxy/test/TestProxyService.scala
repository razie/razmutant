package com.razie.play.proxy.test

import razie.base._
import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.pub.comms._
import com.razie.pub.agent._
import java.security.cert._
import java.security._
import java.security.spec._
import com.razie.agent._
import com.razie.agent.network._
import com.razie.pub.assets._
import razie.draw._
import razie.draw.widgets._
import com.razie.pub.resources._
import com.razie.pub.lightsoa._

class TestProxyService extends JUnit3Suite {
   var proxy:razie.SimpleAgent = null
   var target:razie.SimpleAgent = null
   
   override def setUp = {
      LightAuth.init("mutant")
      target = razie.SimpleAgent.local (4446).onInit
      proxy = razie.SimpleAgent.local (4447).onInit
      proxy register new com.razie.play.proxy.AgentProxyService
      target register new ProxiedService
   }

   def donttestAValidateOriginalPage = expect (true) {
      val ati = new ServiceActionToInvoke (target.handle.url, "control", new ActionItem("Status"))
      val resp:String = ati.act(null).asInstanceOf[String]
      resp.contains ("running OK")  
   }
   
   def donttestBProxyWorks = expect (true) {
      val ati = new ServiceActionToInvoke (target.handle.url, "control", new ActionItem("Status"))
      val resp = proxy.inContext[String] {
         val p = razie.Agent.proxy(ati)
         p.act(null).asInstanceOf[String]
      }
      resp.contains ("running OK")
   }

   def testCSimple = expect (true) {
      val ati = new ServiceActionToInvoke (target.handle.url, "proxied", new ActionItem("kuku"))
      val respProxy = proxy.inContext[String] {
         val p = razie.Agent.proxy(ati)
         p.act(null).asInstanceOf[String]
      }
      val respOrig:String = ati.act(null).asInstanceOf[String]
      println (">>>>>>>>>>>>>>>ORIG PAGE: \n"+respOrig)
      println (">>>>>>>>>>>>>>>PROXIED PAGE: \n"+respProxy)
      respProxy.contains ("kuku")  
   }

   def testDProxyButtons = expect (true) {
      val ati = new ServiceActionToInvoke (target.handle.url, "proxied", new ActionItem("buttons"))
      val respProxy = proxy.inContext[String] {
         val p = razie.Agent.proxy(ati)
         p.act(null).asInstanceOf[String]
      }
      val respOrig:String = ati.act(null).asInstanceOf[String]
      println (">>>>>>>>>>>>>>>ORIG PAGE: \n"+respOrig)
      println (">>>>>>>>>>>>>>>PROXIED PAGE: \n"+respProxy)
      respProxy.contains ("href=\"http://localhost:4447/mutant/proxy/serve?via=http://localhost:4447&url=http://localhost:4446/mutant/control/Status")  
   }

   override def tearDown = {
      proxy.onShutdown.join
      target.onShutdown.join
      super.tearDown
   }
}

object X {
   def proxy (ati:ActionToInvoke) = {
      new ServiceActionToInvoke (Agents.me.url, "proxy", new ActionItem("serve"), "via", Agents.me.url, "url", ati.makeActionUrl)
   }
}

