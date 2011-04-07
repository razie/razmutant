/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details.
 */
package com.razie.pub.agent.test;

import com.razie.pub.agent.Agent;
import com.razie.pub.agent.TempUtilAgent;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.LightAuth;
import com.razie.pub.comms.LightAuthBase;

/**
 * test the light server
 * 
 * @author razvanc99
 */
public class SampleMultipleAgents {
    static AgentHandle me    = new AgentHandle("testagent1", "localhost", "127.0.0.1", "4445",
                                     "http://localhost:4445");
    static AgentHandle other = new AgentHandle("testagent2", "localhost", "127.0.0.1", "4446",
                                     "http://localhost:4446");

    public void setUp() {
        LightAuthBase.init(new LightAuthBase("lightsoa"));
    }

    // two agents in the same JVM, see if they connect to each-other
    public void testConnectingAgents() {
        AgentCloud group = new AgentCloud();
        group.put(me);
        group.put(other);
        Agent agent1 = TempUtilAgent.startAgent(me, group);
        Agent agent2 = TempUtilAgent.startAgent(other, group);

        // ...

        agent1.onShutdown();
        agent2.onShutdown();
    }

    static final Log logger = Log.factory.create(SampleMultipleAgents.class.getName());
}
