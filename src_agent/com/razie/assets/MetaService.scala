package com.razie.assets

import com.razie.pub.agent.AgentHttpService;

import com.razie.pub.agent.AgentService;

import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.agent._
import razie.draw._
import razie.draw.widgets._
import razie.draw.DrawTableScala

import razie._
import razie.assets._

import com.razie.assets._
import com.razie.pub.assets._
import scala.collection.JavaConversions

/** just a sample agent service written in SCALA */
@SoaService(name = "meta", bindings=Array("http"), descr = "access to meta descriptions" )
class MetaService extends AgentService {

   /** the second initialization phase: the agent is starting up */
   override def onStartup() : Unit = {
   }

   /** the agent needs to shutdown this service. You must join() all threads and return to agent. */
   override def onShutdown() : Unit = {
   }

   @SoaMethod(descr = "list all objects managed in this app")
   def objects() =  
      razie.Draw attrTable AssetMgr.metas.toList.sort(_<_).map(razie.AA ("name", _))
    
   @SoaMethod(descr = "list all objects managed in this app", args = Array("format" ))
   @SoaStreamable
   def classes(out:DrawStream, format:String) =  {
      format match {
         case "json" => {
            val discard = new MimeDrawStream (out, "application/json", Technology.JSON)
            for (val s <- AssetMgr.metas.toList.sort(_<_)) 
               out.write(Metas.meta(s).toAA.toJson(null).toString)
         }
         case "xml" => {
            val discard = new MimeDrawStream (out, "application/xml", Technology.XML)
            for (val s <- AssetMgr.metas.toList.sort(_<_)) 
               out.write(Metas.meta(s).toDetailedXml())
         }
         case _ => {
      out write Draw.seq (
         Draw.list (
               Draw.link(AI("asJSON"), Service("meta").action("classes", AA("format", "json"))),
               Draw.link(AI("asXML"), Service("meta").action("classes", AA("format", "xml")))
               ),
         Draw.attrTable (
            for (val s <- AssetMgr.metas.toList.sort(_<_)) yield {
               val m = Metas.meta(s)
               val details = Draw.link(AI("details"), Service("meta").action("details", AA("name", m.id)))
               razie.AA ("name", Draw.link(m.id, Service ("meta").action("details", AA("name", m.id))), "inventory", m.inventory, "details", details)
            }
         )
      )
         }
      }
   }
    
   @SoaMethod(descr = "list all services")
   def services() = 
      razie.Draw attrTable (
         for (val s <- (razie.M apply com.razie.pub.agent.Agent.instance().copyOfServices()).sort(_.getClass().getSimpleName()<_.getClass().getSimpleName())) yield
            AA ("name", s.getClass().getSimpleName(), "class", s.getClass().getName())
     )

   @SoaMethod(descr = "details of a meta", args = Array( "name", "format" ))
   @SoaStreamable
   def details (out:DrawStream, name:String, format:String) =  {
      // TODO nicer tech switch
      val m = AssetMgr.meta(name)
      
      (format, m) match {
         case ("json", Some(meta)) => {
            val discard = new MimeDrawStream (out, "application/json", Technology.JSON)
            out.write(meta.toAA.toJson(null).toString)
         }
         case ("xml", Some(meta)) => {
            val discard = new MimeDrawStream (out, "application/xml", Technology.XML)
            out.write(meta.toDetailedXml())
         }
         case (_, Some(meta)) => {
            out write new NavButton(meta.id, "/mutant/asset/" + meta.id.name)
            out write new NavButton(razie.AI("as json"), razie.Service("meta").action("details", razie.AA("name", meta.id.name, "format", "json")))
            out write new NavButton(razie.AI("as xml"), razie.Service("meta").action("details", razie.AA("name", meta.id.name, "format", "xml")))
         }
      }
   }
    
   @SoaMethod(descr = "1 parm streamable", args = Array( "msg" ))
   @SoaStreamable
   def Method3 (out:DrawStream, msg:String):Unit = out write ("SCALA Method3 msg="+msg)
    
}
