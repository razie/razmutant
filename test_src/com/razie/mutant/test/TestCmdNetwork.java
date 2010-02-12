package com.razie.mutant.test;

import java.io.IOException;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.ServiceActionToInvoke;

/**
 * test the light server
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class TestCmdNetwork extends TestMutant {

    public void testNetwork() throws IOException, InterruptedException {
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "network", new ActionItem("Network"));
        String result = (String) action.act(null);

        assertTrue(result, result.contains(Agents.getMyHostName()));
    }

    public void testDeviceInfo() throws IOException, InterruptedException {
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "network", new ActionItem("deviceInfo"),
                "ref", ref("%3Cmutant%3A%2F%2FDevice%3ATorLp-Razvanc%40null%3E"));
        String result = (String) action.act(null);

        assertTrue(result, result.contains(Agents.getMyHostName()));
    }

    public void testStatus() throws IOException, InterruptedException {
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "control", new ActionItem("Status"));
        String result = (String) action.act(null);

        assertTrue(result, result.contains("running OK"));
    }

    public void testFirstPing() throws IOException, InterruptedException {
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "network", new ActionItem("firstping"));
        String result = (String) action.act(null);
        logger.log("RESULT of PING: " + result);
        
        assertTrue(result, result.contains(Agents.getMyHostName()));
    }

    public void testPing() throws IOException, InterruptedException {
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "network", new ActionItem("ping"));
        String result = (String) action.act(null);
        logger.log("RESULT of PING: " + result);
        
        assertTrue(result, result.contains(Agents.getMyHostName()));
    }

    static final Log logger = Log.Factory.create(TestCmdNetwork.class.getName());
}
