/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.razscala.pub.agent.test;

import com.razie.pub.agent.Agent;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ActionToInvoke;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.agent.test.TestAgentBase;

/**
 * test the light server
 * 
 * @author razvanc99
 */
class TestSimpleServices extends TestAgentBase {
	// create an agent, do something with it and destroy it
	def testAutoboundScalaService() = {
		val group = new AgentCloud();
  
      val m=TestAgentBase.me;
        
		group.put(m);
		val agent = TestAgentBase.startAgent(m, group);

		try {
		//	val svc = new com.razie.razscala.pub.agent.AutoBoundScalaService();
//			agent.register(svc);
			Thread.sleep(500);

			val a = new ServiceActionToInvoke(m.url, "testservice",
					new ActionItem("echo"));
//			val oo = a.exec(null);
	//		Log.logThis("REPLY: " + oo.toString());
			//assertTrue(oo.toString().contains(svc.ECHOMSG()));
		} finally {
			agent.onShutdown();
		}

		Thread.sleep(500);
	}

}
