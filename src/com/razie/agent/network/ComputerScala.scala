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
import com.razie.secu._
import com.razie.pub.util._
import com.razie.pub.agent._


class ComputerScala (ref:AssetKey, ttype:Computer.Type) extends Computer.Impl (ref,ttype) {
   // tired of the getXXX stuff, eh?
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

   def meth (name:String) = {
      (for (m <- getClass().getMethods(); if (m.getName().equals(name)))
         yield m).head
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

   @SoaMethod(descr = "generate local security codes", args=Array("password"))//TODO , perm = LightAuth.PermType.ADMIN, args=Array())
   def resetSecurity (password:String) = {
      val ks = KS.create(Agents.me().localdir + "/keys", password)
      val t = KS.genKeys()
      ks.store (password, Agents.me.name, t._1, t._2) 
      LightAuth.init(new SecuLightAuth(Agents.me().localdir + "/keys", "mutant", password))
      "Ok - keys regenerated..."
   }

   @SoaMethod(descr = "accept this device", args=Array("password"))//TODO , perm = LightAuth.PermType.ADMIN, args=Array())
   def accept (password:String) = {
      if (this.getHandle().name == Agents.me().name) {
         new DrawError ("Cannot accept myself !")
      } else {
         // copy remote pub key
         val ati = new AssetActionToInvoke (this.getHandle().getLocation().toHttp(), this.ref, new ActionItem("pubKey"))
         val pk = ati.act(null).toString
         val bytes = Base64.decode (pk)
         val ks = KS.load(Agents.me().localdir + "/keys", password)
         ks.store (password,this.getHandle().name, null, KS.pubKeyFromBytes(bytes))
         "Ok - stored public key for " + this.getHandle().name + " at " + this.getHandle.url + " ..."
      }
   }

   @SoaMethod(descr = "accept this device", args=Array("password"))//TODO , perm = LightAuth.PermType.ADMIN, args=Array())
   //  @SoaStreamable(streamMimeType = "application/text")
   def pubKey (/*out:DrawStream, */password:String):String = {
      if (this.getHandle().name == Agents.me().name) {
         Base64.encodeBytes(KS.load(Agents.me().localdir + "/keys", password).loadKeys (password, this.getHandle().name)._2.getEncoded())
      } else {
         new DrawError ("Cannot delegate this!").toString
      }
   }

   @SoaMethod(descr = "accept this device", args=Array("password"))//TODO , perm = LightAuth.PermType.ADMIN, args=Array())
   //@SoaStreamable(streamMimeType = "application/text")
   def sign (/*out:DrawStream, */password:String):String = {
      if (this.getHandle().name == Agents.me().name) {
         Base64.encodeBytes(KS.load(Agents.me().localdir + "/keys", password).loadKeys (password, this.getHandle().name)._2.getEncoded())
      } else {
         new DrawError ("Cannot delegate this!").toString
      }
   }

   override def toString = if (this.handle != null) "handle="+this.handle.toString else "key="+this.ref.toString 

   final val cmdCSCRIPT      = new ActionItem("capturescript", RazIcons.UNKNOWN);
   final val cmdRSCRIPT      = new ActionItem("runscript", RazIcons.UNKNOWN);

}
