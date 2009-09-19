package com.razie.mutant

import com.razie.pub.agent.AgentHttpService;

import com.razie.pub.agent.AgentService;

import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import com.razie.pub.draw._
import com.razie.pub.draw.widgets._

import com.razie.assets._

/** just a sample agent service written in SCALA */
//TODO @SoaService(){val name = "debug", val descr = "agent debug service" }
@SoaService(name = "debug", descr = "agent debug service")
class AgentDebugService extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("SCALA service onstartup");

      AgentHttpService.registerSoa(new HttpSoaBinding(this));
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("SCALA service onshutdown");
    }

    /** print a full thread dump on the stream */
   private def printstacktrace (out:DrawStream, excl:String):Unit = {
      val n = Thread.getAllStackTraces();

      out write("Stack Traces: \n\n");

      for (val ei <- n.entrySet().toArray) {
        val e = ei.asInstanceOf[java.util.Map.Entry[Thread, Array[StackTraceElement]]]
        if (excl == null || !e.getKey.getName.contains(excl)) {
          out.write(e.getKey());
          out.write(new DrawText("\n"));
          for (val s <- e.getValue()) {
            out.write(new DrawText("\t" + s.toString()));
            out.write(new DrawText("\n"));
          }
         
          out.write(new DrawText("\n"));
          }
        }

        out.write(new DrawText("\n"));
      }

    
   @SoaMethod(descr = "produce stack trace")
   @SoaStreamable
   def stacktrace (out:DrawStream):Unit = {
     printstacktrace (out, null)
   }

   @SoaMethod(descr = "stack trace without known threads")
   @SoaStreamable
   def stacktraceuser (out:DrawStream):Unit = {
     printstacktrace (out, "Cyber")
   }

    
}
