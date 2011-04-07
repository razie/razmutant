/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.agent.test;

import junit.framework.TestCase;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.LightAuthBase;

/**
 * test the light server
 * 
 * @author razvanc99
 */
public class TestAgentBase extends TestCase {
	public static AgentHandle me = new AgentHandle("testagent1", "localhost",
			"127.0.0.1", "4446", "http://localhost:4446");
	public static AgentHandle other = new AgentHandle("testagent2", "localhost",
			"127.0.0.1", "4447", "http://localhost:4447");

   @Override
	public void setUp() {
		LightAuthBase.init(new LightAuthBase("mutant"));
	}

	static final Log logger = Log.factory.create(TestAgentBase.class
			.getName());
}
