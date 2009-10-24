package com.razie.agent.network

import com.razie.pub.assets._
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
import com.razie.pub.util._
import com.razie.pub.agent._
import com.razie.pubstage.comms._


/** this is the actual implementation for assets Device which are laptop/desktop (can run an agent) 
 * @author razvanc
 */
class ComputerScala (ref:AssetKey, ttype:Computer.Type) extends ComputerImpl (ref,ttype) {
   // tired of the getXXX stuff, eh?
   def ip = getIp
   def port = getPort
   def name = getName

   override def render(t:Renderer.Technology , out:DrawStream ) : AnyRef = {
         super.render (t,out)

         val reply = new DrawList();
         out.open(reply);

         if (Computer.Type.LAPTOP.equals(this.getType()) || Computer.Type.DESKTOP.equals(this.getType())) {
            //TODO 1-1 when in the remote network, proxy automatically via the dyndns
            val homeurl = "http://" + ip + ":" + port + "/mutant"
            reply.write(new NavButton(new ActionItem("mutant", "mutant"), homeurl))
            // TODO 1-1 remove this test proxy
            reply.write(new NavButton(new ActionItem("proxied", "proxied"), razie.Agent.proxy(homeurl).makeActionUrl))
            
            reply.write(new ServiceActionToInvoke("control", Device.cmdUPGRATETO, "ip", ip))
            reply.write(new ServiceActionToInvoke("control", Device.cmdUPGRADEFROM, "ip", ip))
            reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdSTOP))
            reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdUPGRADE))
            reply.write(new ServiceActionToInvoke(Agents.agent(name).url, "control", Device.cmdDIE))
            reply.write(new AssetActionToInvoke(Agents.agent(name).url, ref, cmdCSCRIPT))
            reply.write(PageServices.methodButton(ref, meth("pubKey")))
            reply.write(PageServices.methodButton(ref, meth("resetSecurity")))
            reply.write(PageServices.methodButton(ref, meth("accept")))
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

   private def meth (name:String) = getClass().getMethods().find (_.getName().equals(name)) match { 
	   case Some(m) => m
	   case None => throw new IllegalArgumentException ("Method " + name + " not found")
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

   // TODO move to razplay and inject as valueadd
   @SoaMethod(descr = "generate local security codes", args=Array("password"))//TODO, perm = LightAuth.PermType.ADMIN)
   def resetSecurity (password:String) = {
      LightAuth.instance.resetSecurity (password)
   }

   // TODO move to razplay and inject as valueadd
   @SoaMethod(descr = "accept this device", args=Array("password"))
   def accept (password:String) = {
      if (this.getHandle().name == Agents.me().name) {
         new DrawError ("Cannot accept myself !")
      } else {
         // copy remote pub key
         val ati = new AssetActionToInvoke (this.getHandle().url, this.ref, new ActionItem("pubKey"))
         var pk = ati.act(null).toString
         // TODO remove this when pubKey returns text, see todo there
         pk =  HtmlContents.justBody(pk)
         LightAuth.instance.accept (password, this.getHandle, pk)
      }
   }

   // TODO move to razplay and inject as valueadd
   @SoaMethod(descr = "return my pubkey")
//   @SoaStreamable(mime = "application/text")
   def pubKey (/*out:DrawStream*/)= {
   //TODO use mime type when TODO in DeviceInventory.doAction is done...
      if (this.getHandle().name == Agents.me().name) {
         val pk = LightAuth.instance.pubkey (this.getHandle)
         pk
      } else {
    	  // TODO SECU protect this code
         new DrawError ("Cannot delegate this!").toString
      }
   }

   override def toString = if (this.handle != null) "handle="+this.handle.toString else "key="+this.ref.toString 

   final val cmdCSCRIPT      = new ActionItem("capturescript", RazIcons.UNKNOWN);
   final val cmdRSCRIPT      = new ActionItem("runscript", RazIcons.UNKNOWN);

}
