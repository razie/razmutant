/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.agent.test;

import razie.SimpleAgent;
import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.agent.Agent;
import com.razie.pub.agent.test.TestAgentBase;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.ServiceActionToInvoke;

/**
 * test the light server
 * 
 * @author razvanc99
 */
public class TestControlService extends TestAgentBase {

   // create an agent, do something with it and destroy it
   public void testGetLog() throws InterruptedException {
      Agent agent = new SimpleAgent(me, new AgentCloud(me));
      agent.onInit().onStartup();
      try {
         ActionToInvoke a = new ServiceActionToInvoke(me.url, "control", new ActionItem("GetLog"), "howMany",
               "20");
         Object oo = a.act(null);
         Log.logThis("REPLY: " + oo.toString());
         assertTrue(oo.toString().contains("HTTP_SOA_GetLog"));
      } finally {
         agent.onShutdown();
         agent.join(); // will wait until it dies
      }
   }

   // create an agent, do something with it and destroy it
   public void testStatus() throws InterruptedException {
      Agent agent = new SimpleAgent(me, new AgentCloud(me));
      agent.onInit().onStartup();
      try {
         ActionToInvoke a = new ServiceActionToInvoke(me.url, "control", new ActionItem("Status"));
         Object oo = a.act(null);
         Log.logThis("REPLY: " + oo.toString());
         assertTrue(oo.toString().contains("OK"));
      } finally {
         agent.onShutdown();
         agent.join(); // will wait until it dies
      }
   }

   // create an agent, do something with it and destroy it
   public void DONTtestConfig() throws InterruptedException {
      // TODO 1-1 can't test this since the user.config is not loaded in the agent projcet...
      Agent agent = new SimpleAgent(me, new AgentCloud(me));
      agent.onInit().onStartup();

      // make sure the configuraqtion is loaded...
      try {
         ActionToInvoke a = new ServiceActionToInvoke(me.url, "control", new ActionItem("config"));
         Object oo = a.act(null);
         Log.logThis("REPLY: " + oo.toString());
         assertTrue(oo.toString().contains("OK"));
      } finally {
         agent.onShutdown();
         agent.join(); // will wait until it dies
      }
   }

   // create an agent, do something with it and destroy it
   public void testStop() throws InterruptedException {
      Agent agent = new SimpleAgent(other, new AgentCloud(other));
      agent.onInit().onStartup();

      try {
         ActionToInvoke a = new ServiceActionToInvoke(agent.getHandle().url, "control",
               new ActionItem("Stop"));
         Object oo = a.act(null);
         Log.logThis("REPLY: " + oo.toString());

         Thread.sleep(500);

         assertTrue(agent.hasStopped());
      } finally {
         agent.onShutdown();
         agent.join(); // will wait until it dies
      }
   }
}
