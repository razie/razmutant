package com.razie.pub.agent.test

import com.razie.pub.lightsoa._
import com.razie.pub.agent._
import com.razie.pub.base._
import com.razie.pub.base.log._
import com.razie.pub.comms._
import razie.draw._
import org.scalatest.junit._

/** simplest service example */
@SoaService (name="simplest", descr="sayhi service", bindings=Array("http"))
class SayhiService extends AgentService {
   @SoaMethod (descr="well, it says \"hello\"") 
   @SoaStreamable (mime="text") // otherwise it gets wrapped in an html <body> with CSS and everything
   def sayhi (out:DrawStream) = out write "hello"
}

object AgentPerfTestMain extends Application {
   
      var agent:Agent = null
         
      try {
   
         // setup environment
         ExecutionContext.resetJVM
         LightAuthBase.init(new LightAuthBase("mutant"))
         
         // start an agent, register service
         agent = razie.SimpleAgent.local("4446").onInit.onStartup
         agent register new SayhiService

         println ("* Warmup *")

         razie.Perf.perf (1::2::Nil, 4) ( 
            (thread:Int, loop:Int) => { 
               for (i <- razie.Range.excl(0, 10)) 
                  Comms.readUrl("http://localhost:4446/mutant/simplest/sayhi")
               } 
            )
         
         println ("* Actual run *")
         
         razie.Perf.perf (1::2::5::10::Nil, 100) { 
            (thread:Int, loop:Int) => { 
               for (i <- razie.Range.excl(0, 10)) 
                  Comms.readUrl("http://localhost:4446/mutant/simplest/sayhi")
               } }
         
      } catch {
         case e:Exception => Log.logThis("?", e)
      } finally {
         agent.onShutdown.join
      }
}

object AgentPersistentPerfTestMain extends Application {
   
      var agent:Agent = null
         
      try {
   
         // setup environment
         ExecutionContext.resetJVM
         LightAuthBase.init(new LightAuthBase("mutant"))
         
         // start an agent, register service
         agent = razie.SimpleAgent.local("4446").onInit.onStartup
         agent register new SayhiService

         println ("* Warmup *")

//         razie.Perf.perf (1::Nil, 4) ( 
//            (thread:Int, loop:Int) => { 
               val con = new UrlConn ("http://localhost:4446/mutant/simplest/sayhi")
               for (i <- razie.Range.excl(0, 10)) 
                  println ("got: "+con.get("/mutant/simplest/sayhi"))
//               } 
//            )
         
         println ("* Actual run *")
         
//         razie.Perf.perf (1::2::5::10::Nil, 100) { 
//            (thread:Int, loop:Int) => { 
//               for (i <- razie.Range.excl(0, 10)) 
//                  Comms.readUrl("http://localhost:4446/mutant/simplest/sayhi")
//               } }
         
      } catch {
         case e:Exception => Log.logThis("?", e)
      } finally {
         agent.onShutdown.join
      }
}
