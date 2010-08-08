package razie

import com.razie.pub.base._
import com.razie.pub.comms._
import com.razie.agent._
import com.razie.assets._

/**
 * simple agent with asset management functionality.
 * 
 * this was here when i had a separate razassets project - needed to separate asset management
 * 
 * Use like: val agent = razie.AssetAgent (SimpleAgent.local(4446))
 * 
 * @author razvanc
 */
@NoStaticSafe
object AssetAgent {

   def apply (a:razie.SimpleAgent) = a.inContext[SimpleAgent] {
         
//      MutantPresentation.addPresentation(MutantPresentation.XMLDOC);

      a.register(new AssetService());
      a.register(new MetaService());
         
      // initialize the JS support - takes a while...
      razie.Threads.fork {
            // TODO ANOW reinstate this back when scala works
//          ScriptFactory.make(null, "1+2").eval(ScriptContext.Impl.global())
      }

     a
   }
   
}