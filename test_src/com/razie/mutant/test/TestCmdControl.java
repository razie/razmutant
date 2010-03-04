package com.razie.mutant.test;

import java.io.IOException;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ServiceActionToInvoke;

/**
 * test the light server
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class TestCmdControl extends TestMutant {

    public void testCmdGetLog() throws IOException, InterruptedException {
        // send echo command
        ActionToInvoke action = new ServiceActionToInvoke(MYURL, "control", new ActionItem("GetLog"));
        String result = (String) action.act(null);

        assertTrue(result, result.contains("HTTP_CLIENT_IP"));
    }

    static final Log logger = Log.factory.create(TestCmdControl.class.getName());
}
