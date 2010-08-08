package com.razie.play.proxy.test

import razie.base._
import org.scalatest.junit._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.valueadd.security._
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

/** simplest service example */
@SoaService (name="proxied", descr="all kinds of content to test proxies", bindings=Array("http"))
class ProxiedService extends AgentService {
   @SoaMethod (descr="buttons and links")
   def buttons = {
      val reply = new DrawList();
      val homeurl = "http://" + Agents.me.ip + ":" + Agents.me.port + "/mutant"
      reply.write(new NavButton(new ActionItem("mutant", "mutant"), homeurl))
      reply.write(new ServiceActionToInvoke("control", new ActionItem ("Status", RazIcons.STOP.name) ))
      reply      
      "kuku"
   }
   
   @SoaMethod (descr="buttons and links")
   def kuku = {
      "kuku"
   }
   
   @SoaMethod (descr="content") 
   @SoaStreamable (mime="text") // otherwise it gets wrapped in an html <body> with CSS and everything
   def text (out:DrawStream) = out write "hello"
}
