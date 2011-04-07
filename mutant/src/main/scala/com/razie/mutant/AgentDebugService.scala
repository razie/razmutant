package com.razie.mutant

import com.razie.pub.agent.AgentHttpService;

import com.razie.pub.agent.AgentService;

import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import com.razie.pub.base._
import razie.draw._
import razie.draw.widgets._

import com.razie.assets._

/** all kinds of debug tools */
@SoaService(name = "debug", descr = "agent debug service", bindings = Array("http") )
class AgentDebugService extends AgentService {

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

   @SoaMethod(descr = "print all http args from the client")
   def httpArgs () = 
      razie.Draw.attrList (
         ExecutionContext.instance().getAttr("httpattrs").asInstanceOf[razie.base.AttrAccess]
         )

    /** print a full thread dump on the stream */
   private def printstacktrace (out:DrawStream, excl:String):Unit = {
      val n = Thread.getAllStackTraces();

      out write("Stack Traces: \n\n");

      for (ei <- n.entrySet().toArray) {
        val e = ei.asInstanceOf[java.util.Map.Entry[Thread, Array[StackTraceElement]]]
        if (excl == null || !e.getKey.getName.contains(excl)) {
          out.write(e.getKey());
          out.write(new DrawText("\n"));
          for (s <- e.getValue()) {
            out.write(new DrawText("\t" + s.toString()));
            out.write(new DrawText("\n"));
          }
         
          out.write(new DrawText("\n"));
          }
        }

        out.write(new DrawText("\n"));
      }
    
}
