/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.agent.test;

import razie.SimpleAgent;

import com.razie.agent.network.Devices;
import com.razie.agent.upnp.AgentUpnpService;
import com.razie.agent.webservice.TempAgentNetworkService;
import com.razie.pub.agent.Agent;
import com.razie.pub.base.ExecutionContext;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;

/**
 * test the light server
 * 
 * @author razvanc99
 */
class TestMultipleAgents extends TestAgentBase {

    // two agents in the same JVM, see if they connect to each-other without UPNP
    def testConnectingAgents() = {
    	val agents = razie.SimpleAgent.localCloud (List(4446, 4447)).toArray
        agents.foreach(_.onInit.onStartup)
        
        agents(0) inContext {
           // not actively maintaining cloud state
           agents(0).register(new TempAgentNetworkService(false));
    	}

        agents(1) inContext {
           // this will actively search and maintain the network too
           agents(1).register(new TempAgentNetworkService(true));
        }
        
        try {
            Thread.sleep(1000);

            // all agents in the group are up...?
            for (h <- razie.M (Agents.homeCloud().agents().values()))
            	junit.framework.Assert.assertTrue("must be up..." + h, h.status.equals(AgentHandle.DeviceStatus.UP));

        } finally {
            agents.foreach(_.onShutdown())
            agents.foreach(_.join)
        }
        Thread.sleep(1000);
    }

    // two agents in the same JVM, see if they connect to each-other via UPNP
    def testUpnpConnectingAgents() = {
    	val agents = razie.SimpleAgent.localCloud (List(4446, 4447))
        agents.foreach(_.onInit.onStartup)

        agents(0) inContext {
           agents(0).register(new AgentUpnpService(Devices.getInstance()));
           // not actively maintaining cloud state
           agents(0).register(new TempAgentNetworkService(false));
    	}

        agents(1).getContext().enter();
        agents(1).register(new AgentUpnpService(Devices.getInstance()));

        // not actively maintaining cloud state
        agents(1).register(new TempAgentNetworkService(false));
        ExecutionContext.exit();

        try {
            Thread.sleep(2000);

            // all agents in the group are up...?
            for (h <- razie.M (Agents.homeCloud().agents().values()))
            	junit.framework.Assert.assertTrue("must be up..." + h, h.status.equals(AgentHandle.DeviceStatus.UP));
        } finally {
            agents.foreach(_.onShutdown)
            agents.foreach(_.join)
        }
        Thread.sleep(1000);
    }
}
