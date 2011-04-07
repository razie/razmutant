package com.razie.playground.spider;

import razie.draw.DrawStream;

import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.lightsoa.SoaStreamable;

/**
 * asset management
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaService(name = "spider", bindings = { "http" }, descr = "ecosystem for the beings and daemons")
public class AgentSpiderService extends AgentService {

   public AgentSpiderService() {
   }

   protected void onStartup() {
   }

   protected void onShutdown() {
   }

   @SoaMethod(descr = "spider")
   public Object spider(String urlbase, String where, String urlstart, String howmany) {
      Spider sp = new Spider(urlbase, where, urlstart, Integer.parseInt(howmany));
      int s = sp.spider();
      return "OK ... spidered " + s + " pages.";
   }

   @SoaMethod(descr = "", args = { "url" })
   @SoaStreamable
   public void serve(DrawStream out, String url) {
   }

   public static void main(String[] argv) {
      // AgentSpiderService ass=new AgentSpiderService();
      // ass.spider("", "c:/video/razmutant/spider/x1", urlstart, howmany)
   }

   static final Log logger = Log.factory.create("", AgentSpiderService.class.getName());
}
