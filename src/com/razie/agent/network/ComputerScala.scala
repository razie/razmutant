package com.razie.agent.network

import com.razie.pub.assets._
import com.razie.agent.network.Computer.Impl
import com.razie.agent.network.Computer.Type
import com.razie.agent.network._

import com.razie.pub.resources._
import com.razie.pub.lightsoa._
import com.razie.pub.draw._
import com.razie.pub.draw.widgets._
import com.razie.pub.comms._
import com.razie.pub.comms.LightAuth.PermType
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.media._


class ComputerScala (ref:AssetKey, ttype:Computer.Type) extends Computer.Impl (ref,ttype) {
  def ip = getIp
  def port = getPort
  def name = getName

  override def render(t:Renderer.Technology , out:DrawStream ) : AnyRef = {
    super.render (t,out)
        
    val reply = new DrawList();
    out.open(reply);

    if (Computer.Type.LAPTOP.equals(this.getType()) || Computer.Type.DESKTOP.equals(this.getType())) {
      reply.write(new NavButton(new ActionItem("mutant", "mutant"), "http://" + ip + ":" + port
                     + "/mutant"))
      reply.write(new ServiceActionToInvoke("control", Device.cmdUPGRATETO, "ip", ip))
      reply.write(new ServiceActionToInvoke("control", Device.cmdUPGRADEFROM, "ip", ip))
      reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdSTOP))
      reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdUPGRADE))
      reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdDIE))
      reply.write(new AssetActionToInvoke(Agents.agent(name).url, ref, cmdCSCRIPT))
    }

    reply.close();

         // TODO inject this
         out.write("Folders:");
         val folders = new DrawTable(0, 3);
         out.open(folders);
         MediaServerService.browseFolders(name, folders);
         folders.close();
                                            
    return null;
    }

  
  @SoaMethod(descr = "main scripting interface") // TODO, perm = PermType.ADMIN)
  def capturescript () = {
	  val f = new DrawForm ( 
			  cmdRSCRIPT, 
			  new AssetActionToInvoke(Agents.agent(name).url, ref, cmdRSCRIPT),
			  new AttrAccess.Impl ("language:String=scala,script:script=<write here>"))
	  f
  }


  @SoaMethod(descr = "run a given script on the given machine", args=Array("language", "script"))//TODO , perm = LightAuth.PermType.ADMIN)
  def runscript (language:String,script:String) = {
      val scr = ScriptFactory.make(language, script)
      val res = scr.eval(ScriptContext.Impl.global()).toString()
//      HtmlRenderUtils.textToHtml(res)
      res
  }

  final val cmdCSCRIPT      = new ActionItem("capturescript", RazIcons.UNKNOWN);
   final val cmdRSCRIPT      = new ActionItem("runscript", RazIcons.UNKNOWN);
}
