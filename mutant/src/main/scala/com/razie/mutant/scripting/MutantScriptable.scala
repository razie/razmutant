/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.mutant.scripting

import com.razie.pub.agent._
import com.razie.agent.webservice._
import com.razie.pub.assets._
import com.razie.pub.base._
import com.razie.pub.base.data._
import razie.draw.widgets._
import com.razie.pub.lightsoa._
import razie.assets._
import razie.base._
import razie.base.scripting._

/** this is present in the scriptable contexts as "mutant" - it is a collection of all mutant capability in regards to scripting. 
 
 TODO should probably be broken into litle services?
 
 TODO SECURITY RISK - set all permissions to ADMIN, when the scala compiler can compile that
 */
@SoaService(name = "scriptable", descr = "sample scala service", bindings=Array("http"))
class MutantScriptable extends AgentService {

    /** the second initialization phase: the agent is starting up */
    override def onStartup() : Unit = {
      println("mutant scriptable onstartup");
    }

    /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
    override def onShutdown() : Unit = {
      println("mutant scriptable onshutdown");
    }
  
  @SoaMethod(descr = "copy file FROM remote", args = Array( "host", "src", "dest", "shouldMove" ))
  def copyFrom (host:String, src:String, dest:String, shouldMove:String) = {
    CmdFilesClient.copyFrom(AssetLocation.mutantEnv(host, ""), src, dest);
  }
  
  @SoaMethod(descr = "copy file TO remote", args = Array( "host", "src", "dest", "shouldMove" ))
  def copyTo (host:String, src:String, dest:String, shouldMove:String) = {
    CmdFilesClient.copyTo(src, AssetLocation.mutantEnv(host, ""), dest);
  }
  
  @SoaMethod(descr = "copy file locally", args = Array( "src", "dest", "shouldMove" ))
  def copyLocal (src:String, dest:String, shouldMove:String) = {
    CmdFilesClient.copyLocal(src, dest);
  }
  
  @SoaMethod(descr = "execute OS command", args = Array( "cmd" ))
  def cmd (cmd:String) = {
    //TODO implement
  } 

  @SoaMethod(descr = "execute OS command on remote host", args = Array( "host", "cmd" ))
  def rcmd (host:String, cmd:String) = {
    //TODO implement
  } 
  
  @SoaMethod(descr = "execute script", /*perm=LightAuth.PermType. ADMIN, */args = Array( "script" ))
  def script (script:String) = {
    val scr = ScriptFactory.make(null, script);
    new DrawToString(scr.eval(ScriptFactory.mkContext(scr.lang)));
  } 

  @SoaMethod(descr = "execute script on remote host", args = Array( "host", "script" ))
  def rscript (host:String, script:String) = {
    //TODO implement
  } 
    
//  @SoaMethod(descr = "get a simple list of services")
//  def services () = {
//     val list = for (s <- RazElement.tolist(Agent.instance().copyOfServices()).sort(_.getClass().getSimpleName()<_.getClass().getSimpleName())) yield s.getClass.getSimpleName()
//  } 
    
}
