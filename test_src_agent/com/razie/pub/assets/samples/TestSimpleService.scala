package com.razie.pub.assets.samples

import com.razie.pub.lightsoa._
import com.razie.pub.agent._
import com.razie.pub.agent.test._
import com.razie.agent._
import com.razie.assets._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.pub.base.log._
import com.razie.pub.comms._
import com.razie.pub.assets._
import razie.actionables._
import razie.actionables.library._
import razie.draw._
import org.scalatest.junit._

/** simplest service example */
@SoaService (name="simplest", descr="simplest service", bindings=Array("http"))
class SimplestService extends AgentService {
   @SoaMethod (descr="well, it says \"hello\"")	
   @SoaStreamable (mime="text") // otherwise it gets wrapped in an html <body> with CSS and everything
   def sayhi (out:DrawStream) = out write "hello"
}

/** test asset definition samples, complete with bindings */
class TestSimpleService extends JUnit3Suite {

   var agent:razie.SimpleAgent = null

   override def setUp = {
      ExecutionContext.resetJVM
      LightAuth.init()
      agent = razie.SimpleAgent.local("4446").onInit
      agent register new SimplestService
   }

   // local call - you need to be in the same context to be local
   def test1 = expect ("hello") {
      agent inContext {
         val action = razie.Service ("simplest").action ("sayhi")
        (action act null).asInstanceOf[String]
      }
   }

   // remote call
   def test2 = expect ("hello") {
      val action = razie.Service (agent.getHandle, "simplest").action ("sayhi")
      (action act null).asInstanceOf[String]
   }

   // remote call
   def test3 = expect ("hello") {
      val action = new ServiceActionToInvoke (agent.getHandle.url, "simplest", razie.AI("sayhi"))
	  (action act null).asInstanceOf[String]
  }

   // remote call
   def test4 = expect ("hello") {
      val url = "http://localhost:4446/mutant/simplest/sayhi"
      Comms.readUrl(url)
  }

   override def tearDown = {
      agent.onShutdown
      agent.join
      super.tearDown
   }
}

object MainTestSimpleService {
   def main (argv:Array[String]) {
      val t = new TestSimpleService
      try {
         t.setUp
         t.test1; t.test2; t.test3; t.test4
      } catch {
         case e:Exception => com.razie.pub.base.log.Log.alarmThis ("?", e)
      } finally {
         t.tearDown
      }
   }
}